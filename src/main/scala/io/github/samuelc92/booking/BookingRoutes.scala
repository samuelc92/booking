package io.github.samuelc92.booking

import cats.effect.Sync
import cats.implicits.*
import cats.effect.*
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.io.*
import org.http4s.EntityEncoder
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.circe.CirceEntityDecoder.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import io.github.samuelc92.booking.usecases.*
import io.github.samuelc92.booking.entities.*
import io.github.samuelc92.booking.entities.Booking.given
import io.github.samuelc92.booking.repositories.BookingRepositoryAlgebra

object BookingRoutes:

  def routes(bookingRepository: BookingRepositoryAlgebra) =
    HttpRoutes.of[IO] {
      case GET -> Root / "booking" / IntVar(id) =>
        bookingRepository
          .findById(id)
          .flatMap {
            case Some(bookingClass) => Ok(bookingClass)
            case None => NotFound()
          }
      case GET -> Root / "booking" =>
        bookingRepository
          .findAll
          .flatMap(Ok(_))
      case req @ POST -> Root / "booking" =>
        for {
          booking <- req.as[Booking]
          resp <- CreateBookingUseCase(bookingRepository)
            .execute(booking)
            .flatMap { b =>
              b match {
                case Left(error) => BadRequest(error)
                case Right(value) => Created(value)
              }
            }
        } yield (resp)
      case DELETE -> Root / "booking" / IntVar(id) =>
        bookingRepository
          .delete(id)
          .flatMap(_ => NoContent())
    }
