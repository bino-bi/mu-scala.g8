import com.typesafe.sbt.packager.archetypes.scripts.BashStartScriptPlugin.autoImport.bashScriptExtraDefines
import higherkindness.mu.rpc.srcgen.Model._

val V = new {
  val mu = "$mu_version$"
  val logback = "1.2.3"
  val log4cats = "2.1.1"
  val scalatest = "3.1.2"
  val pureconfig = "0.17.0"
  val doobie = "1.0.0-RC1"
  val flywaydb = "7.8.1"
}

inThisBuild(Seq(
  organization := "$organization$",
  scalaVersion := "$scala_version$",
  scalacOptions += "-language:higherKinds"
))

lazy val macroSettings: Seq[Setting[_]] = Seq(
  libraryDependencies ++= Seq(
    scalaOrganization.value % "scala-compiler" % scalaVersion.value % Provided
  ),
  scalacOptions ++= on(2, 13)("-Ymacro-annotations").value
)

val protocol = project
  .settings(
    name := "$name;format="norm"$-protocol",

    libraryDependencies ++= Seq(
      "io.higherkindness" %% "mu-rpc-service" % "$mu_version$"
    ),
    macroSettings,
    muSrcGenIdlType := IdlType.Proto,
    muSrcGenIdiomaticEndpoints := true
  )
  .enablePlugins(SrcGenPlugin)

val server = project
  .settings(
    name := "$name;format="norm"$-rpc-server",

    libraryDependencies ++= Seq(
      "com.github.pureconfig" %% "pureconfig" % V.pureconfig,
      "com.github.pureconfig" %% "pureconfig-cats" % V.pureconfig,
      "com.github.pureconfig" %% "pureconfig-cats-effect" % V.pureconfig,
      "io.higherkindness" %% "mu-rpc-fs2" % V.mu,
      "io.higherkindness" %% "mu-rpc-server" % V.mu,
      "ch.qos.logback" % "logback-classic" % V.logback,
      "org.typelevel" %% "log4cats-core" % V.log4cats,
      "org.typelevel" %% "log4cats-slf4j" % V.log4cats,
      "org.tpolecat" %% "doobie-core" % V.doobie,
      "org.tpolecat" %% "doobie-postgres" % V.doobie,
      "org.tpolecat" %% "doobie-postgres-circe" % V.doobie,
      "org.flywaydb"                   % "flyway-core"               % V.flywaydb,
      "org.tpolecat" %% "doobie-hikari" % V.doobie,
      "org.scalatest" %% "scalatest" % V.scalatest % Test,
      "io.higherkindness" %% "mu-rpc-testing" % V.mu % Test
    ),

    // Start the server in a separate process so it shuts down cleanly when you hit Ctrl-C
    fork := true,
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    Universal / packageName := "app",
    Universal / mappings += {
      val conf = (Compile / resourceDirectory).value / "application.conf"
      conf -> "conf/application.conf"
    },
    bashScriptExtraDefines += """addJava "-Dconfig.file=${app_home}/../conf/application.conf""""
  )
  .enablePlugins(UniversalPlugin)
  .enablePlugins(JavaAppPackaging)
  .dependsOn(protocol)

val root = (project in file("."))
  .settings(
    name := "$name;format="norm"$"
  )
  .aggregate(protocol, server, client)
