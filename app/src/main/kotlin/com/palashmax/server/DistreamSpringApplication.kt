package com.palashmax.server

import com.palashmax.DesktopCaptureServer
import org.slf4j.Logger
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import java.io.IOException
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@SpringBootApplication
class DistreamSpringApplication

@Controller
class DistreamSpringController {

	init {
		val desktopCaptureServer = DesktopCaptureServer.getSelfInstance()
	}

	@CrossOrigin(originPatterns = ["localhost"])
	@MessageMapping("/display")
	@SendTo("/topic/display")
	fun displayServer(): String {
		// TODO: Process display request
		return "Hello"
	}
}


@Configuration
@EnableWebSocketMessageBroker
class DistreamSpringWebSocketConfig : WebSocketMessageBrokerConfigurer {
	override fun configureMessageBroker(config: MessageBrokerRegistry) {
		config.enableSimpleBroker("/topic")
		config.setApplicationDestinationPrefixes("/app")
	}

	override fun registerStompEndpoints(registry: StompEndpointRegistry) {
		registry.addEndpoint("/gs-guide-websocket").setAllowedOriginPatterns("http://localhost:[*] ").withSockJS()
	}

	fun addCorsMappings(registry: CorsRegistry) {
		registry.addMapping("/**")
			.allowedOrigins("*")
			.allowCredentials(false)
			.maxAge(3600)
			.allowedHeaders(
				"Accept", "Content-Type", "Origin",
				"Authorization", "X-Auth-Token"
			)
			.exposedHeaders("X-Auth-Token", "Authorization")
			.allowedMethods("POST", "GET", "DELETE", "PUT", "OPTIONS")
	}
}

@Component
class SimpleCORSFilter : Filter {
//	@Throws(ServletException::class)
//	override fun init(filterConfig: FilterConfig?) {
//		LOGGER.info("Initilisation du Middleware")
//	}

	@Throws(IOException::class, ServletException::class)
	override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
		val requestToUse = servletRequest as HttpServletRequest
		val responseToUse = servletResponse as HttpServletResponse
		val originUrl = requestToUse.getHeader("Origin")
		responseToUse.setHeader("Access-Control-Allow-Origin", originUrl)
		responseToUse.setHeader("Access-Control-Allow-Credentials", "true")
		filterChain.doFilter(requestToUse, responseToUse)
	}

	override fun destroy() {}

//	companion object {
//		private val LOGGER: Logger = LoggerFactory.getLogger(SimpleCORSFilter::class.java)
//	}
}