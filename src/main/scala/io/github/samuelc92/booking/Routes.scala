package io.github.samuelc92.booking

import cats.effect.IO
import doobie.Transactor
import org.http4s.HttpRoutes
import org.http4s.dsl.io.*

object Routes {

  def routes(xa: Transactor[IO]) =
    import cats.syntax.semigroupk.*
    val completeRoutes = BookingRoutes.routes(BookingRepository(xa)) <+>
      SchedulerRoutes.routes(BookingRepository(xa)) <+>
      healthRoutes
    completeRoutes.orNotFound

  val healthRoutes =
    HttpRoutes.of[IO] {
      case GET -> Root / "health" =>
        Ok("ping")
    }
}
