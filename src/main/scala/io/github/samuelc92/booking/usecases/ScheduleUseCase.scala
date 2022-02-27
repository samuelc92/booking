package io.github.samuelc92.booking.usecases

import java.time.{LocalDate, OffsetDateTime, OffsetTime, ZoneOffset}
import io.github.samuelc92.booking.*
import cats.effect.IO
import io.github.samuelc92.booking.repositories.{BookingMapped, BookingRepositoryAlgebra}

import scala.annotation.tailrec

case class ScheduleResponse(employeeId: Int, date: LocalDate, times: List[ScheduleTimes])
case class ScheduleTimes(time: OffsetTime, available: Boolean)

object ScheduleUseCase:
  def apply(repository: BookingRepositoryAlgebra) = new ScheduleUseCase(repository)

class ScheduleUseCase(repository: BookingRepositoryAlgebra):

  def getScheduler(employeeId: Int, date: LocalDate) =
    val employeeScheduler = buildEmployeeScheduler
    val employeeBookings = repository.findByEmployeeIdAndDay(employeeId,
      date.atTime(OffsetTime.of(0, 0, 0, 0, ZoneOffset.UTC)))
    for {
      bookings <- employeeBookings
      scheduler <- IO.pure[ScheduleResponse](ScheduleResponse(employeeId, date,
        buildEmployeeScheduler(bookings)))
    } yield scheduler

  private def buildEmployeeScheduler(bookings: List[BookingMapped]): List[ScheduleTimes] =
    val startTime = OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC)
    val endTime = OffsetTime.of(20, 0, 0, 0, ZoneOffset.UTC)
    @tailrec
    def recur(actualHour: OffsetTime, acc: List[ScheduleTimes]): List[ScheduleTimes] =
      if (actualHour.isAfter(endTime))
        acc
      else
        val isAvailable = bookings
          .filter(b => isActualHourBetweenStartAtAndEndAt(actualHour, b.startAt.toOffsetTime, b.endAt.toOffsetTime))
          .isEmpty
        recur(actualHour.plusMinutes(30), ScheduleTimes(actualHour, isAvailable) +: acc)
    recur(startTime, List.empty)

  private def isActualHourBetweenStartAtAndEndAt(actualHour: OffsetTime, startAt: OffsetTime, endAt: OffsetTime) =
    (actualHour.isEqual(startAt) || actualHour.isAfter(startAt)) && actualHour.isBefore(endAt)