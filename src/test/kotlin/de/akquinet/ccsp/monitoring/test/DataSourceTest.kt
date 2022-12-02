@file:Suppress("SqlResolve")

package de.akquinet.ccsp.monitoring.test

import de.akquinet.ccsp.monitoring.JDBC_CONNECTIONS_ACTIVE
import de.akquinet.ccsp.monitoring.JDBC_CONNECTIONS_CLOSED
import de.akquinet.ccsp.monitoring.JDBC_CONNECTIONS_OPENED
import de.akquinet.ccsp.monitoring.JDBC_PREPARED_STATEMENT_CALLS
import de.akquinet.ccsp.monitoring.jdbc.JDBCDataSourceMetrics
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.assertj.core.api.Assertions.assertThat
import org.hsqldb.jdbc.JDBCDataSource
import org.junit.jupiter.api.Test
import javax.sql.DataSource

class DataSourceTest {
	private val dataSource: DataSource = JDBCDataSource().apply { setURL("jdbc:hsqldb:mem:test") }
	private val registry = SimpleMeterRegistry()
	private val dataSourceMetrics = JDBCDataSourceMetrics(dataSource).apply { bindTo(registry) }

	@Test
	fun `Count connections`() {
		checkMeters(0.0,0.0, 0.0)

		dataSourceMetrics.connection.use {
			it.createStatement().execute("CREATE TABLE COMPANY(ID INT PRIMARY KEY, NAME VARCHAR(100) )")
			checkMeters(1.0,0.0, 1.0)
		}

		dataSourceMetrics.connection.use {
			it.prepareStatement("INSERT INTO COMPANY(ID, NAME) VALUES (1, 'akquinet')")
			checkMeters(2.0,1.0, 1.0)
		}

		val counters = dataSourceMetrics.registry().get(JDBC_PREPARED_STATEMENT_CALLS).counters()
		assertThat(counters.size).isEqualTo(1)

		checkMeters(2.0,2.0, 0.0)
	}

	private fun checkMeters(opened: Double, closed: Double, active: Double) {
		assertThat(dataSourceMetrics.counter(JDBC_CONNECTIONS_OPENED).count()).`as`("opened").isEqualTo(opened)
		assertThat(dataSourceMetrics.counter(JDBC_CONNECTIONS_CLOSED).count()).`as`("closed").isEqualTo(closed)
		assertThat(dataSourceMetrics.gauge(JDBC_CONNECTIONS_ACTIVE).value()).`as`("active").isEqualTo(active)
	}
}