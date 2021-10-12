package $organization$

import cats.Applicative
import cats.effect.{Resource, Sync}
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax._

package object config {

  @inline
  final def configResource[F[_] : Sync : Applicative]: Resource[F, Config] = {
    for {
      config <- Resource.eval(ConfigSource.default.loadF[F, Config]())
    } yield config
  }

  final case class Config(rcp: RcpConfig, $if(with_db.truthy)$database: DatabaseConfig$endif$)

  final case class RcpConfig(port: Int)
  $if(with_db.truthy)$
  final case class DatabaseConfig(driver: String, url: String, user: String, password: String)
  $endif$
}
