package io.github.samuelc92.booking.routes

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.io.*
import org.http4s.EntityEncoder
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.circe.CirceEntityDecoder.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import io.github.samuelc92.booking.repositories.{EmployeeRepositoryAlgebra, EmployeeScheduleRepositoryAlgebra}
import io.github.samuelc92.booking.usecases.{CreateEmployeeRequest, CreateEmployeeUseCase}

object EmployeeRoutes:

  def routes(employeeRepository: EmployeeRepositoryAlgebra, employeeScheduleRepository: EmployeeScheduleRepositoryAlgebra) =
    HttpRoutes.of[IO] {
      case req @ POST -> Root / "employees" =>
        for {
          request <- req.as[CreateEmployeeRequest]
          response <- CreateEmployeeUseCase(employeeRepository, employeeScheduleRepository)
            .execute(request) flatMap {
              case Left(error) => BadRequest(error.getMessage)
              case Right(value) => Created(value)
            }
        } yield (response)

    }
