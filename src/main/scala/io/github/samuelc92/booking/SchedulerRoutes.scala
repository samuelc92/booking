package io.github.samuelc92.booking

import zhttp.http._
import zio._
import io.github.samuelc92.booking.repositories.{BookingRepositoryAlgebra, EmployeeScheduleRepositoryAlgebra}
import io.github.samuelc92.booking.usecases.ScheduleUseCase

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object SchedulerRoutes:

  def routes: Http[Any, Nothing, Request, Response] =
    Http.collect[Request] {
      case Method.GET -> !! / "schedulers" =>
        Response.json("""{"greetings": "Hello World!"}""")
    }
