package com.palashmax

//import com.palashmax.server.TomcatSocketServer
import com.palashmax.server.DistreamSpringApplication
import com.palashmax.server.SparkSocketServer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
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
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

@Component
class DesktopCaptureServer: Closeable {

	@Value("\${host.port}")
	private var portNumber: Int = 9000

	@Value("\${system.fps}")
	var framesPerSecond: Long = 60

	private val clientSocketExecutors = Executors.newFixedThreadPool(5)
	private val timer = Executors.newSingleThreadScheduledExecutor()
	private lateinit var socketServer: ServerSocket
	private lateinit var webSocketServer: SparkSocketServer
	// TODO: Use https://docs.oracle.com/javase/6/docs/api/java/awt/Robot.html for events

	private var screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
	private var robot: Robot = Robot()
	var screenImage: String = ""

	init {
		runScreenImageUpdater()
	}

	companion object {
		fun getSelfInstance(): DesktopCaptureServer {
			return DesktopCaptureServer()
		}
	}
	private fun captureScreenRobot(): BufferedImage? {
		return robot.createScreenCapture(Rectangle(screenSize))
	}

	private fun generateScreenImage(): String {
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

	private fun updateScreenImage() {
		this.screenImage = generateScreenImage()
	}

	private fun runScreenImageUpdater() {
		val frameGrabber = Runnable { updateScreenImage() }
		timer.scheduleAtFixedRate(frameGrabber, 0, 1000/framesPerSecond, TimeUnit.MILLISECONDS)
	}

	fun createSocketServer(portNumber: Int = 9000) {
		this.portNumber = portNumber
		try {
			socketServer = ServerSocket(this.portNumber)
			runScreenImageUpdater()
			while(true) {
				val clientSocket = socketServer.accept()
				val workerThread = SocketRequestHandler(clientSocket, this.framesPerSecond) { this.screenImage }
				clientSocketExecutors.execute(workerThread)
			}
		} catch(e: Exception) {
			e.printStackTrace()
		}
	}

	fun createWebSocketServer(portNumber: Int = 9000) {
		/*this.portNumber = portNumber
		this.webSocketServer = SparkSocketServer(portNumber)
		webSocketServer.setup()*/
		runApplication<DistreamSpringApplication>()
	}

	override fun close() {
		socketServer.close()
		webSocketServer.stop()
		timer.shutdown()
		clientSocketExecutors.shutdown()
	}
}
