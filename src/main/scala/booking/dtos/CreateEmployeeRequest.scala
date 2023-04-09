package booking.dtos

import booking.entities.Employee
import booking.entities.EmployeeSchedule
import booking.entities.Name

import zio.json.{DeriveJsonCodec, JsonCodec}
import sttp.tapir.Schema
import java.time.{DayOfWeek, LocalTime}

final case class CreateEmployeeRequest(
  name: String,
  scheduler: Seq[CreateEmployeeScheduleRequest]) {
    def toEntity: Employee = Employee(
      0,
      Name(name),
      scheduler.map(e => e.toEntity)
    )
  }  

object CreateEmployeeRequest:
  implicit val jsonCodec: JsonCodec[CreateEmployeeRequest] = DeriveJsonCodec.gen
  implicit val schema: Schema[CreateEmployeeRequest] = Schema.derived

final case class CreateEmployeeScheduleRequest(
  day: String,
  startTime1: LocalTime,
  endTime1: LocalTime,
  startTime2: LocalTime,
  endTime2: LocalTime) {
    def toEntity: EmployeeSchedule = EmployeeSchedule(
        0,
        0,
        DayOfWeek.valueOf(day),
        startTime1,
        endTime1,
        startTime2,
        endTime2
    )
  }

object CreateEmployeeScheduleRequest:
  implicit val jsonCodec: JsonCodec[CreateEmployeeScheduleRequest] = DeriveJsonCodec.gen
  implicit val schema: Schema[CreateEmployeeScheduleRequest] = Schema.derived
