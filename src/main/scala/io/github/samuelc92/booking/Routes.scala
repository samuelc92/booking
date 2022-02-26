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
    val completeRoutes = healthRoutes <+>
      BookingRoutes.routes(BookingRepository(xa)) <+>
      SchedulerRoutes.routes(BookingRepository(xa)) <+>
      EmployeeRoutes.routes(EmployeeRepository(xa), EmployeeScheduleRepository(xa))
    completeRoutes.orNotFound

  val healthRoutes =
    HttpRoutes.of[IO] {
      case GET -> Root / "health" =>
        Ok("ping")
    }
}
