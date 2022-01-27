package io.github.samuelc92.booking.usecases

import cats.effect.IO
import java.time.OffsetDateTime
import java.util.UUID

import io.github.samuelc92.booking.{BookingMapped, BookingRepositoryAlgebra}
import io.github.samuelc92.booking.entities.Booking

object CreateBookingUseCase:
  def apply(repository: BookingRepositoryAlgebra) = new CreateBookingUseCase(repository)

class CreateBookingUseCase(repository: BookingRepositoryAlgebra):
  def execute(booking: Booking): IO[Int] =
    repository.create(BookingMapped(booking.id, booking.attendanceId, booking.employeeId, booking.startAt, booking.endAt
      , booking.status.ordinal))