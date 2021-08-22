package com.palashmax

import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class SocketRequestHandler(
	private val clientSocket: Socket,
	private val framesPerSecond: Long = 60,
	private val getScreenImageFun: ()->String
): Runnable, Closeable {

	private lateinit var timer: ScheduledExecutorService

	override fun run() {
		val outWriter = PrintWriter(clientSocket.getOutputStream(), true)
		/*val inputReader = BufferedReader(
			InputStreamReader(clientSocket.getInputStream())
		)*/
		val frameGrabber = Runnable { outWriter.println(getScreenImageFun.invoke()) }
		timer = Executors.newSingleThreadScheduledExecutor()
		timer.scheduleAtFixedRate(frameGrabber, 0, 1000/framesPerSecond, TimeUnit.MILLISECONDS)
	}

	override fun close() {
		timer!!.shutdown()
	}
}