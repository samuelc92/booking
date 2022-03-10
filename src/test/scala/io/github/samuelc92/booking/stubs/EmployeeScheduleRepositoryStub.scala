package io.github.samuelc92.booking.stubs

import cats.effect.IO
import io.github.samuelc92.booking.repositories.{EmployeeSchedule, EmployeeScheduleRepositoryAlgebra}
import io.github.samuelc92.booking.stubs.EmployeeScheduleRepositoryStub.employeeSchedules

class EmployeeScheduleRepositoryStub extends EmployeeScheduleRepositoryAlgebra:
  override def findByEmployeeId(employeeId: Int): IO[List[EmployeeSchedule]] = ???

  override def findByEmployeeIdAndDay(employeeId: Int, day: String): IO[Option[EmployeeSchedule]] =
    IO.pure(employeeSchedules.find(e => e.employeeId == employeeId && e.day == day))

  override def create(employeeSchedule: Seq[EmployeeSchedule]): IO[Either[Throwable, Int]] = IO.pure(Right(1))

object EmployeeScheduleRepositoryStub:
  def apply(): EmployeeScheduleRepositoryAlgebra = new EmployeeScheduleRepositoryStub()

  def employeeSchedules =
    List(
      EmployeeSchedule(1, "TUESDAY", "09:00", "12:00", "13:00", "18:00"),
      EmployeeSchedule(1, "WEDNESDAY", "09:00", "12:00", "13:00", "18:00"),
    )