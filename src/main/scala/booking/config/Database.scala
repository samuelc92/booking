package booking.config

import cats.effect.IO
import doobie.Transactor

object Database {

  def transactor: Transactor[IO] =
  Transactor.fromDriverManager[IO] (
    "org.postgresql.Driver",
    "jdbc:postgresql:public",
    "postgres",
    "postgres"
  )
}
