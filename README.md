# JDBC monitoring with MicroMeter

	https://www.micrometer.io

Extension for MicroMeter in order to measure JDBC-related meters. It provides adapters for several connection provider types, such as
[java.sql.DataSource](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/javax/sql/DataSource.html), 
[org.hibernate.engine.jdbc.connections.spi.ConnectionProvider](https://docs.jboss.org/hibernate/orm/5.6/javadocs/org/hibernate/engine/jdbc/connections/spi/ConnectionProvider.html), e.g.

If you just need an extension for [Hibernate Statistics](https://docs.jboss.org/hibernate/orm/5.4/javadocs/org/hibernate/stat/Statistics.html) module, 
there already exist an extension within the [Hibernate ORM](https://github.com/hibernate/hibernate-orm) project.

# Delegation vs Reflection

Instead of using reflection, the framework heavily utilizes the Kotlin [Delegation](https://kotlinlang.org/docs/delegation.html) feature.
It contains several wrapper classes, for example JDBCConnection for SQL-Connections, in order to implement counters and other meters. 

