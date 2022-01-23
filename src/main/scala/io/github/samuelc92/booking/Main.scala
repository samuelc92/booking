package io.github.samuelc92.booking

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.blaze.server.*
import org.http4s.implicits.*

object Main extends IOApp:

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(BookingRoutes.routes)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)