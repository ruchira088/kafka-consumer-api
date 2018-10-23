import Dependencies._

val organizationName = "com.ruchij"

lazy val root =
  (project in file("."))
    .enablePlugins(PlayScala, BuildInfoPlugin)
    .settings(
      name := "kafka-consumer-api",
      organization := organizationName,
      version := "0.0.1",
      scalaVersion := SCALA_VERSION,
      buildInfoKeys := BuildInfoKey.ofN(name, version, sbtVersion, scalaVersion),
      buildInfoPackage := "com.ruchij.eed3si9n",
      libraryDependencies ++=
        rootDependencies ++ rootTestDependencies.map(_ % Test)
    )

lazy val rootDependencies =
  Seq(
    guice,
    akkaStreamKafka,
    kafkaAvroSerializer,
    playSlick,
    playSlickEvolutions,
    postgresql,
    h2,
    javaFaker,
    scalaLogging,
    logbackClassic
  )

lazy val rootTestDependencies =
  Seq(scalaTestPlusPlay)

lazy val simpleKafkaPublisher =
  (project in file("./simple-kafka-publisher"))
    .settings(
      name := "simple-kafka-publisher",
      organization := organizationName,
      version := "0.0.1",
      scalaVersion := SCALA_VERSION
    )

lazy val macroUtilities =
  (project in file("./macro-utilities"))
    .settings(
      name := "macro-utilities",
      organization := organizationName,
      version := "0.0.1",
      scalaVersion := SCALA_VERSION,
      libraryDependencies ++= Seq(scalaReflect)
    )
