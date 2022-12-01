package de.akquinet.ccsp.monitoring.jdbc

import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl

/**
 * Convenience class when obtaining the connection via a data source
 */
open class HibernateDataSourceBinder : HibernateJDBCMetrics(DatasourceConnectionProviderImpl())

