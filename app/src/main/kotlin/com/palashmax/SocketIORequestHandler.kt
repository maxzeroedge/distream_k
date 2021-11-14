package com.palashmax

import com.corundumstudio.socketio.SocketIOClient
import java.io.Closeable
import java.io.DataOutputStream
import java.net.Socket
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

class SocketIORequestHandler(
	private val clientSocket: SocketIOClient,
	private val framesPerSecond: Long = 60,
	private val getScreenImageFun: ()->String
): Runnable, Closeable {

	private lateinit var timer: ScheduledExecutorService

	override fun run() {
		val frameGrabber = Runnable {
			clientSocket.sendEvent("image", getScreenImageFun())
		}
		timer = Executors.newSingleThreadScheduledExecutor()
		timer.scheduleAtFixedRate(frameGrabber, 0, 1000/framesPerSecond, TimeUnit.MILLISECONDS)
		println("Client Connected")
	}

	override fun close() {
		timer!!.shutdown()
	}
}