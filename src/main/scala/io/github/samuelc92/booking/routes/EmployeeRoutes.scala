package io.github.samuelc92.booking.routes

import io.github.samuelc92.booking.entities.*
import io.github.samuelc92.booking.usecases.*

import zio.*

import sttp.apispec.openapi.circe.yaml.*
import sttp.model.StatusCode
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.json.zio.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.swagger.SwaggerUI
import sttp.tapir.ztapir.*
import zhttp.http.{Http, HttpApp, Request, Response}

trait EmployeeRoutes:
  def httApp: ZIO[Any, Nothing, HttpApp[Any, Throwable]]

object EmployeeRoutes:

  lazy val layer: ZLayer[CreateEmployeeUseCase, Nothing, EmployeeRoutes] = ZLayer {
    for {
      useCase <- ZIO.service[CreateEmployeeUseCase]
    } yield EmployeeRoutesImpl(useCase)
  }

  def httApp: ZIO[EmployeeRoutes, Nothing, HttpApp[Any, Throwable]] =
    ZIO.serviceWithZIO[EmployeeRoutes](_.httApp)

final case class EmployeeRoutesImpl(useCase: CreateEmployeeUseCase) extends EmployeeRoutes:

  private val baseEndpoint = endpoint.in("api").in("v1").in("employees")

  private val exampleEmployee = Employee("Test")
  private val employeeBody = jsonBody[Employee].example(exampleEmployee)
  private val employeeOutBody = jsonBody[Employee].example(exampleEmployee)

  private val postEmployeeEndpoint =
    baseEndpoint.post
      .in(employeeBody)
      .out(employeeOutBody)

  private val postEmployeeRoute =
    postEmployeeEndpoint.zServerLogic { case employee =>
      useCase.execute(employee)
    }

  private val allRoutes: Http[Any, Throwable, Request, Response] =
    ZioHttpInterpreter().toHttp(List(
      postEmployeeEndpoint.zServerLogic(request => useCase.execute(request))))

  private val endpoints = {
    val endpoints = List(
      postEmployeeEndpoint
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