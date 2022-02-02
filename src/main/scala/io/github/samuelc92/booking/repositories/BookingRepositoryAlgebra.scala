package io.github.samuelc92.booking.repositories

import cats.effect.IO
import cats.implicits.*
import doobie.*
import doobie.implicits.*
// Very important to deal with arrays
import doobie.implicits.javasql.*
import doobie.postgres.*
import doobie.postgres.implicits.*
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor.*

import java.util.UUID
import java.time.OffsetDateTime

case object BookingNotFoundError
case class BookingMapped(id: Int, attendanceId: UUID, employeeId: UUID, startAt: OffsetDateTime, endAt: OffsetDateTime, status: Int)

trait BookingRepositoryAlgebra:
  def findById(id: Int): IO[Option[BookingMapped]]
  def findAll: IO[List[BookingMapped]]
  def findByEmployeeIdAndDay(employeeId: UUID, startAt: OffsetDateTime): IO[List[BookingMapped]]
  def create(booking: BookingMapped): IO[Int]
  def delete(id: Int): IO[Int]

object BookingRepository:
  def apply(transactor: Transactor[IO]): BookingRepositoryAlgebra =
    new BookingRepository(transactor)

class BookingRepository(transactor: Transactor[IO]) extends BookingRepositoryAlgebra:

  def findById(id: Int): IO[Option[BookingMapped]] =
    sql"SELECT * FROM booking WHERE id = $id"
      .query[BookingMapped]
      .option
      .transact(transactor)

  def findAll: IO[List[BookingMapped]] =
    sql"SELECT * FROM booking"
      .query[BookingMapped]
      .to[List]
      .transact(transactor)

  def create(booking: BookingMapped): IO[Int] =
    sql"""
         |INSERT INTO booking(attendanceId, employeeId, startAt, endAt, status)
         |VALUES (${booking.attendanceId}, ${booking.employeeId}, ${booking.startAt}, ${booking.endAt}, ${booking.status})
    """.stripMargin
      .update
      .run
      .transact(transactor)

  def delete(id: Int): IO[Int] =
    sql"DELETE FROM booking WHERE id = $id"
      .update
      .run
      .transact(transactor)

  def findByEmployeeIdAndDay(employeeId: UUID, startAt: OffsetDateTime): IO[List[BookingMapped]] =
    sql"""
         |SELECT * FROM booking WHERE employeeid = $employeeId
         |AND date_trunc('day', startat) = date_trunc('day', $startAt)
    """.stripMargin
      .query[BookingMapped]
      .to[List]
      .transact(transactor)