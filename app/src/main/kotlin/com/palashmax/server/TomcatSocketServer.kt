//package com.palashmax.server
//
//import org.apache.catalina.startup.Tomcat
//import java.io.File
//import java.io.FileInputStream
//import java.io.FileOutputStream
//import java.io.IOException
//import java.nio.channels.FileChannel
//
//
//class TomcatSocketServer(private val portNumber: Int = 9000) {
//	//https://github.com/robmayhew/embedded-tomcat-websocket-example/blob/master/src/main/java/WebSocketServer.java
//	private val tomcat = Tomcat()
//
//	fun setup() {
//		tomcat.setPort(portNumber)
//		tomcat.addWebapp("/", File(setupWebapp()).absolutePath)
//		tomcat.start()
//		tomcat.server.await()
//
//	}
//
//	@Throws(Exception::class)
//	fun setupWebapp(): String {
//		val jarFile = File(SocketEndpoint::class.java.protectionDomain.codeSource.location.toURI().path)
//		val target = "tempwebapp"
//		val targetFolder = File("$target${File.separator}WEB-INF${File.separator}lib")
//		targetFolder.mkdirs()
//		copyFileUsingChannel(jarFile, File(targetFolder.path + File.separator + "app.jar"))
//		return target
//	}
//
//	@Throws(IOException::class)
//	private fun copyFileUsingChannel(source: File, dest: File) {
//		var sourceChannel: FileChannel? = null
//		var destChannel: FileChannel? = null
//		try {
//			// Access Denied here :(
//			sourceChannel = FileInputStream(source).channel
//			destChannel = FileOutputStream(dest).channel
//			destChannel.transferFrom(sourceChannel, 0, sourceChannel.size())
//		} catch (e: Exception) {
//			e.printStackTrace()
//		} finally {
//			sourceChannel!!.close()
//			destChannel!!.close()
//		}
//	}
//
//	fun stop() {
//		tomcat.stop()
//	}
//}