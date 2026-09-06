package io.zabrek.soulbound.database.type;

import io.zabrek.soulbound.api.config.ConfigAccessor;
import io.zabrek.soulbound.api.logger.SoulBoundLogger;
import io.zabrek.soulbound.database.DatabaseUpdate;
import io.zabrek.soulbound.database.MigrationKey;
import io.zabrek.soulbound.database.provider.ConnectionProvider;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * Abstract Database class, serves as a base for any connection method (MySQL,
 * SQLite, etc.)
 */
public abstract class Database {

    /**
     * The plugin instance, used for accessing plugin's data folder.
     */
    protected final Plugin plugin;

    /**
     * The prefix for the database tables, used to avoid conflicts with.
     */
    protected final String prefix;

    /**
     * The initial name for the profile, used when creating a new profile.
     */
    protected final String profileInitialName;

    /**
     * The connection provider instance.
     */
    protected final ConnectionProvider connectionProvider;

    /**
     * Custom {@link SoulBoundLogger} instance for this class.
     */
    private final SoulBoundLogger log;

    /**
     * Creates a new Database instance.
     *
     * @param log                the SoulBoundLogger to use for logging
     * @param connectionProvider the connection provider instance
     * @param plugin             the SoulBound plugin instance
     * @param config             the config accessor instance
     */
    protected Database(final SoulBoundLogger log, final ConnectionProvider connectionProvider, final Plugin plugin, final ConfigAccessor config) {
        this.log = log;
        this.plugin = plugin;
        this.connectionProvider = connectionProvider;
        this.prefix = config.getString("database.prefix", "sb_");
        this.profileInitialName = config.getString("profile.initial_name", "default");
    }

    /**
     * Returns the current database connection.
     *
     * @return the database connection
     */
    public Connection getConnection() {
        log.debug("New database connection requested");
        return connectionProvider.create();
    }

    /**
     * Closes the database connection if it is open.
     */
    public void closeConnection() {
        connectionProvider.close();
    }

    /**
     * Creates the database tables by executing all migrations that have not been executed yet.
     */
    public final void createTables() {
        log.debug("Checking database tables and running pending migrations...");

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try {
                final SortedMap<MigrationKey, DatabaseUpdate> migrations = getMigrations();
                final Set<MigrationKey> executedMigrations = queryExecutedMigrations(conn);

                final List<Map.Entry<MigrationKey, DatabaseUpdate>> pendingMigrations = migrations.entrySet().stream()
                        .filter(entry -> !executedMigrations.contains(entry.getKey()))
                        .toList();

                log.debug("Pending migrations to execute: %d".formatted(pendingMigrations.size()));

                for (final Map.Entry<MigrationKey, DatabaseUpdate> entry : pendingMigrations) {
                    final MigrationKey key = entry.getKey();
                    final DatabaseUpdate migration = entry.getValue();

                    log.debug("Executing migration '%s'".formatted(key));
                    migration.executeUpdate(conn);
                    markMigrationExecuted(conn, key);
                    log.debug("Migration '%s' successfully executed".formatted(key));
                }
            } catch (final SQLException e) {
                conn.rollback();
                log.error("Migration failed, rolling back changes.", e);
                throw new IllegalStateException("Database migration failed. Halting application startup.", e);
            }

            conn.setAutoCommit(true);
        } catch (final SQLException sqlException) {
            log.error("Database connection error during migrations!", sqlException);
            throw new IllegalStateException(sqlException);
        }
    }

    /**
     * Returns a SortedMap of all migrations with an identifier as {@link MigrationKey} and the migration function as
     * Value.
     *
     * @return the SortedMap of all migrations
     */
    protected abstract SortedMap<MigrationKey, DatabaseUpdate> getMigrations();

    /**
     * Queries the database for all migrations that have been executed. The function have to ensure that the table
     * containing the executed migrations exists.
     *
     * @param connection the connection to the database
     * @return a set of all migrations, in form of {@link MigrationKey}, that have been executed
     * @throws SQLException if something went wrong with the query
     */
    protected abstract Set<MigrationKey> queryExecutedMigrations(Connection connection) throws SQLException;

    /**
     * Marks the migration as executed in the database to have been executed.
     *
     * @param connection   the connection to the database
     * @param migrationKey the specific migration to mark as executed
     * @throws SQLException if the migration could not be marked as executed
     */
    protected abstract void markMigrationExecuted(Connection connection, MigrationKey migrationKey) throws SQLException;
}
