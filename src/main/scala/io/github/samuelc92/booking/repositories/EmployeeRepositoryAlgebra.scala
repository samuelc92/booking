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

case class Employee(id: Int, fullName: String)

trait EmployeeRepositoryAlgebra:
  def findById(id: Int): IO[Option[Employee]]
  def findAll: IO[List[Employee]]
  def create(employee: Employee): IO[Int]

object EmployeeRepository:
  def apply(transactor: Transactor[IO]): EmployeeRepositoryAlgebra =
    new EmployeeRepository(transactor)

class EmployeeRepository(transactor: Transactor[IO]) extends EmployeeRepositoryAlgebra:

  def findById(id: Int): IO[Option[Employee]] =
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
    createReturningId(employee)
      .transact(transactor)

  private def createReturningId(employee: Employee): ConnectionIO[Int] =
    sql"""
         |INSERT INTO employee(fullName)
         |VALUES (${employee.fullName})
    """.stripMargin
      .update
      .withUniqueGeneratedKeys[Int]("id")

  def delete(id: Int): IO[Int] =
    sql"DELETE FROM employee WHERE id = $id"
      .update
      .run
      .transact(transactor)