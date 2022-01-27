package io.github.samuelc92.booking

import cats.effect.IO
import cats.implicits.*
import doobie.*
import doobie.implicits.*

import java.time.OffsetDateTime

// Very important to deal with arrays
import doobie.postgres._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor._
import doobie.util.ExecutionContexts
import doobie.implicits.javasql.*

import java.util.{UUID}

case object BookingNotFoundError
case class BookingMapped(id: Int, attendanceId: UUID, employeeId: UUID, startAt: OffsetDateTime, endAt: OffsetDateTime, status: Int)

trait BookingRepositoryAlgebra:
  def findById(id: Int): IO[Option[BookingMapped]]
  def findAll: IO[List[BookingMapped]]
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
