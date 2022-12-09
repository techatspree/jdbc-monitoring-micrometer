@file:Suppress("SqlResolve")

package de.akquinet.ccsp.monitoring.test

import de.akquinet.ccsp.monitoring.JDBC_PREPARED_STATEMENT_CALLS
import de.akquinet.ccsp.monitoring.jdbc.hibernate.HibernateDataSourceMetrics
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.cfg.Environment
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl
import org.junit.jupiter.api.Test

class PreparedStatementCounterTest : AbstractJDBCTest() {
	private val dataSourceMetrics = HibernateDataSourceMetrics().apply {
		val provider = provider<DatasourceConnectionProviderImpl>()
		provider.dataSource = dataSource
		provider.configure(mapOf(Environment.DATASOURCE to dataSource))
		bindTo(registry)
	}

	@Test
	fun `Count statements and executions`() {
		dataSourceMetrics.connection.use {
			it.prepareStatement("CREATE TABLE COMPANY(ID INT PRIMARY KEY, NAME VARCHAR(100) )").execute()
			it.prepareStatement("INSERT INTO COMPANY(ID, NAME) VALUES (1, 'akquinet')").executeUpdate()
		}

		val counters = dataSourceMetrics.registry().get(JDBC_PREPARED_STATEMENT_CALLS).counters()
		assertThat(counters.size).isEqualTo(2)
	}
}