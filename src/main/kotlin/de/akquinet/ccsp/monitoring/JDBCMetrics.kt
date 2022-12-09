package de.akquinet.ccsp.monitoring

import io.micrometer.core.instrument.*
import java.util.concurrent.atomic.AtomicInteger

interface JDBCMetrics {
	fun counter(name: String, tags: Tags = Tags.empty()): Counter

	fun gauge(name: String, tags: Tags = Tags.empty()): Gauge

	fun timer(name: String, tags: Tags): Timer

	fun gaugeCounter(name: String): AtomicInteger

	fun registerTimer(name: String, tags: Tags): Timer

	fun registerCounter(name: String, tags: Tags, baseUnit: String): Counter

	fun registry(): MeterRegistry
}
