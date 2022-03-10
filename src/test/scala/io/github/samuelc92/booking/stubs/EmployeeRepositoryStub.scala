package io.github.samuelc92.booking.stubs

import cats.effect.IO
import io.github.samuelc92.booking.repositories.{Employee, EmployeeRepositoryAlgebra}

class EmployeeRepositoryStub extends EmployeeRepositoryAlgebra:
  override def findById(id: Int): IO[Option[Employee]] = ???

  override def findAll: IO[List[Employee]] = ???

  override def create(employee: Employee): IO[Either[Throwable, Int]] = IO.pure(Right(1))

object EmployeeRepositoryStub:
  def apply(): EmployeeRepositoryAlgebra = new EmployeeRepositoryStub()