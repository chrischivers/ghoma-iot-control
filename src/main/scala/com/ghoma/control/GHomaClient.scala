package com.ghoma.control

import com.typesafe.scalalogging.StrictLogging
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Json, Reads}

import scalaj.http.{Http, HttpOptions}


class GHomaClient(config: Config) extends StrictLogging {

  import GHomaClient._

  def turnPlugOn(on: Boolean) = {

    implicit val reads: Reads[LoginResponse] = (
      (JsPath \ "result" \ "code").read[String] and
        (JsPath \ "result" \ "body" \ "accessToken").read[String] and
        (JsPath \ "result" \ "body" \ "expirationTime").read[Int]
      ) (LoginResponse.apply _)


    val loginResponse = sendLoginRequest(config.userAccount, config.passwordEncoded, config.clientToken, config.appKey)
    logger.info("Log in response: " + loginResponse)
    val json = Json.parse(loginResponse)
    val loginParsed = json.as[LoginResponse](reads)
    logger.info(loginParsed.toString)

    if (loginParsed.code == "10000") {
      val response =
        if (on) sendDeviceOnMessage(loginParsed.accessToken, config.deviceNo, config.appKey)
        else sendDeviceOffMessage(loginParsed.accessToken, config.deviceNo, config.appKey)
      logger.info("Operate Device Response: " + response)
    } else throw new RuntimeException(s"Invalid login response: ${loginParsed.code}")
  }

  private def sendLoginRequest(userAccount: String, passwordEncoded: String, clientToken: String, appKey: String): String = {
    val url = "https://rc.g-homa.com/api/V1/user/login"
    val body =
      s"""
         |
         |{
         |  "body":{
         |      "authentication":{
         |          "userAccount":"$userAccount",
         |          "password":"$passwordEncoded"
         |       },
         |      "baseInfo":{
         |          "zone":"Europe/London",
         |          "nation":"United Kingdom",
         |          "city":"England"
         |       },
         |       "clientToken":{
         |          "type":"2",
         |          "version":"1",
         |          "token":"$clientToken"
         |       }
         |     },
         |     "system":{
         |        "ver":"1.0.0",
         |        "sign":"252133cc70ce7a52ab2f141804f11c55",
         |        "appKey":"$appKey",
         |        "time":"1502644045",
         |        "clientSys":"android-6.0.1",
         |        "appVer":"3.0.19",
         |        "clientModel":"Vodafone Smart ultra 6"
         |     }
         | }
         |
         |""".stripMargin

    postHttpRequest(url, body)
  }

  private def sendDeviceOnMessage(accessToken: String, deviceNo: String, appKey: String): String = {
    val url = "https://rc.g-homa.com/api/V1/operation/device"

    val body =
      s"""
         |
        |{
         |   "body":{
         |       "accessToken":"$accessToken",
         |       "command":{
         |           "deviceNo":"$deviceNo",
         |           "action":"1",
         |           "key":"0",
         |           "value":"1"
         |       }
         |   },
         |   "system":{
         |       "ver":"1.0.0",
         |        "sign":"6122e7ddbfaf65fa0bc3e3a542828b5a",
         |        "appKey":"$appKey",
         |        "time":"1502644055",
         |        "clientSys":"android-6.0.1",
         |        "appVer":"3.0.19",
         |        "clientModel":"Vodafone Smart ultra 6"
         |    }
         |}
         |
      """.stripMargin

    postHttpRequest(url, body)
  }

  private def sendDeviceOffMessage(accessToken: String, deviceNo: String, appKey: String): String = {
    val url = "https://rc.g-homa.com/api/V1/operation/device"
    val body =
      s"""
         |{
         |   "body":{
         |       "accessToken":"$accessToken",
         |       "command":{
         |           "deviceNo":"$deviceNo",
         |           "action":"0",
         |           "key":"0",
         |           "value":"0"
         |       }
         |   },
         |   "system":{
         |       "ver":"1.0.0",
         |       "sign":"dcbf44a984dbafbb23581133258f96c4",
         |       "appKey":"$appKey",
         |       "time":"1502644057",
         |       "clientSys":"android-6.0.1",
         |       "appVer":"3.0.19",
         |       "clientModel":"Vodafone Smart ultra 6"
         |   }
         |}
          """.stripMargin

    postHttpRequest(url, body)
  }

  private def postHttpRequest(url: String, body: String): String = {
    logger.info(s"Posting message to $url. Message: $body")
    Http(url)
      .postData(body)
      .header("content-type", "application/json")
      .options(HttpOptions.followRedirects(true)).asString.body
  }
}

object GHomaClient {
  case class LoginResponse(code: String, accessToken: String, expirationTime: Int)
}
