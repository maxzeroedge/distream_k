package com.palashmax
//
//import javafx.application.Application
//import javafx.embed.swing.SwingFXUtils
//import javafx.event.EventHandler
//import javafx.scene.Scene
//import javafx.scene.image.ImageView
//import javafx.scene.image.WritableImage
//import javafx.scene.input.MouseEvent
//import javafx.scene.layout.HBox
//import javafx.stage.Stage
//import java.awt.Dimension
//import java.awt.Toolkit
//import java.io.BufferedReader
//import java.io.ByteArrayInputStream
//import java.io.InputStreamReader
//import java.io.PrintWriter
//import java.net.Socket
//import java.util.concurrent.ScheduledExecutorService
//import javax.imageio.ImageIO
//
//class DisplayStreamerClient: Application()  {
//	private var framesPerSecond: Long = 60
//	private lateinit var clientSocket: Socket
//	private lateinit var timer: ScheduledExecutorService
//	private val imageView = ImageView()
//
//	// Using https://docs.oracle.com/javase/6/docs/api/java/awt/Robot.html for events
//
//	private var screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
//
//	private fun attachMouseMoveEvent(imageView: ImageView) {
//		val mouseEventHandler = EventHandler<MouseEvent> { println("Mouse Moved: ${it.sceneX}, ${it.sceneY}") }
//		imageView.addEventFilter(MouseEvent.MOUSE_MOVED, mouseEventHandler)
//	}
//
//	fun attachMouseClickEvent(imageView: ImageView) {
//		val mouseEventHandler = EventHandler<MouseEvent> { println("Mouse Clicked:" + it.clickCount) }
//		imageView.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEventHandler)
//	}
//
//	fun stringToWriteableImage(inputString: String): WritableImage? {
//		val bufferedImage = ImageIO.read(ByteArrayInputStream(inputString.toByteArray()))
//		return SwingFXUtils.toFXImage(bufferedImage, null)
//	}
//
//	override fun start(primaryStage: Stage?) {
//		imageView.fitWidth = screenSize.width * 0.8
//		imageView.fitHeight = screenSize.height * 0.8
//		attachMouseMoveEvent(imageView)
//		attachMouseClickEvent(imageView)
//		val hBox = HBox(imageView)
//		val scene = Scene(hBox)
//		primaryStage!!.scene = scene
//		primaryStage.show()
//	}
//
//	fun launchApp(fps: Long = 60, hostName: String = "localhost", portNumber: Int = 9000){
//		clientSocket = Socket(hostName, portNumber)
//		// val outWriter = PrintWriter(clientSocket.getOutputStream(), true)
//		val inputReader = BufferedReader(
//			InputStreamReader(clientSocket.getInputStream())
//		)
//		imageView.image = stringToWriteableImage(inputReader.readLine())
//		framesPerSecond = fps
//		launch()
//	}
//
//	override fun stop() {
//		timer!!.shutdown()
//	}
//}