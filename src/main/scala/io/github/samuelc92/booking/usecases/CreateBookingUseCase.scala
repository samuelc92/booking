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
    for {
      bookings <- repository.findByEmployeeIdAndDay(request.employeeId, request.startAt)
      bookingOption <- IO.pure[Option[BookingMapped]](filterBookingThatIsSamePeriod(bookings, request))
      result <- createBooking(bookingOption, request)
    } yield result

  private def createBooking(bookingOption: Option[BookingMapped], request: Booking): IO[Either[NoSlotError, Int]] =
    bookingOption match {
      case Some(_) => IO.pure(Left(NoSlotError("There is no slot available")))
      case None =>
        val mapped = BookingMapped(request.id, request.attendanceId, request.employeeId, request.startAt, request.endAt, request.status.ordinal)
        for {
          booking <- repository.create(mapped)
        } yield Right(booking)
    }

  private def filterBookingThatIsSamePeriod(bookings: List[BookingMapped], request: Booking): Option[BookingMapped] =
    bookings
      .find(b => request.startAt >= b.startAt && request.startAt <= b.endAt)

extension (d: OffsetDateTime) {
  def >=(d2: OffsetDateTime): Boolean =
    if d.compareTo(d2) >= 0 then true else false

  def <=(d2: OffsetDateTime): Boolean =
    if d.compareTo(d2) <= 0 then true else false
}