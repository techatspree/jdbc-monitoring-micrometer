@file:JvmName("Metrics")

package de.akquinet.ccsp.monitoring

import io.micrometer.core.instrument.Meter
import java.util.concurrent.atomic.AtomicInteger

const val JDBC_CONNECTIONS_OPENED = "Number of opened connections"
const val JDBC_CONNECTIONS_CLOSED = "Number of closed connections"
const val JDBC_CONNECTIONS_ACTIVE = "Number of active connections"

const val JDBC_PREPARED_STATEMENTS = "Prepared statements"
const val JDBC_PREPARED_STATEMENT_TIMER = "Prepared statement execution time"
const val TAG_PREPARED_STATEMENT_CREATION = "preparedstatement.sql.calls"
const val TAG_PREPARED_STATEMENT_EXECUTION = "preparedstatement.executions"
const val TAG_PREPARED_STATEMENT_BATCHED_EXECUTION = "preparedstatement.batched_executions"

const val JDBC_STATEMENTS = "Statements"
const val JDBC_STATEMENT_TIMER = "Statement execution time"
const val JDBC_STATEMENTS_EXECUTE = "Number of execution calls"
const val TAG_STATEMENT_EXECUTION = "statement.executions"
const val TAG_STATEMENT_CREATION = "statement.sql.calls"

const val UNIT_INSTANCES = "instances"

fun AtomicInteger.increment() {
	incrementAndGet()
}

fun AtomicInteger.decrement() {
	decrementAndGet()
}

fun Meter.getUniqueName(): String {
	val tags = id.tags.joinToString(separator = ":") { it.key + "." + it.value }

	return if (tags.isNotEmpty()) id.name + "/" + tags else id.name
}
