package org.nehuatl.tachiwin

import android.app.ActivityManager
import android.content.Context
import java.io.File

object DevicePerformanceHelper {

    enum class Performance {
        LOW,
        MEDIUM,
        HIGH
    }

    data class DeviceMetrics(
        val ramMB: Int,
        val cpuCoreCount: Int,
        val cpuMaxFrequency: Float
    )

    fun getDevicePerformanceMetrics(context: Context): DeviceMetrics {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val ramInMB = (memoryInfo.totalMem / (1024 * 1024)).toInt() // RAM in MB

        // CPU information
        val coreCount = Runtime.getRuntime().availableProcessors()
        val maxCpuFreq = File("/proc/cpuinfo").readLines()
            .also {
                "cpu info: $it".v()
            }
            .find { it.contains("cpu MHz") }
            ?.split(":")?.last()?.trim()?.toFloatOrNull() ?: 0f

        return DeviceMetrics(ramInMB, coreCount, maxCpuFreq)
    }

    fun categorizeDevicePerformance(metrics: DeviceMetrics): Performance {
        "device metrics: $metrics".v()
        return when {
            (
                    (metrics.ramMB >= 4096)
                            && (metrics.cpuCoreCount >= 6)
                    // && (metrics.cpuMaxFrequency >= 2000)
                    ) -> Performance.HIGH
            (
                    metrics.ramMB in 2048..4096
                            && metrics.cpuCoreCount >= 4
                    // && metrics.cpuMaxFrequency >= 1500
                    ) -> Performance.MEDIUM
            else -> Performance.LOW
        }
    }

    fun performance(context: Context): Performance {
        val metrics = getDevicePerformanceMetrics(context)
        return categorizeDevicePerformance(metrics)
    }
}
