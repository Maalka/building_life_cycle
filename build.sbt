import WebKeys._

name := "building_life_cycle"
organization in ThisBuild := "com.maalka"

version := "1.0.1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)


maintainer in Linux := "Clay Teeter <clay.teeter@maalka.com>"
maintainer in Docker := "Clay Teeter <clay.teeter@maalka.com>"

packageSummary in Linux := "Maalka - Life Cycle Tool"
packageDescription := "Maalka - Life Cycle Tool"

dockerRepository := Some("maalka")
dockerUpdateLatest := true

scalaVersion := "2.11.8"
lazy val maalkaApp = (project in file(".")).enablePlugins(SbtWeb, PlayScala, SbtNativePackager,
  BuildInfoPlugin)


libraryDependencies ++= Seq(
  jdbc, filters,
  cache,
  ws,
  specs2 % Test,

  "com.maalka" %% "bedes" % "1.1.0.0",

  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % "test",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.14",
  "com.typesafe.akka" %% "akka-stream" % "2.4.14",
  "com.typesafe.akka" %% "akka-contrib" % "2.4.14",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.14",

  "com.github.tototoshi" %% "scala-csv" % "1.3.5",
  "org.apache.poi" % "poi" % "3.14",

  "org.webjars" % "requirejs" % "2.1.22",
  "org.webjars" % "jquery" % "2.1.3",
  "org.webjars" %% "webjars-play" % "2.4.0-1",
  "org.webjars.bower" % "angular" % "1.6.6" exclude("org.webjars.bower", "jquery"),
  "org.webjars.bower" % "angular-route" % "1.6.6",
  "org.webjars.bower" % "angular-cookies" % "1.6.6",
  "org.webjars.bower" % "angular-sanitize" % "1.6.6",
  "org.webjars" % "highcharts" % "6.0.3",
  "org.webjars" % "highstock" % "6.0.3",
  "org.webjars" % "matchmedia-ng" % "1.0.5" exclude("org.webjars", "angularjs"),
  "org.webjars.bower" % "json-formatter" % "0.2.7",
  "org.webjars.bower" % "ng-file-upload" % "12.2.13" exclude("org.webjars.bower", "angular"),
  "org.webjars.bower" % "ng-csv" % "0.3.6",
  "org.webjars.bower" % "ngInfiniteScroll" % "1.3.0",
  "org.webjars.bower" % "moment" % "2.13.0",
  "org.webjars.bower" % "angular-xeditable" % "0.8.1",
  "org.webjars.npm" % "flatpickr" % "3.0.7",
  "net.sourceforge.htmlunit" % "htmlunit" % "2.27"

)

dependencyOverrides := Set(
  "org.webjars.bower" % "angular" % "1.6.6"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += "Artifactory Realm" at "https://jfrog.maalka.com/artifactory/libs-release-local/"


// Scala Compiler Options
scalacOptions in ThisBuild ++= Seq(
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-deprecation", // warning and location for usages of deprecated APIs
  "-feature", // warning and location for usages of features that should be imported explicitly
  "-unchecked", // additional warnings where generated code depends on assumptions
  "-Xlint", // recommended additional warnings
  "-Xcheckinit", // runtime error when a val is not initialized due to trait hierarchies (instead of NPE somewhere else)
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver
  "-Ywarn-value-discard", // Warn when non-Unit expression results are unused
  "-Ywarn-inaccessible",
  "-Ywarn-dead-code"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

//
// sbt-web configuration
// https://github.com/sbt/sbt-web
//

AngularTemplatesKeys.module := "angular.module('maalka-templates', [])"
AngularTemplatesKeys.naming := {value : String => value.replace("\\", "/")}

// Configure the steps of the asset pipeline (used in stage and dist tasks)
// rjs = RequireJS, uglifies, shrinks to one file, replaces WebJars with CDN
// digest = Adds hash to filename
// gzip = Zips all assets, Asset controller serves them automatically when client accepts them
pipelineStages := Seq(rjs, digest, gzip)

// RequireJS with sbt-rjs (https://github.com/sbt/sbt-rjs#sbt-rjs)
// ~~~
RjsKeys.paths := Map("jsRoutes" -> ("/jsroutes" -> "empty:"),
  "moment" -> ("../lib/moment/" -> "empty:"))

//RjsKeys.mainModule := "main"

JsEngineKeys.engineType := JsEngineKeys.EngineType.Node


excludeFilter in (Assets, LessKeys.less) := {
  val public = (baseDirectory.value / "public").getCanonicalPath
  new SimpleFileFilter({ f =>
    println("%s - %s".format(f.getCanonicalPath, (webJarsDirectory in Assets).value.getCanonicalPath))
    (f.getCanonicalPath startsWith public) ||
      (f.getCanonicalPath startsWith (webModuleDirectory in Assets).value.getCanonicalPath) ||
      (f.getCanonicalPath startsWith (webJarsDirectory in Assets).value.getCanonicalPath) ||
      f.isHidden
  })
}

includeFilter in rjs := GlobFilter("*.json") | GlobFilter("*.js") | GlobFilter("*.css") | GlobFilter("*.map")
excludeFilter in uglify := (excludeFilter in uglify).value || GlobFilter("*.min.js")


// Asset hashing with sbt-digest (https://github.com/sbt/sbt-digest)
// ~~~
// md5 | sha1
//DigestKeys.algorithms := "md5"
//includeFilter in digest := "..."
//excludeFilter in digest := "..."

// HTTP compression with sbt-gzip (https://github.com/sbt/sbt-gzip)
// ~~~
// includeFilter in GzipKeys.compress := "*.html" || "*.css" || "*.js"
// excludeFilter in GzipKeys.compress := "..."

// JavaScript linting with sbt-jshint (https://github.com/sbt/sbt-jshint)
// ~~~
// JshintKeys.config := ".jshintrc"

// All work and no play...
emojiLogs

