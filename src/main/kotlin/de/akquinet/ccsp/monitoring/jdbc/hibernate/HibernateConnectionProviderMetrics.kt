package de.akquinet.ccsp.monitoring.jdbc.hibernate

import de.akquinet.ccsp.monitoring.AbstractJDBCMetrics
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
import java.sql.SQLException

/**
 * Use {@link org.hibernate.engine.jdbc.connections.spi.ConnectionProvider} to obtain connections
 */
abstract class HibernateConnectionProviderMetrics(private val connectionProvider: ConnectionProvider) :
	AbstractJDBCMetrics(),
	ConnectionProvider by connectionProvider {

	@kotlin.jvm.Throws(SQLException::class)
	@Suppress("UsePropertyAccessSyntax")
	final override fun getConnection() = handleConnection(connectionProvider.getConnection())
}