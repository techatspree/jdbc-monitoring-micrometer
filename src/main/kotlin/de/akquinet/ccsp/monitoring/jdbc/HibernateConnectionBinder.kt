package de.akquinet.ccsp.monitoring.jdbc

import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl
import java.sql.Connection


/**
 * Convenience class when obtaining the connection via JDBC driver manager
 */
open class HibernateConnectionBinder : DriverManagerConnectionProviderImpl(), ConnectionListener {
    override fun getConnection(): Connection {
        val connection = super.getConnection()

        onOpenConnection(connection)

        return connection
    }
}