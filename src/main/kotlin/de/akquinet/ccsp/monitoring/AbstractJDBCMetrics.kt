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

	@Suppress("MemberVisibilityCanBePrivate")
	var enabled: Boolean = true

	@kotlin.jvm.Throws(SQLException::class)
	fun handleConnection(connection: Connection): Connection {
		onOpenConnection(connection)

		if (enabled) {
			counter(JDBC_CONNECTIONS_OPENED).increment()
			gaugeCounter(JDBC_CONNECTIONS_ACTIVE).increment()
		}

		return JDBCConnection(this, connection)
	}

	override fun bindTo(registry: MeterRegistry) {
		meterRegistry = registry

		Counter.builder(JDBC_CONNECTIONS_OPENED).baseUnit(CONNECTIONS).description("Number of opened connections")
			.register(registry)
		Counter.builder(JDBC_CONNECTIONS_CLOSED).baseUnit(CONNECTIONS).description("Number of closed connections")
			.register(registry)
		registerGauge(JDBC_CONNECTIONS_ACTIVE, CONNECTIONS, "Number of active connections")
	}

	@Suppress("MoveLambdaOutsideParentheses", "SameParameterValue")
	private fun registerGauge(name: String, unit: String, desc: String) {
		gaugeCounters[name] = AtomicInteger(0)

		Gauge.builder(name, { gaugeCounter(name) }).baseUnit(unit).description(desc).register(meterRegistry)
	}

	override fun registry() = meterRegistry

	override fun counter(name: String, tags: Tags): Counter = meterRegistry.get(name).tags(tags).counter()

	override fun gauge(name: String, tags: Tags): Gauge = meterRegistry.get(name).tags(tags).gauge()

	override fun gaugeCounter(name: String): AtomicInteger = gaugeCounters[name]!!

	override fun registerTimer(name: String, tags: Tags): Timer =
		Timer.builder(name).tags(tags).description(name).register(meterRegistry)

	override fun incrementCallCounter(name: String, tags: Tags): Counter =
		Counter.builder(name).baseUnit("calls").description(name).register(meterRegistry)

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
