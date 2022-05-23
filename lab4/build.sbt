scalaVersion := "2.13.8"

scalacOptions ++= Seq("-language:implicitConversions", "-deprecation")
libraryDependencies ++= Seq(
  "com.storm-enroute" %% "scalameter-core" % "0.21",
  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.0",
  "org.scalameta" %% "munit" % "0.7.22" % Test
)

lazy val root = (project in file("."))
  .settings(
    name := "lab4"
  )
