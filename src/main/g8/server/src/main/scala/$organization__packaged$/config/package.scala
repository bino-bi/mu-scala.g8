package bi.bino.next.profiles

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

  final case class Config(rcp: RcpConfig, database: DatabaseConfig)

  final case class RcpConfig(port: Int)

  final case class DatabaseConfig(driver: String, url: String, user: String, password: String)
}
