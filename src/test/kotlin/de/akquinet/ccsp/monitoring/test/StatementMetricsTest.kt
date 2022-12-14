@file:Suppress("SqlResolve")

package de.akquinet.ccsp.monitoring.test

import de.akquinet.ccsp.monitoring.*
import de.akquinet.ccsp.monitoring.jdbc.hibernate.HibernateDriverMetrics
import io.micrometer.core.instrument.Tags
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.cfg.AvailableSettings.DRIVER
import org.hibernate.cfg.AvailableSettings.URL
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl
import org.hsqldb.jdbc.JDBCDriver
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class StatementMetricsTest : AbstractJDBCTest() {
	private val driverMetrics = HibernateDriverMetrics().apply {
		val provider = provider<DriverManagerConnectionProviderImpl>()
		provider.configure(mapOf(DRIVER to JDBCDriver::class.java.name, URL to JDBC_URL))
		bindTo(registry)
	}

	@Test
	fun `Count statements and executions`() {
		val sqlInsert1 = SQL_INSERT.replaceFirst("?", "1").replaceFirst("?", "'akquinet'")
		val sqlInsert2 = SQL_INSERT.replaceFirst("?", "2").replaceFirst("?", "'IBM'")

		driverMetrics.connection.use {
			it.createStatement().executeUpdate(SQL_CREATE)
			it.createStatement().executeUpdate(sqlInsert1)
			it.createStatement().executeUpdate(sqlInsert2)
			it.createStatement().executeUpdate(SQL_DELETE)
			it.createStatement().executeUpdate(sqlInsert1)
		}

		assertThat(driverMetrics.registry().get(JDBC_STATEMENTS).counter().count())
			.`as`("Numer of statement instances").isEqualTo(5.0)

		assertThat(statementCreations().tags(executionTags(sqlInsert1)).counter().count())
			.`as`("Executions of '$sqlInsert1'").isEqualTo(2.0)
		assertThat(statementCreations().tags(executionTags(sqlInsert2)).counter().count())
			.`as`("Executions of '$sqlInsert2'").isEqualTo(1.0)
		assertThat(statementCreations().tags(executionTags(SQL_DELETE)).counter().count())
			.`as`("Executions of '$SQL_DELETE'").isEqualTo(1.0)

		val timer = statementExecutions().tags(timerTags(sqlInsert1)).timer()

		assertThat(timer.count()).`as`("Number of records for '$sqlInsert1'").isEqualTo(2)
		assertThat(timer.totalTime(TimeUnit.MICROSECONDS)).`as`("Total time spent for '$sqlInsert1'").isGreaterThan(0.0)
	}

	private fun executionTags(sql: String) = Tags.of(TAG_STATEMENT_CREATION, sql)
	private fun timerTags(sql: String) = Tags.of(TAG_STATEMENT_EXECUTION, sql)

	@Suppress("SameParameterValue")
	private fun statementCreations() = driverMetrics.registry().get(JDBC_STATEMENTS_EXECUTE)
	private fun statementExecutions() = driverMetrics.registry().get(JDBC_STATEMENT_TIMER)
}