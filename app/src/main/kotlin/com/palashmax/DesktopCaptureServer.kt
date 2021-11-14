package com.palashmax

//import com.palashmax.server.TomcatSocketServer
import com.corundumstudio.socketio.SocketIOServer
import com.palashmax.server.DistreamSpringApplication
import com.palashmax.server.SparkSocketServer
import com.palashmax.server.spring.MessageModule
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.ConfigurableApplicationContext
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
	var framesPerSecond: Long = 1 // with 60 it may crash system due to unprocessed images

	private val clientSocketExecutors = Executors.newFixedThreadPool(5)
	private val timer = Executors.newSingleThreadScheduledExecutor()
	private lateinit var appContext: ConfigurableApplicationContext
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

	fun createWebSocketServer(portNumber: Int = 9000) {
		appContext = runApplication<DistreamSpringApplication>("--sock-server.port=${portNumber}")
		try {
			runScreenImageUpdater()
			val clientSocket = appContext.getBean(MessageModule::class.java)
			clientSocket.connectedEventHandler =  { client ->
				val workerThread = SocketIORequestHandler(client, this.framesPerSecond) { this.screenImage }
				clientSocketExecutors.execute(workerThread)
			}
		} catch(e: Exception) {
			e.printStackTrace()
		}
	}

	override fun close() {
		appContext.close()
		timer.shutdown()
		clientSocketExecutors.shutdown()
	}
}
