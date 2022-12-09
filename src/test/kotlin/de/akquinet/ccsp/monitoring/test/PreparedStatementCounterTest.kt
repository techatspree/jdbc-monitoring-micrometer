@file:Suppress("SqlResolve")

package de.akquinet.ccsp.monitoring.test

import de.akquinet.ccsp.monitoring.JDBC_PREPARED_STATEMENT_CALLS
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
	fun `Count statements and executions`() {
		val sqlCreate = "CREATE TABLE COMPANY(ID INT PRIMARY KEY, NAME VARCHAR(100))"
		val sqlInsert = "INSERT INTO COMPANY(ID, NAME) VALUES (?, ?)"

		dataSourceMetrics.connection.use {
			it.prepareStatement(sqlCreate).execute()
			it.prepareStatement(sqlInsert).apply { setInt(1, 1); setString(2, "akquinet") }.executeUpdate()
			it.prepareStatement(sqlInsert).apply { setInt(1, 2); setString(2, "IBM") }.executeUpdate()
		}

		assertThat(searchCalls().counters().size).isEqualTo(2)
		assertThat(searchCalls().tags(callTags(sqlCreate)).counter().count())
			.`as`("Prepared statements for '$sqlCreate'").isEqualTo(1.0)
		assertThat(searchCalls().tags(callTags(sqlInsert)).counter().count())
			.`as`("Prepared statements for '$sqlInsert'").isEqualTo(2.0)

		val timer = searchExecutions().tags(executionTags(sqlInsert)).timer()
		assertThat(timer.count()).`as`("Number of records for '$sqlCreate'").isEqualTo(2)
		assertThat(timer.totalTime(TimeUnit.MICROSECONDS)).`as`("Total time spent for '$sqlCreate'").isGreaterThan(0.0)
	}

	private fun callTags(sql: String) = Tags.of(TAG_PREPARED_STATEMENT_CREATION, sql)

	@Suppress("SameParameterValue")
	private fun executionTags(sql: String) = Tags.of(TAG_PREPARED_STATEMENT_EXECUTION, sql)
	private fun searchCalls() = dataSourceMetrics.registry().get(JDBC_PREPARED_STATEMENT_CALLS)
	private fun searchExecutions() = dataSourceMetrics.registry().get(JDBC_PREPARED_STATEMENT_TIMER)
}