package com.palashmax

import javafx.application.Application
import javafx.event.EventHandler
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Scene
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.stage.Stage
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.swing.Timer
import kotlin.properties.Delegates

class DesktopCaptureServer: Application() {
	private var framesPerSecond: Long = 60
	private lateinit var timer: ScheduledExecutorService
	// TODO: Use https://docs.oracle.com/javase/6/docs/api/java/awt/Robot.html for events

	private var screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
	private var robot: Robot = Robot()

	private fun captureScreenRobot(): BufferedImage? {
		return robot.createScreenCapture(Rectangle(screenSize))
	}

	private fun updateScreenImage(imageView: ImageView) {
		imageView.image = SwingFXUtils.toFXImage(captureScreenRobot(), null)
	}

	private fun attachMouseMoveEvent(imageView: ImageView) {
		val mouseEventHandler = EventHandler<MouseEvent> { println("Mouse Moved: ${it.sceneX}, ${it.sceneY}") }
		imageView.addEventFilter(MouseEvent.MOUSE_MOVED, mouseEventHandler)
	}
	
	fun attachMouseClickEvent(imageView: ImageView) {
		val mouseEventHandler = EventHandler<MouseEvent> { println("Mouse Clicked:" + it.clickCount) }
		imageView.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEventHandler)
	}

	override fun start(primaryStage: Stage?) {
		val imageView = ImageView()
		updateScreenImage(imageView)
		imageView.fitWidth = screenSize.width * 0.8
		imageView.fitHeight = screenSize.height * 0.8
		attachMouseMoveEvent(imageView)
		attachMouseClickEvent(imageView)
		val hBox = HBox(imageView)
		val scene = Scene(hBox)
		primaryStage!!.scene = scene
		primaryStage.show()
		val frameGrabber = Runnable { updateScreenImage(imageView) }
		timer = Executors.newSingleThreadScheduledExecutor()
		timer.scheduleAtFixedRate(frameGrabber, 0, 1000/framesPerSecond, TimeUnit.MILLISECONDS)
	}

	fun launchApp(fps: Long = 60){
		framesPerSecond = fps
		launch()
	}

	override fun stop() {
		timer.shutdown()
	}
}
