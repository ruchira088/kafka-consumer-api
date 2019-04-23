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
        rootDependencies ++ Seq(scalaTestPlusPlay).map(_ % Test)
    )
    .dependsOn(macroUtilities)

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

lazy val simpleKafkaPublisher =
  (project in file("./simple-kafka-publisher"))
    .enablePlugins(BuildInfoPlugin)
    .settings(
      name := "simple-kafka-publisher",
      organization := organizationName,
      version := "0.0.1",
      scalaVersion := SCALA_VERSION,
      buildInfoKeys := BuildInfoKey.ofN(name),
      buildInfoPackage := "com.ruchij.publisher.eed3si9n",
      libraryDependencies ++=
        Seq(akkaActor, akkaStream, akkaStreamKafka, avro4sCore, kafkaAvroSerializer, javaFaker, logbackClassic)
    )
    .dependsOn(root)

lazy val macroUtilities =
  (project in file("./macro-utilities"))
    .settings(
      name := "macro-utilities",
      organization := organizationName,
      version := "0.0.1",
      scalaVersion := SCALA_VERSION,
      libraryDependencies ++= Seq(scalaReflect)
    )

addCommandAlias("runWithPostgres", "run -Dconfig.file=conf/application.postgres.conf")
