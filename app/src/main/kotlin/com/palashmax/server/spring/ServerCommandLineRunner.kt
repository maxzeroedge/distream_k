package com.palashmax.server.spring

import com.corundumstudio.socketio.SocketIOServer
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class ServerCommandLineRunner(val server: SocketIOServer) : CommandLineRunner {
	@Throws(Exception::class)
	override fun run(vararg args: String) {
		server.start()
	}
}
