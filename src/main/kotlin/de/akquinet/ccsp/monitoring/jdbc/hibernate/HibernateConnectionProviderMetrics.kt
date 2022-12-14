package de.akquinet.ccsp.monitoring.jdbc.hibernate

import de.akquinet.ccsp.monitoring.AbstractJDBCMetrics
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
import org.hibernate.service.spi.Configurable
import org.hibernate.service.spi.ServiceRegistryAwareService
import org.hibernate.service.spi.ServiceRegistryImplementor
import org.hibernate.service.spi.Stoppable
import java.sql.SQLException

/**
 * Use {@link org.hibernate.engine.jdbc.connections.spi.ConnectionProvider} to obtain connections
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class HibernateConnectionProviderMetrics(private val connectionProvider: ConnectionProvider) :
	AbstractJDBCMetrics(), Configurable, Stoppable, ServiceRegistryAwareService,
	ConnectionProvider by connectionProvider {
	protected val configurationValues: MutableMap<String, Any> = HashMap()

	@kotlin.jvm.Throws(SQLException::class)
	@Suppress("UsePropertyAccessSyntax")
	final override fun getConnection() = handleConnection(connectionProvider.getConnection())

	@Suppress("UNCHECKED_CAST")
	fun <P : ConnectionProvider> provider(): P = connectionProvider as P

	final override fun configure(configurationValues: Map<Any?, Any?>) {
		if (connectionProvider is Configurable) {
			connectionProvider.configure(configurationValues)
		}

		for (entry in configurationValues) {
			this.configurationValues[entry.key as String] = entry.value!!
		}
	}

	final override fun stop() {
		if (connectionProvider is Stoppable) {
			connectionProvider.stop()
		}
	}

	override fun injectServices(serviceRegistry: ServiceRegistryImplementor) {
		if (connectionProvider is ServiceRegistryAwareService) {
			connectionProvider.injectServices(serviceRegistry)
		}
	}
}