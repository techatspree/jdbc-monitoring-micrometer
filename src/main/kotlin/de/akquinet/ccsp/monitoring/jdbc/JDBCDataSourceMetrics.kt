package de.akquinet.ccsp.monitoring.jdbc

import de.akquinet.ccsp.monitoring.AbstractJDBCMetrics
import java.sql.SQLException
import javax.sql.DataSource

/**
 * Use {@link java.sql.DataSource} to obtain connections
 */
@Suppress("unused")
open class JDBCDataSourceMetrics(private val dataSource: DataSource) : AbstractJDBCMetrics(), DataSource by dataSource {
	@Throws(SQLException::class)
	final override fun getConnection(username: String, password: String) =
		handleConnection(dataSource.getConnection(username, password))

	@Suppress("UsePropertyAccessSyntax")
	@Throws(SQLException::class)
	final override fun getConnection() = handleConnection(dataSource.getConnection())
}