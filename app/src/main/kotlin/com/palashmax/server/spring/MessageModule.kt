package com.palashmax.server.spring

import com.corundumstudio.socketio.HandshakeData
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIONamespace
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.ConnectListener
import com.corundumstudio.socketio.listener.DataListener
import com.corundumstudio.socketio.listener.DisconnectListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MessageModule(final val server: SocketIOServer) {

	private final val namespace: SocketIONamespace = server.addNamespace("/display")

	public lateinit var connectedEventHandler: (SocketIOClient)->Unit

	companion object {
		private val log: Logger = LoggerFactory.getLogger(this::class.java)
	}

	init {
		this.namespace.addConnectListener(onConnected())
		this.namespace.addDisconnectListener(onDisconnected())
		this.namespace.addEventListener("mouseEvent", SocketMessage::class.java, onMouseEventReceived())
	}

	private fun onMouseEventReceived(): DataListener<SocketMessage> {
		return DataListener<SocketMessage> { client, data, _ ->
			log.debug("Client[{}] - Received chat message '{}'", client.sessionId.toString(), data);
			// namespace.broadcastOperations.sendEvent("image", data)
		};
	}

	private fun onConnected(): ConnectListener {
		return ConnectListener { client ->
			val handshakeData: HandshakeData = client.handshakeData
			log.debug(
				"Client[{}] - Connected to chat module through '{}'",
				client.sessionId.toString(),
				handshakeData.url
			)
			connectedEventHandler(client)
		}
	}

	private fun onDisconnected(): DisconnectListener {
		return DisconnectListener { client ->
			log.debug("Client[{}] - Disconnected from chat module.", client.sessionId.toString())
		}
	}

}