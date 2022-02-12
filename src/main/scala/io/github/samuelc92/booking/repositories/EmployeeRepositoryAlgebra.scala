package io.github.samuelc92.booking.repositories

import cats.effect.IO
import cats.implicits.*
import doobie.util.transactor.Transactor
import doobie.*
import doobie.implicits.*
// Very important to deal with arrays
import doobie.implicits.javasql.*
import doobie.postgres.*
import doobie.postgres.implicits.*
import doobie.util.ExecutionContexts

import java.util.UUID

case class Employee(id: UUID, name: String)

trait EmployeeRepositoryAlgebra:
  def findById(id: UUID): IO[Option[Employee]]
  def findAll: IO[List[Employee]]

object EmployeeRepository:
  def apply(transactor: Transactor[IO]): EmployeeRepositoryAlgebra =
    new EmployeeRepository(transactor)

class EmployeeRepository(transactor: Transactor[IO]) extends EmployeeRepositoryAlgebra:

  def findById(id: UUID): IO[Option[Employee]] =
    sql"SELECT * FROM employee WHERE id = $id"
      .query[Employee]
      .option
      .transact(transactor)

  def findAll: IO[List[Employee]] =
    sql"SELECT * FROM employee"
      .query[Employee]
      .to[List]
      .transact(transactor)

  def create(employee: Employee): IO[Int] =
    sql"""
         |INSERT INTO employee(id, name)
         |VALUES (${employee.id}, ${employee.name})
    """.stripMargin
      .update
      .run
      .transact(transactor)

  def delete(id: Int): IO[Int] =
    sql"DELETE FROM employee WHERE id = $id"
      .update
      .run
      .transact(transactor)
