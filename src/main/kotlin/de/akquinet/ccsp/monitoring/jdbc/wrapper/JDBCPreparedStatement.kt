package de.akquinet.ccsp.monitoring.jdbc.wrapper

import de.akquinet.ccsp.monitoring.*
import io.micrometer.core.instrument.Tags
import java.sql.PreparedStatement

class JDBCPreparedStatement(
	private val jdbcMetrics: JDBCMetrics,
	sql: String,
	private val preparedStatement: PreparedStatement
) : PreparedStatement by preparedStatement {
	init {
		jdbcMetrics.registerTimer(
			JDBC_PREPARED_STATEMENT_TIMER, Tags.of(TAG_JDBC_PREPARED_STATEMENT, sql))
		jdbcMetrics.incrementCallCounter(JDBC_PREPARED_STATEMENT_CALLS, Tags.of(TAG_JDBC_PREPARED_STATEMENT, sql))
	}
}