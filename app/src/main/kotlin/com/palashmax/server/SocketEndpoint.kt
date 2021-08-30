package com.palashmax.server

import com.palashmax.DesktopCaptureServer
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.websocket.*
import javax.websocket.server.ServerEndpoint

@ClientEndpoint
@ServerEndpoint("/display")
//@Component
class SocketEndpoint { //: Endpoint()
	private val desktopCaptureServer = DesktopCaptureServer.getSelfInstance()

	private val chatEndpoints: Set<SocketEndpoint> = CopyOnWriteArraySet()
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

	/*override fun onOpen(session: Session?, config: EndpointConfig?) {
		if (session != null) {
			this.onOpen(session)
		}
	}*/

	@OnError
	/*override*/ fun onError(session: Session, throwable: Throwable) {
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