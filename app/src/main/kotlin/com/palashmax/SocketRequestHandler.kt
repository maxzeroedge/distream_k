package com.palashmax

import java.io.Closeable
import java.io.PrintWriter
import java.net.Socket
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class SocketRequestHandler(
	private val clientSocket: Socket,
	private val framesPerSecond: Long = 60,
	private val getScreenImageFun: ()->String
): Runnable, Closeable {

	private lateinit var timer: ScheduledExecutorService

	override fun run() {
		// val outWriter = PrintWriter(clientSocket.getOutputStream(), true)
		val outStream = clientSocket.getOutputStream()
		val inputReader = Scanner(clientSocket.getInputStream(), "UTF-8")

		// Each WebSocket is initially a GET request, that is upgraded to websocket
		val data = inputReader.useDelimiter("\\r\\n\\r\\n").next()
		val getMatcher = Pattern.compile("^GET").matcher(data)
		if(getMatcher.find()) {
			val websocketKeyMatcher = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data)
			websocketKeyMatcher.find()
			val acceptKey = Base64.getEncoder()
				.encodeToString(
					MessageDigest.getInstance("SHA-1")
						.digest((websocketKeyMatcher.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
							.toByteArray(Charset.forName("UTF-8"))))
			val response = (
				"""
				HTTP/1.1 101 Switching Protocols
				Connection: Upgrade
				Upgrade: websocket
				Sec-WebSocket-Accept: $acceptKey
				""".trimIndent() + "\r\n\r\n"
			).toByteArray(Charset.forName("UTF-8"))
			// outWriter.write(response)
			outStream.write(response, 0, response.size)
		}
		val frameGrabber = Runnable {
			val resp = getScreenImageFun.invoke().toByteArray(Charset.forName("UTF-8"))
			outStream.write(resp, 0, resp.size)
			// outWriter.println(getScreenImageFun.invoke())
		}
		timer = Executors.newSingleThreadScheduledExecutor()
		timer.scheduleAtFixedRate(frameGrabber, 0, 1000/framesPerSecond, TimeUnit.MILLISECONDS)
		println("Client Connected")
	}

	override fun close() {
		timer!!.shutdown()
	}
}