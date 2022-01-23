package io.github.samuelc92.booking.config

import cats.effect.IO
import io.circe.generic.auto.*

case class ServerConfig(port: Int, host: String)

case class DbConfig(url: String, username: String, password: String)

case class Config(serverConfig: ServerConfig, dbConfig: DbConfig)
object Config {

  def load: Config = Config(ServerConfig(9000, "0.0.0.0"), DbConfig("", "postgres", "postgres"))
}
