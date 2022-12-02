package de.akquinet.ccsp.monitoring.jdbc

import de.akquinet.ccsp.monitoring.AbstractJDBCMetrics
import java.sql.Driver
import java.sql.SQLException
import java.util.*

/**
 * Use {@link java.sql.Driver} to obtain connections
 */
@Suppress("unused")
open class JDBCDriverMetrics(private val driver: Driver) : AbstractJDBCMetrics(), Driver by driver {
	@Throws(SQLException::class)
	@Suppress("UsePropertyAccessSyntax")
	final override fun connect(url: String, info: Properties) = handleConnection(driver.connect(url, info))
}