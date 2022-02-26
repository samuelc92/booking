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
import java.time.DayOfWeek

case class EmployeeSchedule(employeeId: Int, day: String, startTime1: String, endTime1: String,
                            startTime2: String, endTime2: String)

trait EmployeeScheduleRepositoryAlgebra:
  def findByEmployeeId(employeeId: Int): IO[List[EmployeeSchedule]]
  def create(employeeSchedule: Seq[EmployeeSchedule]): IO[Int]

object EmployeeScheduleRepository:
  def apply(transactor: Transactor[IO]): EmployeeScheduleRepositoryAlgebra = new EmployeeScheduleRepository(transactor)

class EmployeeScheduleRepository(transactor: Transactor[IO]) extends EmployeeScheduleRepositoryAlgebra:

  def findByEmployeeId(employeeId: Int): IO[List[EmployeeSchedule]] =
    sql"SELECT * FROM employee_schedule WHERE employeeId = $employeeId"
      .query[EmployeeSchedule]
      .to[List]
      .transact(transactor)

  def create(employeeSchedule: Seq[EmployeeSchedule]): IO[Int] =
    val sql = "INSERT INTO employee_schedule(employeeId, day, startTime1, endTime1, startTime2, endTime2) VALUES (?, ?, ?, ?, ?, ?)"
    Update[EmployeeSchedule](sql).updateMany(employeeSchedule).transact(transactor)