package $organization$

import cats.effect._
import $organization$.protocol.hello.Greeter
import $organization$.misc.transactorResource
import $organization$.profiles.config.{Config, configResource}
import higherkindness.mu.rpc.server._
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import org.typelevel.log4cats.slf4j.Slf4jLogger
object Server extends IOApp {
  private implicit def unsafeLogger[F[_] : Sync]: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger[F]

  def run(args: List[String]): IO[ExitCode] = (for {
    config <- configResource[IO]
    transactor <- transactorResource[IO](config)
    service = new HappyGreeter[IO]
    _ <- server(config)(service)
  } yield ExitCode.Success).useForever

  private def server(config: Config)(implicit service: Greeter[IO]): Resource[IO, Unit] = for {
    serviceDef <- Profiles.bindService[IO]
    _ <- Resource.eval(Logger[IO].info(s"Setup server with port: \${config.rcp.port}"))
    _ <- GrpcServer.defaultServer[IO](config.rcp.port, List(AddService(serviceDef)))
    _ <- Resource.make(Logger[IO].info("Server started"))(_ => Logger[IO].info("Server shutting down ..."))
  } yield ()

}
