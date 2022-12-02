package de.akquinet.ccsp.monitoring

import de.akquinet.ccsp.monitoring.jdbc.wrapper.JDBCConnection
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.BaseUnits.CONNECTIONS
import io.micrometer.core.instrument.binder.MeterBinder
import java.sql.Connection
import java.sql.SQLException
import java.util.concurrent.atomic.AtomicInteger

abstract class AbstractJDBCMetrics : MeterBinder {
	var enabled: Boolean = true

	private val meters = HashMap<String, Meter>()
	private var activeConnections = AtomicInteger(0)

	@kotlin.jvm.Throws(SQLException::class)
	fun handleConnection(connection: Connection): Connection {
		onOpenConnection(connection)

		if (enabled) {
			counter(JDBC_CONNECTIONS_OPENED).increment()
			activeConnections.increment()
		}

		return JDBCConnection(connection, counter(JDBC_CONNECTIONS_CLOSED), activeConnections)
	}

	@Suppress("MoveLambdaOutsideParentheses")
	override fun bindTo(registry: MeterRegistry) {
		meters[JDBC_CONNECTIONS_OPENED] = Counter.builder(JDBC_CONNECTIONS_OPENED).baseUnit(CONNECTIONS).register(registry)
		meters[JDBC_CONNECTIONS_CLOSED] = Counter.builder(JDBC_CONNECTIONS_CLOSED).baseUnit(CONNECTIONS).register(registry)
		meters[JDBC_CONNECTIONS_ACTIVE] =
			Gauge.builder(JDBC_CONNECTIONS_ACTIVE, { activeConnections }).baseUnit(CONNECTIONS).register(registry)
	}

	fun counter(name: String) = meters[name] as Counter

	fun gauge(name: String) = meters[name] as Gauge

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
