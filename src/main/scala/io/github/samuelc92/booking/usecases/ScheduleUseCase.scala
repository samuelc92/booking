package io.github.samuelc92.booking.usecases

import java.time.{LocalDate, OffsetDateTime, OffsetTime, ZoneOffset}
import io.github.samuelc92.booking.*
import cats.effect.IO
import io.github.samuelc92.booking.repositories.{BookingMapped, BookingRepositoryAlgebra, EmployeeSchedule, EmployeeScheduleRepositoryAlgebra}

import scala.annotation.tailrec

final case class ScheduleResponse(employeeId: Int, date: LocalDate, times: List[ScheduleTimes])
final case class ScheduleTimes(time: OffsetTime, available: Boolean)

object ScheduleUseCase:
  def apply(repository: BookingRepositoryAlgebra, employeeScheduleRepository: EmployeeScheduleRepositoryAlgebra) =
    new ScheduleUseCase(repository, employeeScheduleRepository)

class ScheduleUseCase(repository: BookingRepositoryAlgebra, employeeScheduleRepository: EmployeeScheduleRepositoryAlgebra):

  def getScheduler(employeeId: Int, date: LocalDate): IO[ScheduleResponse] =
    val employeeBookings = repository.findByEmployeeIdAndDay(employeeId,
      date.atTime(OffsetTime.of(0, 0, 0, 0, ZoneOffset.UTC)))
    for {
      bookings <- employeeBookings
      employeeSchedule <- employeeScheduleRepository.findByEmployeeIdAndDay(employeeId, date.getDayOfWeek.name())
      scheduler <- employeeSchedule match {
        case Some(value) => IO.pure(ScheduleResponse(employeeId, date,
          buildEmployeeScheduler(bookings, value)
            .sortWith((t, that) => t.time.isBefore(that.time))))
        case None => IO.pure(ScheduleResponse(employeeId, date, List.empty))
      }
    } yield scheduler

  private def buildEmployeeScheduler(bookings: List[BookingMapped], employeeSchedule: EmployeeSchedule): List[ScheduleTimes] =
    val startTime1 = getOffSetTime(employeeSchedule.startTime1)
    val endTime1 = getOffSetTime(employeeSchedule.endTime1)
    val startTime2 = getOffSetTime(employeeSchedule.startTime2)
    val endTime2 = getOffSetTime(employeeSchedule.endTime2)
    @tailrec
    def recur(actualHour: OffsetTime, endTime: OffsetTime, acc: List[ScheduleTimes]): List[ScheduleTimes] =
      if (actualHour.isAfter(endTime)) acc
      else
        val isAvailable = actualHour.isBefore(endTime) && isBookingEmpty(bookings, actualHour)
        recur(actualHour.plusMinutes(30), endTime, ScheduleTimes(actualHour, isAvailable) +: acc)
    recur(startTime1, endTime1, List.empty) ++ recur(startTime2, endTime2, List.empty)

  private def isBookingEmpty(bookings: List[BookingMapped], actualHour: OffsetTime) =
    bookings
      .filter(b => isActualHourBetweenStartAtAndEndAt(actualHour, b.startAt.toOffsetTime, b.endAt.toOffsetTime))
      .isEmpty

  private def isActualHourBetweenStartAtAndEndAt(actualHour: OffsetTime, startAt: OffsetTime, endAt: OffsetTime) =
    (actualHour.isEqual(startAt) || actualHour.isAfter(startAt)) && actualHour.isBefore(endAt)

  private def getOffSetTime(time: String) =
    OffsetTime.of(time.split(":")(0).toInt, time.split(":")(1).toInt, 0, 0, ZoneOffset.UTC)