package com.palashmax.server.spring

import org.springframework.boot.context.properties.ConfigurationProperties

@org.springframework.context.annotation.Configuration
@ConfigurationProperties(prefix = "sock-server")
class SockServerConfiguration {

	lateinit var port: String
}

