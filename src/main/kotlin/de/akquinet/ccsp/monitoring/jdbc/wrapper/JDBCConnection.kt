package de.akquinet.ccsp.monitoring.jdbc.wrapper

import de.akquinet.ccsp.monitoring.decrement
import io.micrometer.core.instrument.Counter
import java.sql.Connection
import java.sql.SQLException
import java.util.concurrent.atomic.AtomicInteger

class JDBCConnection(
	private val connection: Connection,
	private val closeCounter: Counter,
	private val activeConnections: AtomicInteger
) :
	Connection by connection {
	@Throws(SQLException::class)
	override fun close() {
		closeCounter.increment()
		activeConnections.decrement()
		connection.close()
	}
}