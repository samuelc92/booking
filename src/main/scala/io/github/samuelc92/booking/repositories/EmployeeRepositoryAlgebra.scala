package io.github.samuelc92.booking.repositories

import io.github.samuelc92.booking.config.DatabaseConfig
import io.github.samuelc92.booking.entities.*

import org.flywaydb.core.Flyway

import zio.*
import zio.interop.catz.*
import zio.interop.catz.implicits.*

import doobie.*
import doobie.hikari.*
import doobie.implicits.*
import doobie.implicits.legacy.instant.*
import doobie.util.ExecutionContexts
import doobie.util.transactor

case class EmployeeTable(id: Int, name: String)

trait EmployeeRepositoryAlgebra {
  def create(employee: Employee): ZIO[Any, Throwable, Unit]

  // def findById(id: Int): ZIO[Any, Nothing, Option[Employee]]

  // def findAll: ZIO[Any, Nothing, List[Employee]]
}

object EmployeeRepositoryAlgebra {
  def create(employee: Employee): ZIO[EmployeeRepositoryAlgebra, Throwable, Unit] =
    ZIO.serviceWithZIO[EmployeeRepositoryAlgebra](_.create(employee))

  /*
  def findById(id: Int): ZIO[EmployeeRepositoryAlgebra, Nothing, Option[Employee]] =
    ZIO.serviceWithZIO[EmployeeRepositoryAlgebra](_.findById(id))

  def findAll(id: String): ZIO[EmployeeRepositoryAlgebra, Nothing, List[Employee]] =
    ZIO.serviceWithZIO[EmployeeRepositoryAlgebra](_.findAll)
  */

  lazy val layer: ZLayer[DatabaseConfig, Throwable, EmployeeRepositoryAlgebra] = ZLayer.scoped {
    for {
      config     <- ZIO.service[DatabaseConfig]
      _          <- loadAndMigrateFlyway(config) 
      ec         <- ExecutionContexts.fixedThreadPool[Task](32).toScopedZIO
      transactor <- HikariTransactor
                      .newHikariTransactor[Task](
                        config.driver,
                        config.url,
                        config.user,
                        config.password,
                        ec
                      )
                      .toScopedZIO
      repository = EmployeeRepository(transactor)
    } yield repository
  }

  case class EmployeeRepository(transactor: HikariTransactor[Task]) extends EmployeeRepositoryAlgebra {

    override def create(employee: Employee): ZIO[Any, Throwable, Unit] = {

      val transaction = for {
        _ <- SQL.insertEmployee(employee).run
      } yield ()

      transaction.transact(transactor)
    }

    /*
    override def findById(id: Int): ZIO[Any, SQLException, Option[Employee]] =
      ctx.run {
        quote {
          query[EmployeeTable]
            .filter(p => p.id == lift(id))
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
    */
  }

  object SQL {

    def insertEmployee(employee: Employee): Update0 =
      sql"""INSERT INTO employee
              (name)
            VALUES (${employee.name})
      """.update
  }

  def loadAndMigrateFlyway(config: DatabaseConfig): Task[Unit] =
    for {
      flyway <- ZIO.attempt {
                  Flyway
                    .configure()
                    .dataSource(config.url, config.user, config.password)
                    .load()
                }
      _      <- ZIO.attempt(flyway.migrate())
    } yield ()
}