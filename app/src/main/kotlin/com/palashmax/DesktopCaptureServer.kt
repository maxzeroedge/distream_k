package com.palashmax

import java.awt.Dimension
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.net.ServerSocket
import java.util.*
import java.util.concurrent.Executors
import javax.imageio.ImageIO

class DesktopCaptureServer: Closeable {
	private var portNumber: Int = 9000
	private var framesPerSecond: Long = 60
	private val clientSocketExecutors = Executors.newFixedThreadPool(5)
	// TODO: Use https://docs.oracle.com/javase/6/docs/api/java/awt/Robot.html for events

	private var screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
	private var robot: Robot = Robot()

	private fun captureScreenRobot(): BufferedImage? {
		return robot.createScreenCapture(Rectangle(screenSize))
	}

	private fun getScreenImage(): String {
		// val image = SwingFXUtils.toFXImage(captureScreenRobot(), null)
		val byteArrayStream = ByteArrayOutputStream()
		try {
			ImageIO.write(captureScreenRobot(), "png", byteArrayStream)
			return Base64.getEncoder().encodeToString(byteArrayStream.toByteArray())
		} catch (e: Exception) {
			e.printStackTrace()
		}
		return ""
	}

	fun createSocketServer(portNumber: Int = 9000) {
		this.portNumber = portNumber
		try {
			val socketServer = ServerSocket(this.portNumber)
			while(true) {
				val clientSocket = socketServer.accept()
				val workerThread = SocketRequestHandler(clientSocket, this.framesPerSecond) { getScreenImage() }
				clientSocketExecutors.execute(workerThread)

				/*var inputLine: String = inputReader.readLine()
				while( inputLine != null ) {
					outWriter.println(inputLine)
					inputLine = inputReader.readLine()
				}
				val frameGrabber = Runnable { outWriter.println(getScreenImage()) }
				timer = Executors.newSingleThreadScheduledExecutor()
				timer.scheduleAtFixedRate(frameGrabber, 0, 1000/framesPerSecond, TimeUnit.MILLISECONDS)
				*/
			}
		} catch(e: Exception) {
			e.printStackTrace()
		}
	}

	override fun close() {
		clientSocketExecutors.shutdown()
	}
}
