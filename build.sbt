val MunitVersion           = "1.0.0-M1"
val LogbackVersion         = "1.2.6"
val MunitCatsEffectVersion = "1.0.6"
val DoobieVersion          = "1.0.0-RC1"
val CirceVersion           = "0.14.1"
val zioVersion             = "2.0.1"
val zioJsonVersion         = "0.4.2"
val zioHttpVersion         = "2.0.0-RC10"
val quillVersion           = "4.6.0"
val tapirVersion           = "1.0.2" 

lazy val root = (project in file("."))
  .settings(
    organization := "io.github.samuelc92",
    name := "booking",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "3.2.2",
    libraryDependencies ++= Seq(
      "dev.zio"         %% "zio"                      % zioVersion,
      "io.d11"          %% "zhttp"                    % zioHttpVersion,
      "io.getquill"     %% "quill-zio"                % quillVersion,
      "io.getquill"     %% "quill-jdbc-zio"           % quillVersion,
      "com.h2database"  % "h2"                        % "2.1.214",
      "org.scalameta"   %% "munit"                    % MunitVersion           % Test,
      "org.typelevel"   %% "munit-cats-effect-3"      % MunitCatsEffectVersion % Test,
      "ch.qos.logback"  %  "logback-classic"          % LogbackVersion,
      "org.tpolecat"    %% "doobie-core"              % DoobieVersion,
      "org.tpolecat"    %% "doobie-postgres"          % DoobieVersion,
      "org.tpolecat"    %% "doobie-hikari"            % DoobieVersion,
      // Optional for auto-derivation of JSON codecs
      "io.circe"        %% "circe-generic"            % CirceVersion,
      "io.circe"        %% "circe-core"               % CirceVersion,

      "com.softwaremill.sttp.tapir" %% "tapir-core"              % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server"   % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-zio"          % tapirVersion
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )
