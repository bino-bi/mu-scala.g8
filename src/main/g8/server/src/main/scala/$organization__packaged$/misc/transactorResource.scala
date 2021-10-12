package $organization$.misc

import $organization$.config.Config
import cats.effect.{Async, Clock, Resource, Sync}
import doobie.ExecutionContexts
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateResult

import scala.concurrent.ExecutionContext

object transactorResource {
  private[this] def transactionPool[F[_]: Sync]: Resource[F, ExecutionContext] =
    ExecutionContexts.cachedThreadPool[F]

  def migrate[F[_]: Sync](transactor: HikariTransactor[F]): Resource[F, MigrateResult] = Resource.eval {
    transactor.configure { datasource =>
      Sync[F].delay {
        val flyway = Flyway.configure().dataSource(datasource).load()
        flyway.migrate()
      }
    }
  }

  def apply[F[_]: Async: Clock](config: Config): Resource[F, HikariTransactor[F]] = for {
    transaction <- transactionPool[F]

    xa <- HikariTransactor.newHikariTransactor[F](
            config.database.driver,
            config.database.url,
            config.database.user,
            config.database.password,
            transaction
          )
    _  <- migrate(xa)
  } yield xa
}
