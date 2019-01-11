import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% "bootstrap-play-25"          % "4.6.0",
    "uk.gov.hmrc"             %% "govuk-template"             % "5.26.0-play-25",
    "uk.gov.hmrc"             %% "play-reactivemongo"         % "6.2.0",
    "uk.gov.hmrc"             %% "play-ui"                    % "7.27.0-play-25",
    "uk.gov.hmrc"             %% "play-json-union-formatter"  % "1.4.0",
    "uk.gov.hmrc"             %% "play-scheduling"            % "5.4.0"
  )

  private lazy val scope = "test, it"

  val test: Seq[ModuleID] = Seq(
    "com.github.tomakehurst"  %  "wiremock"                 % "2.20.0"                % scope,
    "org.mockito"             %  "mockito-core"             % "2.23.4"                % scope,
    "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % scope,
    "org.scalatest"           %% "scalatest"                % "3.0.4"                 % scope,
    "com.typesafe.play"       %% "play-test"                % current                 % scope,
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "2.0.1"                 % scope,
    "uk.gov.hmrc"             %% "hmrctest"                 % "3.3.0"                 % scope,
    "uk.gov.hmrc"             %% "service-integration-test" % "0.2.0"                 % scope,
    "uk.gov.hmrc"             %% "reactivemongo-test"       % "3.1.0"                 % scope
  )

}