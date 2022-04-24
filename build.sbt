ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.10"

lazy val root = (project in file("."))
  .settings(
    name := "pwc-kafka-scala-streaming"
  )

val sparkVersion = "3.0.2"
val kafkaVersion = "2.4.0"

libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % sparkVersion,
    "org.apache.spark" %% "spark-sql" % sparkVersion,

    // streaming
    "org.apache.spark" %% "spark-streaming" % sparkVersion,

    // streaming-kafka
    "org.apache.spark" % "spark-sql-kafka-0-10_2.12" % sparkVersion,

    // low-level integrations
    "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion,
    "org.apache.spark" %% "spark-streaming-kinesis-asl" % sparkVersion,

  //fakerScala
//  "com.github.pjfanning" %% "scala-faker" % "0.5.3"
  "io.github.etspaceman" %% "scalacheck-faker" % "7.0.0",

  // kafka
  "org.apache.kafka" %% "kafka" % kafkaVersion,
  "org.apache.kafka" % "kafka-streams" % kafkaVersion,

//  mongo connector
  "org.mongodb.spark" %% "mongo-spark-connector" % "3.0.1"
)
val circeVersion = "0.14.1"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)
