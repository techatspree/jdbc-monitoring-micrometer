package de.akquinet.ccsp.monitoring.jdbc.hibernate

import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl

/**
 * Convenience class when obtaining the connection via JDBC driver manager
 */
@Suppress("unused")
open class HibernateDriverMetrics : HibernateConnectionProviderMetrics(DriverManagerConnectionProviderImpl())