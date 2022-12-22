package de.akquinet.ccsp.monitoring

import io.micrometer.core.instrument.*
import java.util.concurrent.atomic.AtomicInteger

interface JDBCMetrics {
	fun registry(): MeterRegistry

	fun registerTimer(name: String, tags: Tags): Timer

	fun registerCounter(name: String, tags: Tags, baseUnit: String): Counter

	fun registerFunctionCounter(name: String, tags: Tags, baseUnit: String): FunctionCounter

	fun registerGauge(name: String, tags: Tags, unit: String): Gauge

	fun counter(name: String, tags: Tags = Tags.empty()): Counter

	fun gauge(name: String, tags: Tags = Tags.empty()): Gauge

	fun timer(name: String, tags: Tags = Tags.empty()): Timer

	fun gaugeCounterValue(name: String, tags: Tags = Tags.empty()): AtomicInteger

	fun functionCounterValue(name: String, tags: Tags = Tags.empty()): AtomicInteger
}
