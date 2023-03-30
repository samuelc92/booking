package io.github.samuelc92.booking.usecases

import io.github.samuelc92.booking.stubs.{EmployeeScheduleRepositoryStub}
import munit.CatsEffectSuite

  /*
class CreateEmployeeUseCaseTests extends CatsEffectSuite:
  import io.github.samuelc92.booking.usecases.CreateEmployeeUseCaseTests.*

  private val useCase = CreateEmployeeUseCase(EmployeeRepositoryStub.apply(), EmployeeScheduleRepositoryStub.apply())

  test("execute_WhenRequestIsValid_ShouldSuccess") {
    useCase.execute(employeeScheduleRequest) map { result =>
      result match {
        case Right(_) => assert(true)
        case Left(_) => assert(false)
      }
    }
  }
  test("execute_WhenSendInvalidEmployeeSchedule_ShouldReturnError") {
    useCase.execute(invalidEmployeeScheduleRequest) map { result =>
      result match {
        case Right(_) => assert(false)
        case Left(value) => assertEquals(value.getMessage, "Invalid value")
      }
    }
  }
  test("execute_WhenSendEmployeeScheduleRequestWithInvalidTimeFormatter_ShouldReturnError") {
    useCase.execute(invalidTimeEmployeeScheduleRequest) map { result =>
      result match {
        case Right(_) => assert(false)
        case Left(value) => assertEquals(value.getMessage, "Invalid value")
      }
    }
  }

object CreateEmployeeUseCaseTests:
  val employeeScheduleRequest = CreateEmployeeRequest("test", List(
    CreateEmployeeScheduleRequest("MONDAY", "09:00", "12:00", "13:00", "18:00")))

  val invalidEmployeeScheduleRequest = CreateEmployeeRequest("test", List(
    CreateEmployeeScheduleRequest("MONDAY", "09:abe", "12:00", "13:00", "18:00")))

  val invalidTimeEmployeeScheduleRequest = CreateEmployeeRequest("test", List(
    CreateEmployeeScheduleRequest("MONDAY", "09:00", "1200", "13:00", "18:00")))
  */
