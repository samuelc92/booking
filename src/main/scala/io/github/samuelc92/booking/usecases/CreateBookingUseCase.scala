package io.github.samuelc92.booking.usecases

import cats.effect.IO
import java.time.OffsetDateTime
import java.util.UUID

import io.github.samuelc92.booking.{BookingMapped, BookingRepositoryAlgebra}
import io.github.samuelc92.booking.entities.Booking

trait BusinessError
case class NoSlotError(message: String) extends BusinessError

object CreateBookingUseCase:
  def apply(repository: BookingRepositoryAlgebra) = new CreateBookingUseCase(repository)

class CreateBookingUseCase(repository: BookingRepositoryAlgebra):
  def execute(request: Booking): IO[Either[NoSlotError, Int]] =
    val bookings = for {
      bookings <- repository.findByEmployeeIdAndDay(request.employeeId, request.startAt)
      booking <- IO.pure[List[BookingMapped]](bookings.filter(b => request.startAt >= b.startAt && request.startAt <= b.endAt))
    } yield booking
    bookings.flatMap { b =>
      b match {
        case head :: tail => IO(Left(NoSlotError("There is no slot available")))
        case Nil =>
          val mapped = BookingMapped(request.id, request.attendanceId, request.employeeId, request.startAt, request.endAt, request.status.ordinal)
          for {
            result <- repository.create(mapped)
          } yield Right(result)
      }
    }

extension (d: OffsetDateTime) {
  def >=(d2: OffsetDateTime): Boolean =
    if d.compareTo(d2) >= 0 then true else false

  def <=(d2: OffsetDateTime): Boolean =
    if d.compareTo(d2) <= 0 then true else false
}