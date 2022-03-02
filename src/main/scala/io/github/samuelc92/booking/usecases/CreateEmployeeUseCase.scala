package io.github.samuelc92.booking.usecases

import cats.effect.IO
import io.github.samuelc92.booking.repositories.{Employee, EmployeeRepositoryAlgebra, EmployeeSchedule, EmployeeScheduleRepositoryAlgebra}

import java.util.UUID
import scala.annotation.tailrec

final case class CreateEmployeeRequest(fullName: String, scheduler: Seq[CreateEmployeeScheduleRequest])
final case class CreateEmployeeScheduleRequest(day: String, startTime1: String, endTime1: String, startTime2: String, endTime2: String)

object CreateEmployeeUseCase:
  def apply(employeeRepository: EmployeeRepositoryAlgebra,
            employeeScheduleRepository: EmployeeScheduleRepositoryAlgebra): CreateEmployeeUseCase =
    new CreateEmployeeUseCase(employeeRepository, employeeScheduleRepository)

class CreateEmployeeUseCase(employeeRepository: EmployeeRepositoryAlgebra,
                            employeeScheduleRepository: EmployeeScheduleRepositoryAlgebra):

  def execute(createEmployeeRequest: CreateEmployeeRequest): IO[Either[Throwable, Int]] =
    for {
      employeeEither <- employeeRepository.create(Employee(0, createEmployeeRequest.fullName))
      result <- createEmployeeSchedule(employeeEither.getOrElse(0))(createEmployeeRequest.scheduler)
    } yield result

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
