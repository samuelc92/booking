package io.github.samuelc92.booking.usecases

import io.github.samuelc92.booking.stubs.{EmployeeRepositoryStub, EmployeeScheduleRepositoryStub}
import munit.CatsEffectSuite

class CreateEmployeeUseCaseTests extends CatsEffectSuite:
  import io.github.samuelc92.booking.usecases.CreateEmployeeUseCaseTests.invalidEmployeeScheduleRequest

  private val useCase = CreateEmployeeUseCase(EmployeeRepositoryStub.apply(), EmployeeScheduleRepositoryStub.apply())

  test("execute_WhenSendInvalidEmployeeSchedule_ShouldReturnError") {
    useCase.execute(invalidEmployeeScheduleRequest) map { result =>
      result match {
        case Right(_) => assert(false)
        case Left(value) => assertEquals(value.getMessage, "Invalid value")
      }
    }
  }

object CreateEmployeeUseCaseTests:
  val invalidEmployeeScheduleRequest = CreateEmployeeRequest("test", List(
    CreateEmployeeScheduleRequest("MONDAY", "09:abe", "12:00", "13:00", "18:00")))