package de.akquinet.ccsp.monitoring.jdbc.wrapper

import de.akquinet.ccsp.monitoring.*
import io.micrometer.core.instrument.Tags
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.function.BooleanSupplier
import java.util.function.IntSupplier
import java.util.function.LongSupplier
import java.util.function.Supplier

class JDBCPreparedStatement(
	private val jdbcMetrics: JDBCMetrics,
	sql: String,
	private val preparedStatement: PreparedStatement
) : PreparedStatement by preparedStatement {
	private val executionTags = Tags.of(TAG_PREPARED_STATEMENT_EXECUTION, sql)
	private val creationTags = Tags.of(TAG_PREPARED_STATEMENT_CREATION, sql)

	init {
		jdbcMetrics.registerTimer(JDBC_PREPARED_STATEMENT_TIMER, executionTags)
		jdbcMetrics.registerCallCounter(JDBC_PREPARED_STATEMENT_CALLS, creationTags).increment()
	}

	override fun execute() = timer().record(BooleanSupplier { preparedStatement.execute() })

	override fun execute(sql: String) = timer().record(BooleanSupplier { preparedStatement.execute(sql) })

	override fun execute(sql: String, columnIndexes: IntArray) =
		timer().record(BooleanSupplier { preparedStatement.execute(sql, columnIndexes) })

	override fun execute(sql: String, columnNames: Array<String>) =
		timer().record(BooleanSupplier { preparedStatement.execute(sql, columnNames) })

	override fun execute(sql: String, autoGeneratedKeys: Int) = timer()
		.record(BooleanSupplier { preparedStatement.execute(sql, autoGeneratedKeys) })

	override fun executeUpdate() = timer().record(IntSupplier { preparedStatement.executeUpdate() })

	override fun executeUpdate(sql: String) = timer().record(IntSupplier { preparedStatement.executeUpdate(sql) })

	override fun executeUpdate(sql: String, autoGeneratedKeys: Int) =
		timer().record(IntSupplier { preparedStatement.executeUpdate(sql, autoGeneratedKeys) })

	override fun executeUpdate(sql: String, columnIndexes: IntArray) =
		timer().record(IntSupplier { preparedStatement.executeUpdate(sql, columnIndexes) })

	override fun executeUpdate(sql: String, columnNames: Array<out String>) =
		timer().record(IntSupplier { preparedStatement.executeUpdate(sql, columnNames) })

	override fun executeQuery(): ResultSet = timer().record(Supplier { preparedStatement.executeQuery() })!!

	override fun executeQuery(sql: String): ResultSet = timer().record(Supplier { preparedStatement.executeQuery(sql) })!!

	override fun executeBatch(): IntArray = timer().record(Supplier { preparedStatement.executeBatch() })!!

	override fun executeLargeBatch() = timer().record(Supplier { preparedStatement.executeLargeBatch() })!!

	override fun executeLargeUpdate() = timer().record(LongSupplier { preparedStatement.executeLargeUpdate() })!!

	override fun executeLargeUpdate(sql: String) =
		timer().record(LongSupplier { preparedStatement.executeLargeUpdate(sql) })

	override fun executeLargeUpdate(sql: String, autoGeneratedKeys: Int) =
		timer().record(LongSupplier { preparedStatement.executeLargeUpdate(sql, autoGeneratedKeys) })

	override fun executeLargeUpdate(sql: String, columnIndexes: IntArray) =
		timer().record(LongSupplier { preparedStatement.executeLargeUpdate(sql, columnIndexes) })

	override fun executeLargeUpdate(sql: String, columnNames: Array<out String>) =
		timer().record(LongSupplier { preparedStatement.executeLargeUpdate(sql, columnNames) })

	private fun timer() = jdbcMetrics.timer(JDBC_PREPARED_STATEMENT_TIMER, executionTags)
}
