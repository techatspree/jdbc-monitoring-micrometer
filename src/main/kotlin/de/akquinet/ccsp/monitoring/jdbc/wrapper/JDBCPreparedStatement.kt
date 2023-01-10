package de.akquinet.ccsp.monitoring.jdbc.wrapper

import de.akquinet.ccsp.monitoring.*
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.Timer
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.function.BooleanSupplier
import java.util.function.IntSupplier
import java.util.function.LongSupplier
import java.util.function.Supplier

class JDBCPreparedStatement(
	private val jdbcMetrics: JDBCMetrics,
	override var name: String,
	private val preparedStatement: PreparedStatement
) : PreparedStatement by preparedStatement, JDBCWrapper {
	private var counterRegistered: Boolean = false

	override fun execute() = timer().record(BooleanSupplier { preparedStatement.execute() })

	override fun executeUpdate() = timer().record(IntSupplier { preparedStatement.executeUpdate() })

	override fun executeQuery(): ResultSet = timer().record(Supplier { preparedStatement.executeQuery() })!!

	override fun executeLargeUpdate() = timer().record(LongSupplier { preparedStatement.executeLargeUpdate() })

	override fun executeBatch(): IntArray = batchTimer().record(Supplier { preparedStatement.executeBatch() })!!

	override fun executeLargeBatch() = batchTimer().record(Supplier { preparedStatement.executeLargeBatch() })!!

	override fun isWrapperFor(iface: Class<*>) = when {
		JDBCWrapper::class.java.isAssignableFrom(iface) -> true
		else -> preparedStatement.isWrapperFor(iface)
	}

	override fun <T> unwrap(iface: Class<T>): T = when {
		JDBCWrapper::class.java.isAssignableFrom(iface) -> iface.cast(this@JDBCPreparedStatement)
		else -> preparedStatement.unwrap(iface)
	}

	private fun timer(): Timer {
		countInstances()

		return jdbcMetrics.registerTimer(
			JDBC_PREPARED_STATEMENT_TIMER,
			Tags.of(TAG_PREPARED_STATEMENT_EXECUTION, name)
		)
	}

	private fun batchTimer(): Timer {
		countInstances()

		return jdbcMetrics.registerTimer(
			JDBC_PREPARED_STATEMENT_TIMER,
			Tags.of(TAG_PREPARED_STATEMENT_BATCHED_EXECUTION, name)
		)
	}

	private fun countInstances() {
		if (!counterRegistered) {
			val tags = Tags.of(TAG_PREPARED_STATEMENT_CREATION, name)
			jdbcMetrics.registerFunctionCounter(JDBC_PREPARED_STATEMENTS, tags, UNIT_INSTANCES)
			jdbcMetrics.functionCounterValue(JDBC_PREPARED_STATEMENTS, tags).increment()
			counterRegistered = true
		}
	}
}
