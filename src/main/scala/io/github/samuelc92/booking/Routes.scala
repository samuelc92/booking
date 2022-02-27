package io.github.samuelc92.booking

import cats.effect.IO
import doobie.Transactor
import io.github.samuelc92.booking.repositories.{BookingRepository, EmployeeRepository, EmployeeScheduleRepository}
import io.github.samuelc92.booking.routes.EmployeeRoutes
import org.http4s.HttpRoutes
import org.http4s.dsl.io.*

object Routes {

  def routes(xa: Transactor[IO]) =
    import cats.syntax.semigroupk.*
    val employeeScheduleRepository = EmployeeScheduleRepository(xa)
    val completeRoutes = healthRoutes <+>
      BookingRoutes.routes(BookingRepository(xa)) <+>
      SchedulerRoutes.routes(BookingRepository(xa), employeeScheduleRepository) <+>
      EmployeeRoutes.routes(EmployeeRepository(xa), employeeScheduleRepository)
    completeRoutes.orNotFound

  val healthRoutes =
    HttpRoutes.of[IO] {
      case GET -> Root / "health" =>
        Ok("ping")
    }
}
