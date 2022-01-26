package io.github.samuelc92.booking

import cats.effect.{ExitCode, IO, IOApp}
import doobie.Transactor
import org.http4s.blaze.server.*
import org.http4s.implicits.*

object Main extends IOApp:

  val xa: Transactor[IO] = Transactor.fromDriverManager[IO] (
    "org.postgresql.Driver",
    "jdbc:postgresql:postgres",
    "postgres",
    "postgres"
  )

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(BookingRoutes.allRoutes(xa))
      .serve
      .compile
      .drain
      .as(ExitCode.Success)