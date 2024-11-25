package org.nehuatl.tachiwin.agnostic

import android.content.Context
import android.os.Bundle
import java.util.*

open class AgnosticLogger(val context: Context) {

    var started = false

    private var loggers: List<Logger> = listOf()

    fun start() {
        if (started) return
        loggers.forEach { it.start() }
        started = true
    }

    fun log(any: Any) {
        log(any.toString())
    }

    fun log(string: String) {
        loggers.forEach { it.log(string) }
    }

    fun log(string: String, value: Double) {
        loggers.forEach { it.log(string, value) }
    }

    fun log(string: String, bundle: Bundle) {
        loggers.forEach { it.log(string, bundle) }
    }

    open class Logger(val context: Context) {
        open fun start() {}
        open fun log(string: String) {}
        open fun log(string: String, value: Double) {}
        open fun log(string: String, bundle: Bundle) {}
    }
}