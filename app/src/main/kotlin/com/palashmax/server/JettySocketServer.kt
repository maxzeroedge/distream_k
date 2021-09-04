package com.palashmax.server
//
//import org.eclipse.jetty.server.Server
//import org.eclipse.jetty.server.ServerConnector
//import org.eclipse.jetty.servlet.ServletContextHandler
//import org.eclipse.jetty.websocket.javax.server.config.JavaxWebSocketServletContainerInitializer
//
//class JettySocketServer(private val portNumber: Int = 9000) {
//	private val server: Server = Server()
//	private val serverConnector = ServerConnector(server)
//
//	init {
//		serverConnector.port = this.portNumber
//		server.addConnector(serverConnector)
//		val context = ServletContextHandler(ServletContextHandler.SESSIONS)
//		context.contextPath = "/"
//		server.handler = context
//		JavaxWebSocketServletContainerInitializer.configure(context) { _, wsContainer ->
//			wsContainer.defaultMaxTextMessageBufferSize = 65535
//			wsContainer.addEndpoint(SocketEndpoint::class.java)
//		}
//	}
//
//	fun start() {
//		server.start()
//	}
//
//	fun join() {
//		server.join()
//	}
//
//	fun stop() {
//		server.stop()
//	}
//}