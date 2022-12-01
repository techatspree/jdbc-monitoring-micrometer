package de.akquinet.ccsp.monitoring.jdbc

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.MeterBinder
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
import java.sql.Connection

open class HibernateJDBCMetrics(private val connectionProvider: ConnectionProvider) : MeterBinder, ConnectionListener,
    ConnectionProvider by connectionProvider {

    override fun bindTo(registry: MeterRegistry) {
        // TODO
    }

    override fun getConnection(): Connection {
        val connection = connectionProvider.connection

        onOpenConnection(connection)

        return connection
    }
}