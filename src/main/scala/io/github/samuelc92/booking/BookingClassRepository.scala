package io.github.samuelc92.booking

import cats.data.NonEmptyList
import cats.effect.*
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

import java.util.UUID
// Very important to deal with arrays
import doobie.postgres._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor._
import doobie.util.ExecutionContexts

case object BookingClassNotFoundError
case class BookingClass(id: Int, description: String, slots: Int)

object BookingClassRepository {

  val xa: Transactor[IO] = Transactor.fromDriverManager[IO] (
    "org.postgresql.Driver",
    "jdbc:postgresql:postgres",
    "postgres",
    "postgres"
  )

  def findById(id: Int): IO[Option[BookingClass]] =
    sql"SELECT id, description, slots FROM public.booking_class WHERE id = $id"
      .query[BookingClass]
      .option
      .transact(xa)

  def findAll: IO[List[BookingClass]] =
    sql"SELECT id, description, slots FROM public.booking_class"
      .query[BookingClass]
      .to[List]
      .transact(xa)

  def create(bookingClass: BookingClass): IO[Int] =
    sql"""
      |INSERT INTO booking_class (description, slots)
      |VALUES (${bookingClass.description}, ${bookingClass.slots})
    """.stripMargin
      .update
      .run
      .transact(xa)

  def update(bookingClass: BookingClass): IO[Int] =
    sql"""
         |UPDATE booking_class
         |SET description = ${bookingClass.description} , slots = ${bookingClass.slots}
         |WHERE id = ${bookingClass.id}
    """.stripMargin
      .update
      .run
      .transact(xa)

  def delete(id: Int): IO[Int] =
    sql"DELETE FROM booking_class WHERE id = $id"
      .update
      .run
      .transact(xa)
}
