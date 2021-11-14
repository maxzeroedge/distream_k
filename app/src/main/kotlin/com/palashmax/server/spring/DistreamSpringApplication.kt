package com.palashmax.server

import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component


@SpringBootApplication
class DistreamSpringApplication

// Works with Spring Boot Websocket
//@Controller
//class DistreamSpringController {
//
//	init {
//		val desktopCaptureServer = DesktopCaptureServer.getSelfInstance()
//	}
//
//	@CrossOrigin(originPatterns = ["localhost"])
//	@MessageMapping("/display")
//	@SendTo("/topic/display")
//	fun displayServer(): String {
//		// TODO: Process display request
//		return "Hello"
//	}
//}
//
//
//@Configuration
//@EnableWebSocketMessageBroker
//class DistreamSpringWebSocketConfig : WebSocketMessageBrokerConfigurer {
//	override fun configureMessageBroker(config: MessageBrokerRegistry) {
//		config.enableSimpleBroker("/topic")
//		config.setApplicationDestinationPrefixes("/app")
//	}
//
//	override fun registerStompEndpoints(registry: StompEndpointRegistry) {
//		registry.addEndpoint("/display").setAllowedOriginPatterns("http://localhost:[*] ").withSockJS()
//	}
//
//	fun addCorsMappings(registry: CorsRegistry) {
//		registry.addMapping("/**")
//			.allowedOrigins("*")
//			.allowCredentials(false)
//			.maxAge(3600)
//			.allowedHeaders(
//				"Accept", "Content-Type", "Origin",
//				"Authorization", "X-Auth-Token"
//			)
//			.exposedHeaders("X-Auth-Token", "Authorization")
//			.allowedMethods("POST", "GET", "DELETE", "PUT", "OPTIONS")
//	}
//}
//
//@Component
//class SimpleCORSFilter : Filter {
//
//	@Throws(IOException::class, ServletException::class)
//	override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
//		val requestToUse = servletRequest as HttpServletRequest
//		val responseToUse = servletResponse as HttpServletResponse
//		val originUrl = requestToUse.getHeader("Origin")
//		responseToUse.setHeader("Access-Control-Allow-Origin", originUrl)
//		responseToUse.setHeader("Access-Control-Allow-Credentials", "true")
//		filterChain.doFilter(requestToUse, responseToUse)
//	}
//
//	override fun destroy() {}
//
////	companion object {
////		private val LOGGER: Logger = LoggerFactory.getLogger(SimpleCORSFilter::class.java)
////	}
//}