@file:Suppress("SqlResolve")

package de.akquinet.ccsp.monitoring.test

import de.akquinet.ccsp.monitoring.JDBC_PREPARED_STATEMENTS
import de.akquinet.ccsp.monitoring.JDBC_PREPARED_STATEMENT_TIMER
import de.akquinet.ccsp.monitoring.TAG_PREPARED_STATEMENT_CREATION
import de.akquinet.ccsp.monitoring.TAG_PREPARED_STATEMENT_EXECUTION
import de.akquinet.ccsp.monitoring.jdbc.hibernate.HibernateDataSourceMetrics
import io.micrometer.core.instrument.Tags
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.cfg.Environment
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class PreparedStatementCounterTest : AbstractJDBCTest() {
	private val dataSourceMetrics = HibernateDataSourceMetrics().apply {
		val provider = provider<DatasourceConnectionProviderImpl>()
		provider.dataSource = dataSource
		provider.configure(mapOf(Environment.DATASOURCE to dataSource))
		bindTo(registry)
	}

	@Test
	fun `Count prepared statements and executions`() {
		dataSourceMetrics.connection.use {
			it.prepareStatement(SQL_CREATE).execute()
			it.prepareStatement(SQL_INSERT).apply { setInt(1, 1); setString(2, "akquinet") }.executeUpdate()
			it.prepareStatement(SQL_INSERT).apply { setInt(1, 2); setString(2, "IBM") }.executeUpdate()
		}

		assertThat(searchStatementCreations().counters().size).isEqualTo(2)
		assertThat(searchStatementCreations().tags(callTags(SQL_CREATE)).counter().count())
			.`as`("Prepared statements for '$SQL_CREATE'").isEqualTo(1.0)
		assertThat(searchStatementCreations().tags(callTags(SQL_INSERT)).counter().count())
			.`as`("Prepared statements for '$SQL_INSERT'").isEqualTo(2.0)

		val timer = searchStatementExecutions().tags(executionTags(SQL_INSERT)).timer()

		assertThat(timer.count()).`as`("Number of records for '$SQL_CREATE'").isEqualTo(2)
		assertThat(timer.totalTime(TimeUnit.MICROSECONDS)).`as`("Total time spent for '$SQL_CREATE'").isGreaterThan(0.0)
	}

	private fun callTags(sql: String) = Tags.of(TAG_PREPARED_STATEMENT_CREATION, sql)

	@Suppress("SameParameterValue")
	private fun executionTags(sql: String) = Tags.of(TAG_PREPARED_STATEMENT_EXECUTION, sql)
	private fun searchStatementCreations() = dataSourceMetrics.registry().get(JDBC_PREPARED_STATEMENTS)
	private fun searchStatementExecutions() = dataSourceMetrics.registry().get(JDBC_PREPARED_STATEMENT_TIMER)
}