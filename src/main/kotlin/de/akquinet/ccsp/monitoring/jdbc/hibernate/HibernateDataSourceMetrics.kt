package de.akquinet.ccsp.monitoring.jdbc.hibernate

import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl

/**
 * Convenience class when obtaining the connection via a data source
 */
@Suppress("unused")
open class HibernateDataSourceMetrics : HibernateConnectionProviderMetrics(DatasourceConnectionProviderImpl())

