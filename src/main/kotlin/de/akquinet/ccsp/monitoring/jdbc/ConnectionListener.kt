package de.akquinet.ccsp.monitoring.jdbc

import java.sql.Connection
import java.sql.SQLException

interface ConnectionListener {
    /**
     * Notify that a java.sql.Connection has been created/opened
     */
    @kotlin.jvm.Throws(SQLException::class)
    fun onOpenConnection(connection: Connection) {
    }
}