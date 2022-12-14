package de.akquinet.ccsp.monitoring.jdbc.wrapper

import de.akquinet.ccsp.monitoring.*
import io.micrometer.core.instrument.Tags
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.function.BooleanSupplier
import java.util.function.IntSupplier
import java.util.function.LongSupplier
import java.util.function.Supplier

class JDBCPreparedStatement(
	jdbcMetrics: JDBCMetrics,
	sql: String,
	private val preparedStatement: PreparedStatement
) : PreparedStatement by preparedStatement {
	private val executionTags = Tags.of(TAG_PREPARED_STATEMENT_EXECUTION, sql)
	private val batchedTags = Tags.of(TAG_PREPARED_STATEMENT_BATCHED_EXECUTION, sql)
	private val creationTags = Tags.of(TAG_PREPARED_STATEMENT_CREATION, sql)
	private val timer = jdbcMetrics.registerTimer(JDBC_PREPARED_STATEMENT_TIMER, executionTags)
	private val batchTimer = jdbcMetrics.registerTimer(JDBC_PREPARED_STATEMENT_TIMER, batchedTags)
	private val instanceCounter = jdbcMetrics.registerCounter(JDBC_PREPARED_STATEMENTS, creationTags, UNIT_INSTANCES)

	init {
		instanceCounter.increment()
	}

	override fun execute() = timer.record(BooleanSupplier { preparedStatement.execute() })

	override fun executeUpdate() = timer.record(IntSupplier { preparedStatement.executeUpdate() })

	override fun executeQuery(): ResultSet = timer.record(Supplier { preparedStatement.executeQuery() })!!

	override fun executeLargeUpdate() = timer.record(LongSupplier { preparedStatement.executeLargeUpdate() })

	override fun executeBatch(): IntArray = batchTimer.record(Supplier { preparedStatement.executeBatch() })!!

	override fun executeLargeBatch() = batchTimer.record(Supplier { preparedStatement.executeLargeBatch() })!!
}
