@file:Suppress("SqlResolve")

package de.akquinet.ccsp.monitoring.test

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.hsqldb.jdbc.JDBCDataSource
import org.junit.jupiter.api.AfterEach

abstract class AbstractJDBCTest {
	protected val dataSource = JDBCDataSource().apply { setURL("jdbc:hsqldb:mem:test") }
	protected val registry = SimpleMeterRegistry()

	@AfterEach
	fun shutdown() {
		dataSource.connection.use {
			it.createStatement().execute("SHUTDOWN")
		}
	}
}