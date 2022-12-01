package de.akquinet.ccsp.monitoring.jdbc

import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl


/**
 * Convenience class when obtaining the connection via JDBC driver manager
 */
open class HibernateConnectionBinder : HibernateJDBCMetrics(DriverManagerConnectionProviderImpl())