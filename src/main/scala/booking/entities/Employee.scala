package booking.entities

import java.time.{DayOfWeek, LocalTime}

case class Employee(
  id: Int,
  name: Name,
  scheduler: Seq[EmployeeSchedule])

case class EmployeeSchedule(
  id: Int,
  employeeId: Int,
  day: DayOfWeek,
  startTime1: LocalTime,
  endTime1: LocalTime,
  startTime2: LocalTime,
  endTime2: LocalTime)