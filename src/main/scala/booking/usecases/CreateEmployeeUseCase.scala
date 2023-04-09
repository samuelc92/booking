package booking.usecases

import booking.repositories.{EmployeeRepositoryAlgebra, EmployeeSchedule, EmployeeScheduleRepositoryAlgebra}
import booking.valueobjects.Period
import booking.entities.*
import booking.dtos.*
import booking.usecases.CreateEmployeeUseCase.CreateEmployeeUseCaseImpl.*

import scala.annotation.tailrec

import zio.*

import zio.json.*
import sttp.tapir.Schema

trait CreateEmployeeUseCase:
  def execute(request: CreateEmployeeRequest): ZIO[Any, Nothing, Unit]

object CreateEmployeeUseCase {
  lazy val layer: ZLayer[EmployeeRepositoryAlgebra, Throwable, CreateEmployeeUseCase] = ZLayer {
    for {
      employeeRepository <- ZIO.service[EmployeeRepositoryAlgebra]
    } yield CreateEmployeeUseCaseImpl(employeeRepository)
  }

  final case class CreateEmployeeUseCaseImpl(
    employeeRepository: EmployeeRepositoryAlgebra
  ) extends CreateEmployeeUseCase {

    override def execute(request: CreateEmployeeRequest): ZIO[Any, Nothing, Unit] =
      for {
        _ <- employeeRepository
          .create(request.toEntity)
          .tapError(e => ZIO.logError(s"Error on inserting an employee. Error: $e"))
          .orDie
      } yield () 
  } 
}
/*
object CreateEmployeeUseCase:
  def apply(employeeRepository: EmployeeRepositoryAlgebra,
            employeeScheduleRepository: EmployeeScheduleRepositoryAlgebra): CreateEmployeeUseCase =
    new CreateEmployeeUseCase(employeeRepository, employeeScheduleRepository)

class CreateEmployeeUseCase(employeeRepository: EmployeeRepositoryAlgebra,
                            employeeScheduleRepository: EmployeeScheduleRepositoryAlgebra):

  def execute(createEmployeeRequest: CreateEmployeeRequest): IO[Either[Throwable, Int]] =
    IO.pure(isRequestValidate(createEmployeeRequest.scheduler)) flatMap { result =>
      result match {
        case Right(_) =>
          for {
            employeeEither <- employeeRepository.create(Employee(0, createEmployeeRequest.fullName))
            result <- createEmployeeSchedule(employeeEither.getOrElse(0))(createEmployeeRequest.scheduler)
          } yield result
        case Left(value) => IO.pure(Left(value))
      }
    }

  private def createEmployeeSchedule(employeeId: Int)(scheduler: Seq[CreateEmployeeScheduleRequest]): IO[Either[Throwable, Int]] =
    employeeScheduleRepository create scheduler.map(mapToEmployeeSchedule(employeeId))

  private def mapToEmployeeSchedule(employeeId: Int)(employeeScheduleRequest: CreateEmployeeScheduleRequest) =
    EmployeeSchedule(
      employeeId,
      employeeScheduleRequest.day,
      employeeScheduleRequest.startTime1,
      employeeScheduleRequest.endTime1,
      employeeScheduleRequest.startTime2,
      employeeScheduleRequest.endTime2
    )

  @tailrec
  private def isRequestValidate(request: Seq[CreateEmployeeScheduleRequest]): Either[Throwable, Boolean] =
    if (request.isEmpty) Right(true)
    else
      val employeeScheduleRequest = request.head
      val t = for {
        _ <- isTimeValid(employeeScheduleRequest.startTime1)
        result <- isTimeValid(employeeScheduleRequest.endTime1)
      } yield (result)
      t match {
        case Right(value) => isRequestValidate(request.tail)
        case Left(error) => Left(error)
      }

  private def isTimeValid(time: String): Either[Throwable, Boolean] =
    if (time.split(":").length <= 1) Left(IllegalArgumentException("Invalid value"))
    else
      time.split(":")(0).toIntOption match {
        case Some(_) =>
          time.split(":")(1).toIntOption match {
            case Some(_) => Right(true)
            case None => Left(IllegalArgumentException("Invalid value"))
          }
        case None => Left(IllegalArgumentException("Invalid value"))
      }
      */
