package io.github.samuelc92.booking.repositories

import io.getquill.context.ZioJdbc.DataSourceLayer
import io.getquill.{Escape, H2ZioJdbcContext, CamelCase}
import io.getquill.jdbczio.Quill
import io.getquill.*
import zio._
import io.github.samuelc92.booking.entities.*

import java.util.UUID
import javax.sql.DataSource
import java.sql.SQLException

case class EmployeeTable(id: UUID, name: String)

trait EmployeeRepositoryAlgebra:
  def findById(id: String): ZIO[Any, Throwable, Option[Employee]]

  def findAll: ZIO[Any, Throwable, List[Employee]]

  def create(employee: Employee): ZIO[Any, Throwable, String]

object EmployeeRepositoryAlgebra:
  def create(employee: Employee): ZIO[EmployeeRepositoryAlgebra, Throwable, String] =
    ZIO.serviceWithZIO[EmployeeRepositoryAlgebra](_.create(employee))

  def findById(id: String): ZIO[EmployeeRepositoryAlgebra, Throwable, Option[Employee]] =
    ZIO.serviceWithZIO[EmployeeRepositoryAlgebra](_.findById(id))

  def findAll(id: String): ZIO[EmployeeRepositoryAlgebra, Throwable, List[Employee]] =
    ZIO.serviceWithZIO[EmployeeRepositoryAlgebra](_.findAll)

case class EmployeeRepository(ds: DataSource) extends EmployeeRepositoryAlgebra:
  val ctx = new H2ZioJdbcContext(CamelCase)
  import ctx._

  override def create(employee: Employee): ZIO[Any, SQLException, String]  = {
    for
      id <- Random.nextUUID
      _  <- ctx.run {
        quote {
          query[EmployeeTable].insertValue {
            lift(EmployeeTable(id, employee.name))
          }
        }
      }
    yield id.toString
  }.provide(ZLayer.succeed(ds))

  override def findById(id: String): ZIO[Any, SQLException, Option[Employee]] =
    ctx.run {
      quote {
        query[EmployeeTable]
          .filter(p => p.id == lift(UUID.fromString(id)))
          .map(e => Employee(e.name))
      }
    }.provide(ZLayer.succeed(ds)).map(_.headOption)

  override def findAll: ZIO[Any, SQLException, List[Employee]] =
    ctx.run {
      quote {
        query[EmployeeTable]
          .map(e => Employee(e.name))
      }
    }.provide(ZLayer.succeed(ds))

object EmployeeRepository:
  def layer: ZLayer[Any, Throwable, EmployeeRepository] =
    Quill.DataSource.fromPrefix("BookingApp") >>> ZLayer.fromFunction(EmployeeRepository(_))
