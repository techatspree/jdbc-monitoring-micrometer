package de.akquinet.ccsp.monitoring.jdbc

import java.sql.Connection

/**
 * Custom connection factory
 */
interface ConnectionFactory {
	fun openConnection(): Connection
}