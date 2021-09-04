//package com.palashmax.server
//
//import com.google.gson.Gson
//import com.palashmax.DesktopCaptureServer
//import org.eclipse.jetty.websocket.api.Session
//import org.eclipse.jetty.websocket.api.annotations.*
//import java.io.IOException
//import java.nio.ByteBuffer
//import java.util.concurrent.CopyOnWriteArraySet
//import java.util.concurrent.Executors
//import java.util.concurrent.TimeUnit
//
//
//@WebSocket
//class SparkSocketEndpoint {
//	init {
//		println("Initiated socket endpoint")
//	}
//	private val desktopCaptureServer = DesktopCaptureServer.getSelfInstance()
//	private val chatEndpoints: Set<SparkSocketEndpoint> = CopyOnWriteArraySet()
//	private lateinit var session: Session
//	private val timer = Executors.newSingleThreadScheduledExecutor()
//
//	@OnWebSocketConnect
//	fun onOpen(session: Session){
//		println("Someone Connected")
//		this.session = session
//		chatEndpoints.plus(this)
//		val frameGrabber = Runnable {
//			val resp = desktopCaptureServer.screenImage
//			broadcast(mapOf(resp to "data"))
//		}
//		timer.scheduleAtFixedRate(frameGrabber, 0, 1000/desktopCaptureServer.framesPerSecond, TimeUnit.MILLISECONDS)
//	}
//
//	@OnWebSocketMessage
//	fun handleTextMessage(session: Session, message: String?) {
//		println("New Text Message Received")
//		session.remote.sendString(message)
//	}
//
//	@OnWebSocketMessage
//	fun handleBinaryMessage(session: Session, buffer: ByteArray?, offset: Int, length: Int) {
//		println("New Binary Message Received")
//		session.remote.sendBytes(ByteBuffer.wrap(buffer))
//	}
//
//	@OnWebSocketClose
//	fun onClose(session: Session, statusCode: Int, reason: String?) {
//		chatEndpoints.minus(this)
//		timer.shutdown()
//	}
//
//	@OnWebSocketError
//	fun onError(session: Session, error: Throwable) {
//		error.message?.let { this.onClose(session, 500, it) }
//	}
//
//	private fun broadcast(message: Map<String, Any>) {
//		chatEndpoints.parallelStream().forEach {
//			try {
//				it.session.remote.sendString(Gson().toJson(message))
//			} catch (e: Exception) {
//				e.printStackTrace()
//			}
//		}
//	}
//}