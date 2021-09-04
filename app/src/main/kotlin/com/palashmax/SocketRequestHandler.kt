package com.palashmax

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

class SocketRequestHandler(
	private val clientSocket: Socket,
	private val framesPerSecond: Long = 60,
	private val getScreenImageFun: ()->String
): Runnable, Closeable {

	private lateinit var timer: ScheduledExecutorService
	private val BYTES_PER_REQUEST = (2.0.pow(64) - 1).roundToInt()
	private val BYTES_PER_REQUEST_16 = (2.0.pow(16) - 1).roundToInt()
	private val ENABLE_MASKING = false

	fun convertToSendableFormat(inputString: String) {
		// TODO: Encode Message to be sent to UI
		// https://datatracker.ietf.org/doc/html/rfc6455#section-5.2
		val arrayOfBytes = mutableListOf<BitSet>()
		val stringBytes = inputString.toByteArray(Charsets.UTF_8)
		val payloadSize = stringBytes.size
		var i = 0
		while(i*BYTES_PER_REQUEST < payloadSize) {
			var currentSize = 0
			val bodyByteArray = BitSet()
			// Add FIN bit
			if(stringBytes.size > BYTES_PER_REQUEST) {
				// This is not the final frame
				if((i+1)*BYTES_PER_REQUEST < payloadSize) {
					bodyByteArray.set(currentSize++, false)
				}
			} else {
				// This is final frame
				bodyByteArray.set(currentSize++, true)
			}

			// Add RSV-1,2,3 Bits
			bodyByteArray.set(currentSize, currentSize+2, false)
			currentSize += 3

			// Add Opcode Bit: Sending as Text so 0x1, else, 0x2 for binary
			bodyByteArray.set(currentSize, currentSize+2, false)
			currentSize += 3
			bodyByteArray.set(currentSize++, true)

			// TODO: Set Masking Bit as 1 and Masking Key as "Something"
			// Set Masking Bit as 0
			bodyByteArray.set(currentSize++, ENABLE_MASKING)

			// Set Payload Length
			val currentPayloadSize = min(payloadSize, BYTES_PER_REQUEST)
			var payloadLen = 0
			payloadLen = if(currentPayloadSize <= 125) {
				currentPayloadSize
			} else {
				if(currentPayloadSize <= BYTES_PER_REQUEST_16) {
					126
				} else {
					127
				}
			}

			// Convert payload_len to Binary
			bodyByteArray.set(currentSize, currentSize+7, false)
			var payloadBin = payloadLen
			for(j in 7 downTo 0) {
				if(payloadBin % 2 == 1) {
					bodyByteArray.set(currentSize + j, true)
				} else {
					bodyByteArray.set(currentSize + j, false)
				}
				payloadBin /= 2
			}


			// Set Masking Key if Masking Bit is 1: 0 or 4 bytes
			if(ENABLE_MASKING) {
				// TODO: Maybe
			}

			// Set Payload
			var currentBit = 0
			for(j in 0 until min(payloadSize, BYTES_PER_REQUEST)) {
				for(k in 0..7) {
					currentBit = (stringBytes[(i*BYTES_PER_REQUEST) + j].toInt() shr k) and 1
					if(currentBit == 0) {
						bodyByteArray.set(currentSize++, false)
					} else {
						bodyByteArray.set(currentSize++, true)
					}
				}
			}

			i += 1
			arrayOfBytes.add(bodyByteArray)
		}
	}

	override fun run() {
		// val outWriter = PrintWriter(clientSocket.getOutputStream(), true)
		val outStream = clientSocket.getOutputStream()
		val dataOut = DataOutputStream(outStream)
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
			val resp = getScreenImageFun.invoke() //.toByteArray(Charsets.UTF_8)
			//outStream.write(resp, 0, resp.size)
			dataOut.writeUTF(resp)
			/*dataOut.flush()
			dataOut.close()*/
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