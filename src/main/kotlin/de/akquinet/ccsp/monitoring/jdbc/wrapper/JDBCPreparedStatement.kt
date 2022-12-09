package de.akquinet.ccsp.monitoring.jdbc.wrapper

import de.akquinet.ccsp.monitoring.*
import io.micrometer.core.instrument.Tags
import java.sql.PreparedStatement
import java.util.function.BooleanSupplier
import java.util.function.IntSupplier

class JDBCPreparedStatement(
	private val jdbcMetrics: JDBCMetrics,
	sql: String,
	private val preparedStatement: PreparedStatement
) : PreparedStatement by preparedStatement {
	private val executionTags = Tags.of(TAG_PREPARED_STATEMENT_EXECUTION, sql)
	private val creationTags = Tags.of(TAG_PREPARED_STATEMENT_CREATION, sql)

	init {
		jdbcMetrics.registerTimer(JDBC_PREPARED_STATEMENT_TIMER, executionTags)
		jdbcMetrics.registerCallCounter(JDBC_PREPARED_STATEMENT_CALLS, creationTags).increment()
	}

	override fun execute() = jdbcMetrics.timer(JDBC_PREPARED_STATEMENT_TIMER, executionTags)
		.record(BooleanSupplier { preparedStatement.execute() })

	override fun executeUpdate() = jdbcMetrics.timer(JDBC_PREPARED_STATEMENT_TIMER, executionTags)
		.record(IntSupplier { preparedStatement.executeUpdate() })
}
