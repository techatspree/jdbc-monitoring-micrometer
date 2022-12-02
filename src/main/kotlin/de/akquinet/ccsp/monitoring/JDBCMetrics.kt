package de.akquinet.ccsp.monitoring

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Gauge
import java.util.concurrent.atomic.AtomicInteger

interface JDBCMetrics {
	fun counter(name: String): Counter

	fun gauge(name: String): Gauge

	fun gaugeCounter(name: String): AtomicInteger
}
