addSbtPlugin("org.foundweekends.giter8" %% "sbt-giter8" % "0.14.0")
libraryDependencies += { "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value }

addSbtPlugin("org.scalameta" % "sbt-mdoc" % "2.2.24")
addSbtPlugin("com.geirsson" % "sbt-ci-release" % "1.5.9")
addSbtPlugin("com.alejandrohdezma" % "sbt-github" % "0.9.3")
addSbtPlugin("com.alejandrohdezma" % "sbt-github-mdoc" % "0.9.3")
