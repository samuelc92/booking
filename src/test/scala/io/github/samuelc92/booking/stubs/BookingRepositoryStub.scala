package io.github.samuelc92.booking.stubs

import cats.effect.IO
import io.github.samuelc92.booking.repositories.{BookingMapped, BookingRepositoryAlgebra}
import io.github.samuelc92.booking.stubs.BookingRepositoryStub.buildBookingMapped

import java.time.{OffsetDateTime, ZoneOffset}
import java.util.UUID

class BookingRepositoryStub extends BookingRepositoryAlgebra:
  override def findById(id: Int): IO[Option[BookingMapped]] = ???

  override def findAll: IO[List[BookingMapped]] = ???

  override def findByEmployeeIdAndDay(employeeId: Int, startAt: OffsetDateTime): IO[List[BookingMapped]] =
    IO.pure(buildBookingMapped.filter(p => p.startAt.isAfter(startAt)))

  override def create(booking: BookingMapped): IO[Int] = ???

  override def delete(id: Int): IO[Int] = ???

object BookingRepositoryStub:
  def apply(): BookingRepositoryAlgebra = new BookingRepositoryStub()

  def buildBookingMapped =
    List(
      BookingMapped(
        1,
        UUID.randomUUID(),
        1,
        OffsetDateTime.of(2022, 3, 1, 10, 0, 0, 0, ZoneOffset.UTC),
        OffsetDateTime.of(2022, 3, 1, 11, 0, 0, 0, ZoneOffset.UTC),
        0
      ),
      BookingMapped(
        2,
        UUID.randomUUID(),
        1,
        OffsetDateTime.of(2022, 3, 1, 15, 0, 0, 0, ZoneOffset.UTC),
        OffsetDateTime.of(2022, 3, 1, 15, 30, 0, 0, ZoneOffset.UTC),
        0
      )
    )