package io.github.samuelc92.booking.config

/*
import cats.effect.IO
import com.typesafe.config.ConfigFactory

sealed trait Config {
  protected val config = ConfigFactory.load()
  protected val applicationConfig = config.getConfig("application")
}

object ServerConfig extends Config {
  val serverConfig = this
  private val server = config.getConfig("server")
  val host = server.getString("host")
  val port = server.getInt("port")
}

object DbConfig extends Config {
  val dbConfig = this
  private val db = config.getConfig("db")
  val username = db.getString("username")
  val password = db.getString("password")
}// (url: String, username: String, password: String)
*/