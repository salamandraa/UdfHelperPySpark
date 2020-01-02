
name := "UdfHelperPySpark"

version := "0.1"

scalaVersion := "2.11.12"


libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % "2.3.0" % Provided,
  "org.apache.spark" %% "spark-hive" % "2.3.0" % Provided,
  "org.scalatest" % "scalatest_2.11" % "3.0.4" % Test
)