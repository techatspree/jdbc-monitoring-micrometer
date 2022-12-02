package de.akquinet.ccsp.monitoring.jdbc

import de.akquinet.ccsp.monitoring.AbstractJDBCMetrics
import java.sql.SQLException
import java.util.*

/**
 * Use custom connection factory to obtain connections
 */
@Suppress("unused")
open class ConnectionFactoryMetrics(private val connectionFactory: ConnectionFactory) : AbstractJDBCMetrics(),
    ConnectionFactory by connectionFactory {
    @Throws(SQLException::class)
    final override fun openConnection() = handleConnection(connectionFactory.openConnection())
}