package io.github.samuelc92.booking

import doobie.Transactor
import zhttp.http._
import zhttp.service.Server
import zio._
import io.github.samuelc92.booking.routes.EmployeeRoutes
import io.github.samuelc92.booking.repositories.EmployeeRepository


object Main extends ZIOAppDefault:

  def run =
    Server.start(
      port = 8080,
        http = Routes.wrappedRoutes
    ).provide(
      EmployeeRepository.layer
    )
