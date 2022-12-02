package de.akquinet.ccsp.monitoring

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.BaseUnits.CONNECTIONS
import io.micrometer.core.instrument.binder.MeterBinder
import java.sql.Connection
import java.sql.SQLException

abstract class AbstractJDBCMetrics : MeterBinder {
    var enabled: Boolean = true

    private val meters = HashMap<String, Meter>()

    override fun bindTo(registry: MeterRegistry) {
        meters[JDBC_CONNECTIONS_OPENED] = Counter.builder(JDBC_CONNECTIONS_OPENED).baseUnit(CONNECTIONS).register(registry)
    }

    fun counter(name: String) = meters[name] as Counter

    @kotlin.jvm.Throws(SQLException::class)
    fun handleConnection(connection: Connection): Connection {
        onOpenConnection(connection)

        if (enabled) {
            counter(JDBC_CONNECTIONS_OPENED).increment()
        }

        return connection
    }

    /**
     *  Override this method if you want to be informed whenever a connection is created.
     */
    @kotlin.jvm.Throws(SQLException::class)
    protected open fun onOpenConnection(realConnection: Connection) {
    }
}