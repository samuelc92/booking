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

object BookingRoutes:

  val routes =
    HttpRoutes.of[IO] {
      case GET -> Root / "health" =>
        NoContent()
      case GET -> Root / "booking" / "class" / IntVar(id) =>
        BookingClassRepository
          .findById(id)
          .flatMap {
            case Some(bookingClass) => Ok(bookingClass)
            case None => NotFound()
          }
      case GET -> Root / "booking" / "class" =>
        BookingClassRepository
          .findAll
          .flatMap(Ok(_))
      case req @ POST -> Root / "booking" / "class" =>
        for {
          bookingClass <- req.as[BookingClass]
          resp <- BookingClassRepository
            .create(bookingClass)
            .flatMap(Created(_))
        } yield (resp)
      case req @ PUT -> Root / "booking" / "class" =>
        for {
          bookingClass <- req.as[BookingClass]
          resp <- BookingClassRepository
            .update(bookingClass)
            .flatMap(_ => NoContent())
        } yield (resp)
      case DELETE -> Root / "booking" / "class" / IntVar(id) =>
        BookingClassRepository
          .delete(id)
          .flatMap(_ => NoContent())
    }.orNotFound