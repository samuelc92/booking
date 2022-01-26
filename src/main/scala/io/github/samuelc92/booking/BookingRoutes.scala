package io.github.samuelc92.booking

import cats.effect.Sync
import cats.implicits.*
import cats.effect.*
import doobie.Transactor
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.io.*
import org.http4s.EntityEncoder
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.circe.CirceEntityDecoder.*
import io.circe.generic.auto.*
import io.circe.syntax.*

object BookingRoutes:

  def allRoutes(xa: Transactor[IO]) =
    import cats.syntax.semigroupk.*
    val completeRoutes = routes(BookingClassRepository(xa)) <+> bookingRoutes(BookingRepository(xa))
    completeRoutes.orNotFound

  def routes(bookingClassRepository: BookingClassRepositoryAlgebra) =
    HttpRoutes.of[IO] {
      case GET -> Root / "health" =>
        NoContent()
      case GET -> Root / "booking" / "class" / IntVar(id) =>
        bookingClassRepository
          .findById(id)
          .flatMap {
            case Some(bookingClass) => Ok(bookingClass)
            case None => NotFound()
          }
      case GET -> Root / "booking" / "class" =>
        bookingClassRepository
          .findAll
          .flatMap(Ok(_))
      case req @ POST -> Root / "booking" / "class" =>
        for {
          bookingClass <- req.as[BookingClass]
          resp <- bookingClassRepository
            .create(bookingClass)
            .flatMap(Created(_))
        } yield (resp)
      case req @ PUT -> Root / "booking" / "class" =>
        for {
          bookingClass <- req.as[BookingClass]
          resp <- bookingClassRepository
            .update(bookingClass)
            .flatMap(_ => NoContent())
        } yield (resp)
      case DELETE -> Root / "booking" / "class" / IntVar(id) =>
        bookingClassRepository
          .delete(id)
          .flatMap(_ => NoContent())
    }

  def bookingRoutes(bookingRepository: BookingRepositoryAlgebra) =
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
          bookingClass <- req.as[BookingMapped]
          resp <- bookingRepository
            .create(bookingClass)
            .flatMap(Created(_))
        } yield (resp)
      case DELETE -> Root / "booking" / IntVar(id) =>
        bookingRepository
          .delete(id)
          .flatMap(_ => NoContent())
    }
