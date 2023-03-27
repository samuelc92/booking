package io.github.samuelc92.booking.routes

import zhttp.http.*
import zio.*
import zio.json.*
import io.github.samuelc92.booking.entities.*
import io.github.samuelc92.booking.repositories.*

object EmployeeRoutes:

  def apply(): Http[EmployeeRepositoryAlgebra, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      case req@(Method.POST -> !! / "employees") =>
        for
          e <- req.body.asString.map(_.fromJson[Employee])
          r <- e match
            case Left(ex) =>
              ZIO.debug(s"Failed to parse the input: $ex")
                 .as(Response.text(ex).setStatus(Status.BadRequest))
            case Right(e) =>
              EmployeeRepositoryAlgebra.create(e).map(id => Response.text(id))
        yield r
    }
