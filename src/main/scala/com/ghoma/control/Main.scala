package com.ghoma.control

import com.typesafe.scalalogging.StrictLogging

object Main extends App with StrictLogging {

  def turnOn = {
    val config = ConfigLoader.defaultConfig
    val client = new GHomaClient(config)
    client.turnPlugOn(true)
  }
  def turnOff = {
    val config = ConfigLoader.defaultConfig
    val client = new GHomaClient(config)
    client.turnPlugOn(false)
  }

}