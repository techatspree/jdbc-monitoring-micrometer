package de.akquinet.ccsp.monitoring.jdbc.wrapper

import de.akquinet.ccsp.monitoring.JDBCMetrics
import java.sql.PreparedStatement

class JDBCPreparedStatement(
	private val jdbcMetrics: JDBCMetrics,
	sql: String,
	private val preparedStatement: PreparedStatement
) : PreparedStatement by preparedStatement {
}