package de.akquinet.ccsp.monitoring.jdbc

import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl
import java.sql.Connection

/**
 * Convenience class when obtaining the connection via a data source
 */
open class HibernateDataSourceBinder : DatasourceConnectionProviderImpl(), ConnectionListener {
    override fun getConnection(): Connection {
        val connection = super.getConnection()

        onOpenConnection(connection)

        return connection
    }
}

