package com.palashmax.server


import spark.Spark


class SparkSocketServer (private val portNumber: Int = 9000) {
	fun setup() {
		Spark.webSocket("/display", SparkSocketEndpoint::class.java)
		// Spark.staticFileLocation("static")
		Spark.port(portNumber)
		Spark.init()
		//Spark.get("/display") { _, _ -> "Connected to display port" }
		//Spark.awaitInitialization()
		println("Ready")
	}

	fun stop() {
		Spark.stop()
	}
}