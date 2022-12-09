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
	private val executeTags = Tags.of(TAG_PREPARED_STATEMENT_EXECUTION, sql)

	init {
		jdbcMetrics.registerTimer(JDBC_PREPARED_STATEMENT_TIMER, executeTags)
		jdbcMetrics.registerCallCounter(JDBC_PREPARED_STATEMENT_CALLS, Tags.of(TAG_PREPARED_STATEMENT_CREATION, sql))
			.increment()
	}

	override fun execute() = jdbcMetrics.timer(JDBC_PREPARED_STATEMENT_TIMER, executeTags)
		.record(BooleanSupplier { preparedStatement.execute() })

	override fun executeUpdate() = jdbcMetrics.timer(JDBC_PREPARED_STATEMENT_TIMER, executeTags)
		.record(IntSupplier { preparedStatement.executeUpdate() })
}
