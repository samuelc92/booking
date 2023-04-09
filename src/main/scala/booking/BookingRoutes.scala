package booking

import booking.usecases.*
import booking.entities.*
import booking.entities.Booking.given
import booking.repositories.BookingRepositoryAlgebra
import zhttp.http._
import zio._

object BookingRoutes:

  def routes: Http[Any, Nothing, Request, Response] =
    Http.collect[Request] {
      case Method.GET -> !! / "booking" =>
        Response.json("""{"greetings": "Hello World!"}""")
      /*
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
      */
    }
