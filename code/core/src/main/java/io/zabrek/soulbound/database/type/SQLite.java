package io.zabrek.soulbound.database.type;

import io.zabrek.soulbound.api.logger.SoulBoundLogger;
import io.zabrek.soulbound.database.DatabaseUpdate;
import io.zabrek.soulbound.database.MigrationKey;
import org.apache.logging.log4j.core.internal.annotation.SuppressFBWarnings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Connects to and uses a SQLite database.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class SQLite extends Database {

    /**
     * Custom {@link SoulBoundLogger} instance.
     */
    private final SoulBoundLogger log;

    /**
     * Creates a new SQLite instance.
     *
     * @param config all properties
     */
    public SQLite(final DatabaseConfig config) {
        super(config.log(), config.connProvider(), config.plugin(), config.configAccessor());
        this.log = config.log();
    }

    @Override
    protected SortedMap<MigrationKey, DatabaseUpdate> getMigrations() {
        final SortedMap<MigrationKey, DatabaseUpdate> migrations = new TreeMap<>();
        migrations.put(new MigrationKey("soulbound", 1), this::migration1);
        migrations.put(new MigrationKey("soulbound", 2), this::migration2);
        migrations.put(new MigrationKey("soulbound", 3), this::migration3);
        return migrations;
    }

    @Override
    protected Set<MigrationKey> queryExecutedMigrations(final Connection connection) throws SQLException {
        log.debug("Querying executed migrations from table '%s' migration".formatted(prefix));
        final Set<MigrationKey> executedMigrations = new HashSet<>();

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix
                    + "migration (namespace VARCHAR(63) NOT NULL, migration_id INT, "
                    + "time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (namespace, migration_id))");

            try (ResultSet result = stmt.executeQuery("SELECT namespace, migration_id FROM " + prefix + "migration")) {
                while (result.next()) {
                    executedMigrations.add(new MigrationKey(result.getString("namespace"), result.getInt("migration_id")));
                }
            }
        }

        return executedMigrations;
    }

    @Override
    protected void markMigrationExecuted(final Connection connection, final MigrationKey migrationKey) throws SQLException {
        log.debug("Marking migration '%s' as executed.".formatted(migrationKey));
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO " + prefix + "migration (namespace, migration_id) VALUES (?, ?)")) {
            stmt.setString(1, migrationKey.namespace());
            stmt.setInt(2, migrationKey.version());
            stmt.executeUpdate();
        }
    }

    /**
     * Executes the first migration.
     *
     * @param connection the connection to the database
     * @throws SQLException if something goes wrong, while executing the query's
     */
    @SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
    private void migration1(final Connection connection) throws SQLException {
        log.debug("Running SQLite migration 1 (initial table creation)...");
        final boolean originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "cooldown ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "playerID VARCHAR(256) NOT NULL, "
                    + "skill VARCHAR(256) NOT NULL, "
                    + "time TEXT NOT NULL);");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "player ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "playerID VARCHAR(256) NOT NULL, "
                    + "language VARCHAR(16) NOT NULL, "
                    + "skill_active VARCHAR(32) NOT NULL);");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "level ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "playerID VARCHAR(256) NOT NULL, "
                    + "skill VARCHAR(256) NOT NULL, "
                    + "level INT NOT NULL, "
                    + "experience INT NOT NULL);");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "triggers ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "playerID VARCHAR(256) NOT NULL, "
                    + "triggers VARCHAR(512) NOT NULL, "
                    + "instructions VARCHAR(2048) NOT NULL);");

            connection.commit();
        } catch (final SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    /**
     * Executes the second migration.
     *
     * @param connection the connection to the database
     * @throws SQLException if something goes wrong, while executing the query's
     */
    @SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
    private void migration2(final Connection connection) throws SQLException {
        log.debug("Running SQLite migration 2 (profiles table migration)...");
        final boolean originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "profile ("
                    + "profileID CHAR(36) PRIMARY KEY NOT NULL)");
            stmt.executeUpdate("INSERT OR IGNORE INTO " + prefix + "profile "
                    + "(profileID) SELECT playerID FROM " + prefix + "player");

            stmt.executeUpdate("CREATE TABLE " + prefix + "triggers_tmp ("
                    + "profileID CHAR(36) NOT NULL, "
                    + "triggers VARCHAR(512) NOT NULL, "
                    + "instructions VARCHAR(2048) NOT NULL, "
                    + "PRIMARY KEY (profileID, triggers), "
                    + "FOREIGN KEY (profileID) REFERENCES " + prefix + "profile (profileID) ON DELETE CASCADE)");
            stmt.executeUpdate("INSERT OR IGNORE INTO " + prefix + "triggers_tmp "
                    + "(profileID, triggers, instructions) SELECT playerID, triggers, instructions FROM " + prefix + "triggers");
            stmt.executeUpdate("DROP TABLE " + prefix + "triggers");
            stmt.executeUpdate("ALTER TABLE " + prefix + "triggers_tmp RENAME TO " + prefix + "triggers");

            stmt.executeUpdate("CREATE TABLE " + prefix + "cooldown_tmp ("
                    + "profileID CHAR(36) NOT NULL, "
                    + "skill VARCHAR(512) NOT NULL, "
                    + "time TEXT NOT NULL, "
                    + "PRIMARY KEY (profileID, skill), "
                    + "FOREIGN KEY (profileID) REFERENCES " + prefix + "profile (profileID) ON DELETE CASCADE)");
            stmt.executeUpdate("INSERT OR IGNORE INTO " + prefix + "cooldown_tmp "
                    + "(profileID, skill, time) SELECT playerID, skill, time FROM " + prefix + "cooldown");
            stmt.executeUpdate("DROP TABLE " + prefix + "cooldown");
            stmt.executeUpdate("ALTER TABLE " + prefix + "cooldown_tmp RENAME TO " + prefix + "cooldown");

            stmt.executeUpdate("CREATE TABLE " + prefix + "level_tmp ("
                    + "profileID CHAR(36) NOT NULL, "
                    + "skill VARCHAR(256) NOT NULL, "
                    + "level INT NOT NULL, "
                    + "experience INT NOT NULL, "
                    + "PRIMARY KEY (profileID, skill), "
                    + "FOREIGN KEY (profileID) REFERENCES " + prefix + "profile (profileID) ON DELETE CASCADE)");
            stmt.executeUpdate("INSERT OR IGNORE INTO " + prefix + "level_tmp "
                    + "(profileID, skill, level, experience) SELECT playerID, skill, level, experience FROM " + prefix + "level");
            stmt.executeUpdate("DROP TABLE " + prefix + "level");
            stmt.executeUpdate("ALTER TABLE " + prefix + "level_tmp RENAME TO " + prefix + "level");

            stmt.executeUpdate("CREATE TABLE " + prefix + "player_tmp ("
                    + "playerID CHAR(36) NOT NULL, "
                    + "active_profile CHAR(36) NOT NULL, "
                    + "language VARCHAR(16) NOT NULL, "
                    + "skill_active VARCHAR(32) NOT NULL, "
                    + "PRIMARY KEY (playerID), "
                    + "FOREIGN KEY (active_profile) REFERENCES " + prefix + "profile (profileID) ON DELETE RESTRICT)");
            stmt.executeUpdate("INSERT OR IGNORE INTO " + prefix + "player_tmp "
                    + "(playerID, language, active_profile, skill_active) SELECT playerID, language, playerID, skill_active FROM " + prefix + "player");
            stmt.executeUpdate("DROP TABLE " + prefix + "player");
            stmt.executeUpdate("ALTER TABLE " + prefix + "player_tmp RENAME TO " + prefix + "player");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + prefix + "player_profile ("
                    + "playerID CHAR(36) NOT NULL, "
                    + "profileID CHAR(36) NOT NULL, "
                    + "name VARCHAR(63), "
                    + "PRIMARY KEY (playerID, profileID), "
                    + "FOREIGN KEY (playerID) REFERENCES " + prefix + "player (playerID) ON DELETE CASCADE, "
                    + "FOREIGN KEY (profileID) REFERENCES " + prefix + "profile (profileID) ON DELETE CASCADE, "
                    + "UNIQUE (playerID, name))");
            stmt.executeUpdate("INSERT OR IGNORE INTO " + prefix + "player_profile "
                    + "(playerID, profileID, name) SELECT playerID, active_profile, NULL FROM " + prefix + "player");

            connection.commit();
        } catch (final SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    private void migration3(final Connection connection) throws SQLException {
        log.debug("Running SQLite migration 3 (player_profile name migration)...");
        final boolean originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);

        try {
            try (PreparedStatement updateStmt = connection.prepareStatement(
                    "UPDATE " + prefix + "player_profile SET name = ? WHERE name IS NULL")) {
                updateStmt.setString(1, profileInitialName);
                updateStmt.executeUpdate();
            }

            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("CREATE TABLE " + prefix + "player_profile_tmp ("
                        + "playerID CHAR(36) NOT NULL, "
                        + "profileID CHAR(36) NOT NULL, "
                        + "name VARCHAR(63) NOT NULL, "
                        + "PRIMARY KEY (playerID, profileID), "
                        + "FOREIGN KEY (playerID) REFERENCES " + prefix + "player (playerID) ON DELETE CASCADE, "
                        + "FOREIGN KEY (profileID) REFERENCES " + prefix + "profile (profileID) ON DELETE CASCADE, "
                        + "UNIQUE (playerID, name))");
                stmt.executeUpdate("INSERT OR IGNORE INTO " + prefix + "player_profile_tmp "
                        + "(playerID, profileID, name) SELECT playerID, profileID, name FROM " + prefix + "player_profile");
                stmt.executeUpdate("DROP TABLE " + prefix + "player_profile");
                stmt.executeUpdate("ALTER TABLE " + prefix + "player_profile_tmp RENAME TO " + prefix + "player_profile");
            }

            connection.commit();
        } catch (final SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }
}
