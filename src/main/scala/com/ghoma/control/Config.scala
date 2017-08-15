package com.ghoma.control

import com.typesafe.config.ConfigFactory

case class Config(userAccount: String, passwordEncoded: String, clientToken: String, appKey: String, deviceNo: String)

object ConfigLoader {

  private val defaultConfigFactory = ConfigFactory.load()

  val defaultConfig: Config = {
    Config(
      defaultConfigFactory.getString("credentials.user_account"),
      defaultConfigFactory.getString("credentials.password_encoded"),
      defaultConfigFactory.getString("credentials.client_token"),
      defaultConfigFactory.getString("credentials.appKey"),
      defaultConfigFactory.getString("credentials.deviceNo")
    )
  }
}