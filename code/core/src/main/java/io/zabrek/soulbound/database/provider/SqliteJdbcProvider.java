package io.zabrek.soulbound.database.provider;

import io.zabrek.soulbound.api.logger.SoulBoundLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides a Sqlite JDBC connection.
 */
public class SqliteJdbcProvider implements ConnectionProvider {

    /**
     * The logger instances.
     */
    private final SoulBoundLogger log;

    /**
     * The location of the database file.
     */
    private final String dbLocation;

    /**
     * Creates a new SQLite JDBC provider.
     *
     * @param log        the logger instance
     * @param dbLocation the absolute path location of the database file
     */
    public SqliteJdbcProvider(final SoulBoundLogger log, final String dbLocation) {
        this.log = log;
        this.dbLocation = dbLocation;
    }

    @Override
    public Connection create() {
        Connection conn = null;
        try {
            final String jdbcPath = "jdbc:sqlite:%s?busy_timeout=5000".formatted(dbLocation);

            log.debug("Checking for SQLite JDBC driver...");
            Class.forName("org.sqlite.JDBC");

            log.debug("Connecting via SQLite JDBC to '%s'".formatted(jdbcPath));
            conn = DriverManager.getConnection(jdbcPath);
            log.debug("SQLite JDBC connection established successfully.");
        } catch (final ClassNotFoundException | SQLException e) {
            log.error("There was an exception with creating the Sqlite connection.", e);
        }

        if (conn == null) {
            throw new IllegalStateException("Not able to create a database connection!");
        }
        return conn;
    }
}
