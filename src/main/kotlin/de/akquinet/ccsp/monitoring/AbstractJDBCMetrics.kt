package de.akquinet.ccsp.monitoring

import de.akquinet.ccsp.monitoring.jdbc.wrapper.JDBCConnection
import io.micrometer.core.instrument.*
import io.micrometer.core.instrument.binder.BaseUnits.CONNECTIONS
import io.micrometer.core.instrument.binder.MeterBinder
import java.sql.Connection
import java.sql.SQLException
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors

abstract class AbstractJDBCMetrics : MeterBinder, JDBCMetrics {
	private val gaugeCounters = HashMap<String, AtomicInteger>()
	private val functionCounters = HashMap<String, AtomicInteger>()

	private lateinit var meterRegistry: MeterRegistry

	var enabled: Boolean = true

	@kotlin.jvm.Throws(SQLException::class)
	fun handleConnection(connection: Connection): Connection {
		onOpenConnection(connection)

		return if (enabled) {
			JDBCConnection(this, connection)
		} else {
			connection
		}
	}

	override fun bindTo(registry: MeterRegistry) {
		meterRegistry = registry

		registerFunctionCounter(JDBC_CONNECTIONS_OPENED, Tags.empty(), CONNECTIONS)
		registerFunctionCounter(JDBC_CONNECTIONS_CLOSED, Tags.empty(), CONNECTIONS)
		registerGauge(JDBC_CONNECTIONS_ACTIVE, Tags.empty(), CONNECTIONS)
	}

	@Suppress("MoveLambdaOutsideParentheses", "SameParameterValue")
	override fun registerGauge(name: String, tags: Tags, unit: String): Gauge {
		val value = AtomicInteger(0)
		val gauge = Gauge.builder(name, { value }).baseUnit(unit).tags(tags).description(name).register(meterRegistry)
		val key = gauge.getUniqueName()

		gaugeCounters.putIfAbsent(key, value)

		return gauge
	}

	@Suppress("MoveLambdaOutsideParentheses", "SameParameterValue")
	override fun registerFunctionCounter(name: String, tags: Tags, baseUnit: String): FunctionCounter {
		val value = AtomicInteger(0)
		val counter = FunctionCounter.builder(name, value, { value.toDouble() })
			.tags(tags).baseUnit(baseUnit).description(name).register(meterRegistry)
		val key = counter.getUniqueName()

		functionCounters.putIfAbsent(key, value)

		return counter
	}

	override fun registry() = meterRegistry

	override fun counter(name: String, tags: Tags): Counter = meterRegistry.get(name).tags(tags).counter()

	override fun gauge(name: String, tags: Tags): Gauge = meterRegistry.get(name).tags(tags).gauge()

	override fun timer(name: String, tags: Tags): Timer = meterRegistry.get(name).tags(tags).timer()

	override fun gaugeCounterValue(name: String, tags: Tags): AtomicInteger {
		val gauge = meterRegistry.get(name).tags(tags).gauge()
		val key = gauge.getUniqueName()

		return gaugeCounters[key]!!
	}

	override fun functionCounterValue(name: String, tags: Tags): AtomicInteger {
		val counter = meterRegistry.get(name).tags(tags).functionCounter()
		val key = counter.getUniqueName()

		return functionCounters[key]!!
	}

	override fun registerTimer(name: String, tags: Tags): Timer =
		Timer.builder(name).tags(tags).description(name).register(meterRegistry)

	override fun registerCounter(name: String, tags: Tags, baseUnit: String): Counter =
		Counter.builder(name).tags(tags).baseUnit(baseUnit).description(name).register(meterRegistry)

	/**
	 *  Override this method if you want to be informed whenever a connection is created.
	 */
	@kotlin.jvm.Throws(SQLException::class)
	protected open fun onOpenConnection(realConnection: Connection) {
	}
}

fun AtomicInteger.increment() {
	incrementAndGet()
}

fun AtomicInteger.decrement() {
	decrementAndGet()
}

fun Meter.getUniqueName(): String {
	val tags = id.tags.stream().map { tag: Tag -> tag.key + "." + tag.value }
		.collect(Collectors.joining(":"))

	return tags + "/" + id.name
}
