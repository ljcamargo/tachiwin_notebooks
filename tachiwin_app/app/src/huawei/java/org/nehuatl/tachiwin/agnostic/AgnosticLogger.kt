package org.nehuatl.tachiwin.agnostic

import android.content.Context
import android.os.Bundle
import com.huawei.hms.analytics.HiAnalytics
import com.huawei.hms.analytics.HiAnalyticsInstance
import com.huawei.hms.analytics.HiAnalyticsTools
import java.util.*

open class AgnosticLogger(val context: Context) {

    var started = false

    private var loggers: List<Logger> = listOf(
        object: Logger(context) {
            var logger: HiAnalyticsInstance? = null
            override fun start() {
                logger = HiAnalytics.getInstance(context)
                HiAnalyticsTools.enableLog(6)
            }
            override fun log(string: String) {
                logger?.onEvent(string.toLowerCase(Locale.ROOT), Bundle())
            }
            override fun log(string: String, value: Double) {
                logger?.onEvent(
                    string.toLowerCase(Locale.ROOT),
                    Bundle().apply { putDouble("value", value) }
                )
            }
            override fun log(string: String, bundle: Bundle) {
                logger?.onEvent(string.toLowerCase(Locale.ROOT), bundle)
            }
        },
    )

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