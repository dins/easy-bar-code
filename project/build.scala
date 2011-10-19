import sbt._

import Keys._
import AndroidKeys._

object General {
  val settings = Defaults.defaultSettings ++ Seq (
    //name := "Easy Bar Code",
    version := "0.1",
    scalaVersion := "2.8.1",
    platformName in Android := "android-8"
  )

  lazy val fullAndroidSettings =
    General.settings ++
    AndroidProject.androidSettings ++
    TypedResources.settings ++
    AndroidMarketPublish.settings ++ Seq (
      keyalias in Android := "change-me",
      libraryDependencies += "org.scalatest" %% "scalatest" % "1.5.1" % "test"
    )

  lazy val testSettings =
    Seq (
      resolvers ++= Seq("snapshots" at "http://scala-tools.org/repo-snapshots",
        "releases"  at "http://scala-tools.org/repo-releases"),

      libraryDependencies ++= Seq(
          // with Scala 2.8.1 (specs2 1.5 is the latest version for scala 2.8.1)
          "org.specs2" %% "specs2" % "1.5",
          "org.specs2" %% "specs2-scalaz-core" % "5.1-SNAPSHOT" % "test"
      ),
    resolvers ++= Seq("snapshots" at "http://jbrechtel.github.com/repo/snapshots"),
    libraryDependencies += "com.github.jbrechtel" %% "robospecs" % "0.1-SNAPSHOT" % "test"
  )
}

object AndroidBuild extends Build {
  lazy val main = Project (
    "Easy Bar Code",
    file("."),
    settings = General.fullAndroidSettings ++ General.testSettings
  )

  lazy val tests = Project (
    "tests",
    file("tests"),
    settings = General.settings ++ AndroidTest.androidSettings ++ General.testSettings
  ) dependsOn main

}
