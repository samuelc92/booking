package io.github.samuelc92.booking

import zhttp.http._
import zio._
import java.io.{IOException}
import io.github.samuelc92.booking.repositories.{BookingRepository, EmployeeRepository, EmployeeScheduleRepository}
import io.github.samuelc92.booking.routes.EmployeeRoutes

object Routes:

  def routes =
    BookingRoutes.routes ++ SchedulerRoutes.routes ++ healthRoutes

  def wrappedRoutes =
    routes @@ Middleware.debug

  val healthRoutes =
    Http.collect[Request] {
      case Method.GET -> !! / "health" => Response.text("ping")
    }
