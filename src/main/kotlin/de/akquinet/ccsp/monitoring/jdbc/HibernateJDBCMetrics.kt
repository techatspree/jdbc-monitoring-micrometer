package de.akquinet.ccsp.monitoring.jdbc

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.MeterBinder
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
import java.sql.Connection
import java.sql.SQLException

open class HibernateJDBCMetrics(private val connectionProvider: ConnectionProvider) : MeterBinder, ConnectionListener,
    ConnectionProvider by connectionProvider {
    var enabled: Boolean = true

    override fun bindTo(registry: MeterRegistry) {
        // TODO
    }

    @kotlin.jvm.Throws(SQLException::class)
    final override fun getConnection(): Connection {
        val connection = connectionProvider.connection

        onOpenConnection(connection)

        return if (enabled)
            connection
        else
            connection
    }
}