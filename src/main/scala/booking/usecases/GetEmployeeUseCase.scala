package io.github.samuelc92.booking.usecases

import io.github.samuelc92.booking.entities.Employee
import io.github.samuelc92.booking.repositories.EmployeeRepositoryAlgebra
import io.github.samuelc92.booking.Error
import io.github.samuelc92.booking.dtos.*

import zio.*

trait GetEmployeeUseCase:
  def getById(id: Int): ZIO[Any, Error.NotFound, GetEmployeeResponse]
  def getAll: ZIO[Any, Nothing, List[GetEmployeeResponse]]

object GetEmployeeUseCase:
  lazy val layer: ZLayer[EmployeeRepositoryAlgebra, Nothing, GetEmployeeUseCase] = ZLayer {
    for {
      repository <- ZIO.service[EmployeeRepositoryAlgebra]
    } yield GetEmployeeUseCaseImpl(repository)
  }

  final case class GetEmployeeUseCaseImpl(
    repository: EmployeeRepositoryAlgebra
  ) extends GetEmployeeUseCase:

    override def getById(id: Int): ZIO[Any, Error.NotFound, GetEmployeeResponse] =
      for {
        employee <- repository
                      .findById(id)
                      .tapError(e => ZIO.logError(s"Error on getting employee by id. Error $e"))
                      .mapError(_ => Error.NotFound(s"Employee with id $id not found."))
                      .someOrFail(Error.NotFound(s"Employee with id $id not found."))
      } yield GetEmployeeResponse(employee.id, employee.name.toString)
    
    override def getAll: ZIO[Any, Nothing, List[GetEmployeeResponse]] =
      for {
        employees <- repository
                      .findAll
                      .tapError(e => ZIO.logError("Error getting all employees"))
                      .orDie
      } yield employees.map(emp => GetEmployeeResponse(emp.id, emp.name.toString))