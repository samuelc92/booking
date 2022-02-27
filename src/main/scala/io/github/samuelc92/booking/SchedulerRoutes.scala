package io.github.samuelc92.booking

import cats.effect.Sync
import cats.implicits.*
import cats.effect.*
import doobie.Transactor
import org.http4s.{EntityEncoder, HttpRoutes, QueryParamCodec, QueryParamDecoder}
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.io.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.circe.CirceEntityDecoder.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import io.github.samuelc92.booking.repositories.BookingRepositoryAlgebra
import io.github.samuelc92.booking.usecases.ScheduleUseCase

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object SchedulerRoutes:

  implicit val isoLocalDateCodec: QueryParamCodec[LocalDate] =
    QueryParamCodec.localDate(DateTimeFormatter.ISO_LOCAL_DATE)

  object IsoLocalDateParamMatcher extends QueryParamDecoderMatcher[LocalDate]("date")

  def routes(bookingRepository: BookingRepositoryAlgebra) =
    HttpRoutes.of[IO] {
      case GET -> Root / "scheduler" / IntVar(employeeId) :? IsoLocalDateParamMatcher(date) =>
        ScheduleUseCase(bookingRepository)
          .getScheduler(employeeId, date)
          .flatMap(Ok(_))
    }