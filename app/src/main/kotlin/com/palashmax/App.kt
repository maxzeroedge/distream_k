/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.palashmax

class App {
    val greeting: String
        get() {
            return "Hello World!"
        }
}

fun main() {
    println(App().greeting)
    DesktopCaptureServer().launchApp()
}
