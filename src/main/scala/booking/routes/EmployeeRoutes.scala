package booking.routes

import booking.entities.*
import booking.dtos.*
import booking.usecases.*
import booking.Error

import zio.*

import sttp.apispec.openapi.circe.yaml.*
import sttp.model.StatusCode
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.json.zio.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.swagger.SwaggerUI
import sttp.tapir.ztapir.*
import zhttp.http.{Http, HttpApp, Request, Response}
import zhttp.http.Status.NoContent
import java.time.LocalTime

trait EmployeeRoutes:
  def httApp: ZIO[Any, Nothing, HttpApp[Any, Throwable]]

object EmployeeRoutes:

  lazy val layer: ZLayer[CreateEmployeeUseCase & GetEmployeeUseCase, Nothing, EmployeeRoutes] = ZLayer {
    for {
      useCase    <- ZIO.service[CreateEmployeeUseCase]
      getUseCase <- ZIO.service[GetEmployeeUseCase] 
    } yield EmployeeRoutesImpl(
      useCase,
      getUseCase)
  }

  def httApp: ZIO[EmployeeRoutes, Nothing, HttpApp[Any, Throwable]] =
    ZIO.serviceWithZIO[EmployeeRoutes](_.httApp)

final case class EmployeeRoutesImpl(
  useCase: CreateEmployeeUseCase,
  getUseCase: GetEmployeeUseCase) extends EmployeeRoutes {

  private val baseEndpoint = endpoint.in("api").in("v1").in("employees")

  private val exampleEmployeeScheduler = Seq(
    CreateEmployeeScheduleRequest("MONDAY", LocalTime.now(), LocalTime.now(), LocalTime.now(), LocalTime.now())
  )
  private val exampleEmployee = CreateEmployeeRequest("Test", exampleEmployeeScheduler)
  private val employeeBody = jsonBody[CreateEmployeeRequest].example(exampleEmployee)
  private val employeeOutBody = jsonBody[CreateEmployeeRequest].example(exampleEmployee)

  private val exampleGetEmployee = GetEmployeeResponse(0, "Test")
  private val getEmployeeOutBody = jsonBody[GetEmployeeResponse].example(exampleGetEmployee)

  private val exampleGetAll = List(GetEmployeeResponse(0, "Test"))
  private val getAllOutBody = jsonBody[List[GetEmployeeResponse]].example(exampleGetAll)

  private val getEmployeeErrorOut = oneOf[Error](
    oneOfVariant(StatusCode.NotFound, jsonBody[Error.NotFound].description("Employee was not found."))
  )

  private val postEmployeeEndpoint =
    baseEndpoint.post
      .in(employeeBody)

  private val getEmployeeEndpoint =
    baseEndpoint.get
      .in(path[Int]("id"))
      .out(getEmployeeOutBody)
      .errorOut(getEmployeeErrorOut)

  private val getAllEndpoint =
    baseEndpoint.get
      .out(getAllOutBody)

  private val postEmployeeRoute =
    postEmployeeEndpoint.zServerLogic { case employee =>
      useCase.execute(employee)
    }

  private val allRoutes: Http[Any, Throwable, Request, Response] =
    ZioHttpInterpreter().toHttp(List(
      postEmployeeEndpoint.zServerLogic(request => useCase.execute(request)),
      getEmployeeEndpoint.zServerLogic(id => getUseCase.getById(id)),
      getAllEndpoint.zServerLogic(_ => getUseCase.getAll)))

  private val endpoints = {
    val endpoints = List(
      postEmployeeEndpoint,
      getEmployeeEndpoint,
      getAllEndpoint
    )
    endpoints.map(_.tags(List("Employee endpoints")))
  }

  override def httApp: ZIO[Any, Nothing, HttpApp[Any, Throwable]] = {
    for {
      openApi       <- ZIO.succeed(OpenAPIDocsInterpreter().toOpenAPI(endpoints, "Booking Service", "0.1"))
      routesHttp    <- ZIO.succeed(allRoutes)
      endPointsHttp <- ZIO.succeed(ZioHttpInterpreter().toHttp(SwaggerUI[Task](openApi.toYaml)))
    } yield routesHttp ++ endPointsHttp
  }
}