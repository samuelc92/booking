package io.github.samuelc92.booking

import doobie.Transactor
import zio.*
import zhttp.service.Server

import io.github.samuelc92.booking.routes.EmployeeRoutes
import io.github.samuelc92.booking.repositories.EmployeeRepository
import io.github.samuelc92.booking.usecases.CreateEmployeeUseCase
import java.net.InetAddress


object Main extends ZIOAppDefault:

  override val run = ZIO.scoped {
    for {
      httpApp <- EmployeeRoutes.httApp
      start   <- Server(httpApp).withBinding("0.0.0.0", 8080).make.orDie
      _       <- ZIO.logInfo(s"Server started on port: ${start.port}")
      _       <- ZIO.never
    } yield ()
  }.provide(
      HttpServerSettings.default,
      EmployeeRoutes.layer,
      EmployeeRepository.layer,
      CreateEmployeeUseCase.layer,
      ZLayer.Debug.tree
  )
