package booking.entities

import io.circe.Decoder.Result
import io.circe.{Decoder, HCursor}

import java.time.OffsetDateTime
import java.util.UUID

enum BookingStatus:
  case PENDENT, CONFIRMED, CANCELED

case class Booking(id: Int, attendanceId: UUID, employeeId: Int, startAt: OffsetDateTime, endAt: OffsetDateTime
                   , status: BookingStatus)

object Booking:
  given decodeBooking: Decoder[Booking] = new Decoder[Booking] {
    override def apply(c: HCursor): Result[Booking] =
      for {
        id <- c.downField("id").as[Int]
        attendanceId <- c.downField("attendanceId").as[UUID]
        employeeId <- c.downField("employeeId").as[Int]
        startAt <- c.downField("startAt").as[OffsetDateTime]
        endAt <- c.downField("endAt").as[OffsetDateTime]
        status <- c.downField("status").as[String]
      } yield Booking(id, attendanceId, employeeId, startAt, endAt, BookingStatus.valueOf(status))
  }
