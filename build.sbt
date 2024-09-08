lazy val root = (project in file(".")).settings(
  inThisBuild(
    List(
      organization := "com.jambit.stream.detector",
      scalaVersion := "2.13.14"
    )
  ),
  name := "stream-detector"
)

ThisBuild / assemblyMergeStrategy := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case x =>
    val defaultStrategy = (ThisBuild / assemblyMergeStrategy).value
    defaultStrategy(x)
}

libraryDependencies ++= Seq(
  library.flinkClients,
  library.flinkStreamingJava,
  library.flinkKafka,
  library.flinkCep,
  library.flinkTestUtils,
  library.scalaTest,
  library.mockito,
  library.log4jCore,
  library.slf4log4j2,
  library.slf4j
)

lazy val library = new {
  val version = new {
    val scala     = "2.13.14"
    val flink     = "1.17.2"
    val log4j     = "2.20.0"
    val slf4j     = "2.0.7"
    val scalaTest = "3.2.15"
    val mockito   = "3.2.15.0"
  }

  val flinkClients = "org.apache.flink" % "flink-clients" % version.flink
  val flinkStreamingJava =
    "org.apache.flink" % "flink-streaming-java" % version.flink classifier "tests"
  val flinkKafka = "org.apache.flink" % "flink-connector-kafka" % version.flink
  val flinkCep   = "org.apache.flink" % "flink-cep"             % version.flink
  val flinkQueryableStateClient =
    "org.apache.flink" % "flink-queryable-state-client-java" % version.flink

  val log4jCore  = "org.apache.logging.log4j" % "log4j-core"        % version.log4j
  val slf4log4j2 = "org.apache.logging.log4j" % "log4j-slf4j2-impl" % version.log4j
  val slf4j      = "org.slf4j"                % "slf4j-api"         % version.slf4j

  val flinkTestUtils =
    "org.apache.flink" % "flink-test-utils" % version.flink % "test, it" exclude ("org.apache.logging.log4j", "log4j-slf4j-impl")
  val scalaTest = "org.scalatest"     %% "scalatest"   % version.scalaTest % "test, it"
  val mockito   = "org.scalatestplus" %% "mockito-4-6" % version.mockito   % "test"
}
