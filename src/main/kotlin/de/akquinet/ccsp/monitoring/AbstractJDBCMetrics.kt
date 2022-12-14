package de.akquinet.ccsp.monitoring

import de.akquinet.ccsp.monitoring.jdbc.wrapper.JDBCConnection
import io.micrometer.core.instrument.*
import io.micrometer.core.instrument.binder.BaseUnits.CONNECTIONS
import io.micrometer.core.instrument.binder.MeterBinder
import java.sql.Connection
import java.sql.SQLException
import java.util.concurrent.atomic.AtomicInteger

abstract class AbstractJDBCMetrics : MeterBinder, JDBCMetrics {
	private val gaugeCounters = HashMap<String, AtomicInteger>()
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

		registerCounter(JDBC_CONNECTIONS_OPENED, Tags.empty(), CONNECTIONS)
		registerCounter(JDBC_CONNECTIONS_CLOSED, Tags.empty(), CONNECTIONS)
		registerGauge(JDBC_CONNECTIONS_ACTIVE, CONNECTIONS)
	}

	@Suppress("MoveLambdaOutsideParentheses", "SameParameterValue")
	private fun registerGauge(name: String, unit: String) {
		gaugeCounters[name] = AtomicInteger(0)

		Gauge.builder(name, { gaugeCounter(name) }).baseUnit(unit).description(name).register(meterRegistry)
	}

	override fun registry() = meterRegistry

	override fun counter(name: String, tags: Tags): Counter = meterRegistry.get(name).tags(tags).counter()

	override fun gauge(name: String, tags: Tags): Gauge = meterRegistry.get(name).tags(tags).gauge()

	override fun timer(name: String, tags: Tags): Timer = meterRegistry.get(name).tags(tags).timer()

	override fun gaugeCounter(name: String): AtomicInteger = gaugeCounters[name]!!

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
