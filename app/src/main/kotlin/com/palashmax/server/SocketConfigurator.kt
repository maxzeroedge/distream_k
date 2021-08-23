//package com.palashmax.server
//
//import javax.websocket.server.ServerEndpointConfig
//
//class SocketConfigurator : ServerEndpointConfig.Configurator() {
//
//	private val aSocketManagerObject: ManagerObject? = null
//
//	fun EventsConfigurator(aSocketManagerObject: ManagerObject?) {
//		this.aSocketManagerObject = aSocketManagerObject
//	}
//
//	@Throws(InstantiationException::class)
//	override fun <T> getEndpointInstance(endpointClass: Class<T>): T {
//		return SocketEndpoint(aSocketManagerObject) as T // This constructor to be added to your MyEndpoint class
//	}
//}