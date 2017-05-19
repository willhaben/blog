package at.willhaben.blog.simultations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.io.Source
import scala.language.postfixOps
import scala.util.{Properties, Random}

class DefaultSimulation extends Simulation {

  val httpProtocol = http
    .baseURL("http://localhost:8080/api/")
    .inferHtmlResources()
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate, sdch")
    .acceptLanguageHeader("de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4")
    .userAgentHeader("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36")

  val headers_0 = Map(
    "Content-Type" -> "application/json",
    "Accept" -> "application/json")

  val headers_1 = Map("Pragma" -> "no-cache")

  val scn = scenario("RecordedSimulation")
 .exec(http("getData")
      .get("")
      .headers(headers_0))

  setUp(scn.inject(rampUsers(1000) over (1 minutes)))
    .protocols(httpProtocol)
}