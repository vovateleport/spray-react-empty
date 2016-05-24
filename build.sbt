import sbt._
import Process._
import Keys._

lazy val scalaV = "2.11.8"
scalaJSUseRhino in Global := false

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  scalaVersion := scalaV
)

lazy val `sr-shared` = (crossProject.crossType(CrossType.Pure) in file("sr-shared"))
  .settings(commonSettings: _*)
  .settings(
    name := "sr-shared",
    libraryDependencies ++= Seq(
    )
  )

lazy val `sr-sharedJVM` = `sr-shared`.jvm
lazy val `sr-sharedJS` = `sr-shared`.js

lazy val `sr-service` = (project in file("sr-service"))
  .settings(commonSettings: _*)
  .settings(
    name := "sr-service",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % "0.4.0",
      "com.lihaoyi" %% "autowire" % "0.2.5",
      "com.typesafe.akka" %% "akka-actor" % "2.4.6",
      "io.spray" %% "spray-can" % "1.3.3",
      "io.spray" %% "spray-json" % "1.3.2",
      "io.spray" %% "spray-routing" % "1.3.3",
      "io.spray" %% "spray-client" % "1.3.3"
    )
    ,
    (resources in Compile) ++= Seq(
      (fastOptJS in (`sr-ui`, Compile)).value.data,
      (packageJSDependencies in (`sr-ui`, Compile)).value
    )
    ,(unmanagedResourceDirectories in Compile) <+= (baseDirectory in `sr-ui`) {_ / "src" / "main" / "resources" / "static"}
  )
  .dependsOn(`sr-sharedJVM`)

lazy val `sr-ui` = (project in file("sr-ui"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "sr-ui",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % "0.4.0",
      "com.lihaoyi" %%% "autowire" % "0.2.5",
      "org.scala-js" %%% "scalajs-dom" % "0.9.0",
      "com.github.japgolly.scalajs-react" %%% "core" % "0.11.1",
      "com.github.japgolly.scalajs-react" %%% "extra" % "0.11.1",
      "com.github.japgolly.scalajs-react" %%% "ext-scalaz72" % "0.11.1",
      "com.github.japgolly.scalajs-react" %%% "ext-monocle" % "0.11.1"
    ),

    jsDependencies ++= Seq(
      "org.webjars.bower" % "react" % "15.0.2"
        /        "react-with-addons.js"
        minified "react-with-addons.min.js"
        commonJSName "React",

      "org.webjars.bower" % "react" % "15.0.2"
        /         "react-dom.js"
        minified  "react-dom.min.js"
        dependsOn "react-with-addons.js"
        commonJSName "ReactDOM",

      "org.webjars.bower" % "react" % "15.0.2"
        /         "react-dom-server.js"
        minified  "react-dom-server.min.js"
        dependsOn "react-dom.js"
        commonJSName "ReactDOMServer"
    ),
    skip in packageJSDependencies := false
  )
  .dependsOn(`sr-sharedJS`)
