import Dependencies._

lazy val root =
  (project in file("."))
    .enablePlugins(PlayScala, BuildInfoPlugin)
    .settings(
      name := "kafka-consumer-api",
      organization := "com.ruchij",
      version := "0.0.1",
      scalaVersion := SCALA_VERSION,
      buildInfoKeys := BuildInfoKey.ofN(name, version, sbtVersion, scalaVersion),
      buildInfoPackage := "com.ruchij.eed3si9n",
      libraryDependencies ++=
        Seq(guice) ++ Seq(scalaTestPlusPlay).map(_ % Test)
    )