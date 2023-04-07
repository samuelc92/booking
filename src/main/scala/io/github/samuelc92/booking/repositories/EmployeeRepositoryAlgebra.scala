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

trait EmployeeRepositoryAlgebra {
  def create(employee: Employee): Task[Unit]

  def findById(id: Int): Task[Option[Employee]]

  def findAll: Task[List[Employee]]
}

object EmployeeRepositoryAlgebra {
  def create(employee: Employee): ZIO[EmployeeRepositoryAlgebra, Throwable, Unit] =
    ZIO.serviceWithZIO[EmployeeRepositoryAlgebra](_.create(employee))

  def findById(id: Int): ZIO[EmployeeRepositoryAlgebra, Throwable, Option[Employee]] =
    ZIO.serviceWithZIO[EmployeeRepositoryAlgebra](_.findById(id))

  def findAll(id: String): ZIO[EmployeeRepositoryAlgebra, Throwable, List[Employee]] =
    ZIO.serviceWithZIO[EmployeeRepositoryAlgebra](_.findAll)

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

    override def create(employee: Employee): Task[Unit] = {

      val transaction = for {
        _ <- SQL.insertEmployee(employee).run
      } yield ()

      transaction.transact(transactor)
    }

    override def findById(id: Int): Task[Option[Employee]] =
      SQL
        .getById(id)
        .option
        .transact(transactor)

    override def findAll: Task[List[Employee]] =
      SQL
        .getAll
        .to[List]
        .transact(transactor)
  }

  object SQL {

    def insertEmployee(employee: Employee): Update0 =
      sql"""INSERT INTO employee
              (name)
            VALUES (${employee.name.toString()})
      """.update

    def getById(id: Int): Query0[Employee] =
      sql"""SELECT id, name
            FROM employee
            WHERE id = ${id}
        """   
        .query[(Int, String)]
        .map {
          case (id, name) => Employee(id, Name(name))
        }
    
    def getAll: Query0[Employee] =
      sql"""SELECT *
            FROM employee
      """
      .query[(Int, String)]
      .map {
        case (id, name) => Employee(id, Name(name))
      }
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