package io.github.samuelc92.booking.usecases

import io.github.samuelc92.booking.entities.Employee
import io.github.samuelc92.booking.repositories.EmployeeRepositoryAlgebra
import io.github.samuelc92.booking.Error

import zio.*

trait GetEmployeeUseCase:
  def getById(id: Int): ZIO[Any, Error.NotFound, Employee]

object GetEmployeeUseCase:
  lazy val layer: ZLayer[EmployeeRepositoryAlgebra, Nothing, GetEmployeeUseCase] = ZLayer {
    for {
      repository <- ZIO.service[EmployeeRepositoryAlgebra]
    } yield GetEmployeeUseCaseImpl(repository)
  }

  final case class GetEmployeeUseCaseImpl(
    repository: EmployeeRepositoryAlgebra
  ) extends GetEmployeeUseCase:

    override def getById(id: Int): ZIO[Any, Error.NotFound, Employee] =
      repository
        .findById(id)
        .tapError(e => ZIO.logError(s"Error on getting employee by id. Error $e"))
        .mapError(_ => Error.NotFound(s"Employee with id $id not found."))
        .someOrFail(Error.NotFound(s"Employee with id $id not found."))