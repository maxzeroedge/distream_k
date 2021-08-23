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

	fun convertToSendableFormat(inpuString: String) {
		// TODO: Encode Message to be sent to UI
	}

	override fun run() {
		// val outWriter = PrintWriter(clientSocket.getOutputStream(), true)
		val outStream = clientSocket.getOutputStream()
		val inputReader = Scanner(clientSocket.getInputStream(), "UTF-8")

		// Each WebSocket is initially a GET request, that is upgraded to websocket
		val data = inputReader.useDelimiter("\\r\\n\\r\\n").next()
		val getMatcher = Pattern.compile("^GET").matcher(data)
		if(getMatcher.find()) {
			val websocketKeyMatcher = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data)
			if(websocketKeyMatcher.find()){
				val websocketKey = websocketKeyMatcher.group(1)
				val acceptKey = Base64.getEncoder()
					.encodeToString(
						MessageDigest.getInstance("SHA-1")
							.digest(
								"${websocketKey}258EAFA5-E914-47DA-95CA-C5AB0DC85B11"
									.toByteArray(Charsets.UTF_8)
							)
					)
				val response = (
						"HTTP/1.1 101 Switching Protocols\r\nConnection: Upgrade\r\nUpgrade: websocket\r\nSec-WebSocket-Accept: $acceptKey\r\n\r\n"
						).toByteArray(Charsets.UTF_8)
				// outWriter.write(response)
				outStream.write(response, 0, response.size)
			}
		}
		val frameGrabber = Runnable {
			val resp = getScreenImageFun.invoke().toByteArray(Charsets.UTF_8)
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