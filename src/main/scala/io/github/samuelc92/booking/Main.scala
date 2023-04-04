package io.github.samuelc92.booking

import doobie.Transactor
import java.net.InetAddress
import zio.*
import zhttp.service.Server

import io.github.samuelc92.booking.config.AppConfig
import io.github.samuelc92.booking.routes.EmployeeRoutes
import io.github.samuelc92.booking.repositories.EmployeeRepositoryAlgebra
import io.github.samuelc92.booking.usecases.*


object Main extends ZIOAppDefault:

  override val run = ZIO.scoped {
    for {
      httpApp <- EmployeeRoutes.httApp
      start   <- Server(httpApp).withBinding("0.0.0.0", 8080).make.orDie
      _       <- ZIO.logInfo(s"Server started on port: ${start.port}")
      _       <- ZIO.never
    } yield ()
  }.provide(
      AppConfig.layer,
      HttpServerSettings.default,
      EmployeeRoutes.layer,
      EmployeeRepositoryAlgebra.layer,
      CreateEmployeeUseCase.layer,
      GetEmployeeUseCase.layer,
      ZLayer.Debug.tree
  )
