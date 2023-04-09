package booking.usecases

import java.time.{LocalDate, OffsetDateTime, OffsetTime, ZoneOffset}
import booking.*
import cats.effect.IO
import booking.repositories.{BookingMapped, BookingRepositoryAlgebra, EmployeeSchedule, EmployeeScheduleRepositoryAlgebra}
import booking.valueobjects.Period

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
        case Some(value) =>
          IO.pure(
            ScheduleResponse(employeeId, date, buildEmployeeScheduler(bookings, value)
              .sortWith((t, that) => t.time.isBefore(that.time)))
          )
        case None => IO.pure(ScheduleResponse(employeeId, date, List.empty))
      }
    } yield scheduler

  private def buildEmployeeScheduler(bookings: List[BookingMapped], employeeSchedule: EmployeeSchedule): List[ScheduleTimes] =
    val initialPeriod = new Period(employeeSchedule.startTime1, employeeSchedule.endTime1)
    val finalPeriod = new Period(employeeSchedule.startTime2, employeeSchedule.endTime2)
    @tailrec
    def recur(actualHour: OffsetTime, endTime: OffsetTime, acc: List[ScheduleTimes]): List[ScheduleTimes] =
      if (actualHour.isAfter(endTime)) acc
      else
        val isAvailable = actualHour.isBefore(endTime) && isThereNoBookingAt(bookings, actualHour)
        recur(actualHour.plusMinutes(30), endTime, ScheduleTimes(actualHour, isAvailable) +: acc)
    recur(initialPeriod.startTime, initialPeriod.endTime, List.empty) ++
    recur(finalPeriod.startTime, finalPeriod.endTime, List.empty)

  private def isThereNoBookingAt(bookings: List[BookingMapped], actualHour: OffsetTime) =
    bookings
      .filter(b => isActualHourBetweenStartAtAndEndAt(actualHour, b.startAt.toOffsetTime, b.endAt.toOffsetTime))
      .isEmpty

  private def isActualHourBetweenStartAtAndEndAt(actualHour: OffsetTime, startAt: OffsetTime, endAt: OffsetTime) =
    (actualHour.isEqual(startAt) || actualHour.isAfter(startAt)) && actualHour.isBefore(endAt)