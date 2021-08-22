package com.palashmax

import com.sun.xml.internal.ws.api.message.Message
import org.springframework.beans.factory.annotation.Autowired
import java.nio.charset.Charset
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.websocket.*
import javax.websocket.server.ServerEndpoint

@ServerEndpoint("/display")
//@Component
class SocketEndpoint {
	@Autowired
	private lateinit var desktopCaptureServer: DesktopCaptureServer

	private val chatEndpoints: Set<SocketEndpoint> = CopyOnWriteArraySet<SocketEndpoint>()
	private lateinit var session: Session
	private val timer = Executors.newSingleThreadScheduledExecutor()

	@OnOpen
	fun onOpen(session: Session) {
		this.session = session
		chatEndpoints.plus(this)
		val frameGrabber = Runnable {
			val resp = desktopCaptureServer.screenImage
			broadcast(mapOf(resp to "data"))
		}
		timer.scheduleAtFixedRate(frameGrabber, 0, 1000/desktopCaptureServer.framesPerSecond, TimeUnit.MILLISECONDS)
	}

	@OnMessage
	fun onMessage(session: Session, message: Map<String, Any>) {
		// TODO:
	}

	@OnClose
	fun onClose(session: Session) {
		// TODO:
		chatEndpoints.minus(this)
		timer.shutdown()
	}

	@OnError
	fun onError(session: Session, throwable: Throwable) {
		// TODO:
	}

	private fun broadcast(message: Map<String, Any>) {
		chatEndpoints.parallelStream().forEach {
			try {
				it.session.basicRemote.sendObject(message)
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}
}