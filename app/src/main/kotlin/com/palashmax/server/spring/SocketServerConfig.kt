package com.palashmax.server.spring

import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOServer
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class SocketServerConfig(private val serverConfiguration: SockServerConfiguration) {

	@Bean
	fun socketIOServer(): SocketIOServer? {
		val config = Configuration()
		config.hostname = "localhost"
		config.port = serverConfiguration.port.toInt()
		return SocketIOServer(config)
	}
}