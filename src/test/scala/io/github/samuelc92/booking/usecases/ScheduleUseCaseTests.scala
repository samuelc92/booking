package booking.usecases

import booking.stubs.{BookingRepositoryStub, EmployeeScheduleRepositoryStub}
import munit.CatsEffectSuite

import java.time.LocalDate

class ScheduleUseCaseTests extends CatsEffectSuite:
  private val defaultQuantityTimeOff = 2;
  private val useCase = ScheduleUseCase(BookingRepositoryStub.apply(), EmployeeScheduleRepositoryStub.apply())

  test("getScheduler_WhenThereAreThreeHoursBookedOnSameDay_ShouldReturnWithTheTimesAsNotAvailable") {
    useCase.getScheduler(1, LocalDate.of(2022, 3, 1)) map { result =>
      assertEquals(result.times.filter(!_.available).size, 3 + defaultQuantityTimeOff)
    }
  }

  test("getScheduler_WhenThereAreNoHoursBookedOnSameDay_ShouldReturnTheOffTimeAsNotAvailable") {
    useCase.getScheduler(1, LocalDate.of(2022, 3, 2)) map { result =>
      assertEquals(result.times.filter(!_.available).size, defaultQuantityTimeOff)
    }
  }

  test("getScheduler_WhenEmployeeDoesNotHaveScheduler_ShouldReturnEmptyTimes") {
    useCase.getScheduler(1, LocalDate.of(2022, 3, 5)) map { result =>
      assert(result.times.isEmpty)
    }
  }