package io.zabrek.soulbound.database;

import io.zabrek.soulbound.api.config.ConfigAccessor;
import io.zabrek.soulbound.api.config.ConfigAccessorFactory;
import io.zabrek.soulbound.api.config.FileConfigAccessor;
import io.zabrek.soulbound.api.logger.SoulBoundLogger;
import io.zabrek.soulbound.api.logger.SoulBoundLoggerFactory;
import io.zabrek.soulbound.config.Zipper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This class is responsible for backing up and restoring the database.
 */
public final class Backup {

    /**
     * The logger factory to use.
     */
    private final SoulBoundLoggerFactory loggerFactory;

    /**
     * Custom {@link SoulBoundLogger} instance for this class.
     */
    private final SoulBoundLogger log;

    /**
     * Factory that will be used to create {@link ConfigAccessor}s.
     */
    private final ConfigAccessorFactory configAccessorFactory;

    /**
     * Folder to back up its contents.
     */
    private final File root;

    /**
     * Connector to access a database.
     */
    private final Connector con;

    /**
     * Single file to store a database backup to.
     */
    private final File databaseBackupFile;

    /**
     * Folder to store a full backup to.
     */
    private final File backupFolder;

    /**
     * Creates a new Object to store and load backups.
     * It will use the "database-backup.yml" for a single database backup and the "Backups" folder for a full backup
     * inside the given root folder.
     *
     * @param loggerFactory         the logger factory to use
     * @param log                   the custom {@link SoulBoundLogger} instance for this class
     * @param configAccessorFactory the factory that will be used to create {@link ConfigAccessor}s
     * @param root                  the directory to back up and load to
     * @param connector             the connector used for database access
     */
    public Backup(final SoulBoundLoggerFactory loggerFactory, final SoulBoundLogger log, final ConfigAccessorFactory configAccessorFactory,
                  final File root, final Connector connector) {
        this(loggerFactory, log, configAccessorFactory, root, connector, new File(root, "database-backup.yml"), new File(root, "Backups"));
    }

    /**
     * Creates a new Object to store and load backups.
     *
     * @param loggerFactory         the logger factory to use
     * @param log                   the custom {@link SoulBoundLogger} instance for this class
     * @param configAccessorFactory the factory that will be used to create {@link ConfigAccessor}s
     * @param root                  the directory to back up and load to
     * @param connector             the connector used for database access
     * @param databaseBackupFile    the file to store/load a single database backup
     * @param backupFolder          the folder to store/load the full backup
     */
    public Backup(final SoulBoundLoggerFactory loggerFactory, final SoulBoundLogger log, final ConfigAccessorFactory configAccessorFactory, final File root,
                  final Connector connector, final File databaseBackupFile, final File backupFolder) {
        this.loggerFactory = loggerFactory;
        this.log = log;
        this.configAccessorFactory = configAccessorFactory;
        this.root = root;
        this.con = connector;
        this.databaseBackupFile = databaseBackupFile;
        this.backupFolder = backupFolder;
    }

    /**
     * Does a full configuration backup.
     * The backup folder and file are not allowed to exist.
     *
     * @param version the version string to use for the backup zip name
     */
    public void backup(final String version) {
        log.info("Backing up!");
        final long time = System.currentTimeMillis();

        try {
            backupDatabase();
        } catch (final IllegalArgumentException | IllegalStateException e) {
            log.warn("There was an error during backing up the database! This does not affect"
                    + " the configuration backup, nor damage your database. You should backup"
                    + " the database manually if you want to be extra safe, but it's not necessary if"
                    + " you don't want to downgrade later. Reason: %s".formatted(e.getMessage()), e);
        }

        if (!backupFolder.isDirectory() && !backupFolder.mkdirs()) {
            log.error("Could not create the backup folder!");
        }

        final String outputPath = backupFolder.getAbsolutePath() + File.separator + "backup-" + version;
        new Zipper(loggerFactory.create(Zipper.class, "Zipper"), root, "^backup.*", "^database\\.db$", "^logs$")
                .zip(outputPath);

        if (!databaseBackupFile.delete()) {
            log.warn("Could not delete database backup file!");
        }

        log.debug("Done in " + (System.currentTimeMillis() - time) + "ms");
        log.info("Done, you can find the backup in 'Backups' directory.");
    }

    /**
     * Backs the database up to the {@code databaseBackupFile} if that file does not exist.
     *
     * @throws IllegalArgumentException if the file cannot be created
     */
    public void backupDatabase() {
        backupDatabase(databaseBackupFile);
    }

    private void backupDatabase(final File databaseBackupFile) {
        try {
            if (!databaseBackupFile.createNewFile()) {
                throw new IllegalArgumentException("Could not create the backup file!");
            }
            final FileConfigAccessor config = configAccessorFactory.create(databaseBackupFile);
            final String[] tables = {"level", "cooldown", "player", "migration", "player_profile", "profile"};
            for (final String table : tables) {
                log.debug("Loading " + table);
                final String enumName = ("LOAD_ALL_" + table).toUpperCase(Locale.ROOT);
                con.querySQL(QueryType.valueOf(enumName), new Arguments(),
                        resultSet -> writeBackup(table, resultSet, config),
                        "Could not backup %s from database!".formatted(table));
            }
            config.save();
        } catch (final IOException | InvalidConfigurationException e) {
            log.warn("There was an error during database backup: " + e.getMessage(), e);
            if (databaseBackupFile.exists() && !databaseBackupFile.delete()) {
                log.warn("Could not delete the broken backup file!");
            }
        }
    }

    private void writeBackup(final String table, final ResultSet resultSet, final FileConfigAccessor config) throws SQLException {
        log.debug("Saving %s to the backup file".formatted(table));
        final ResultSetMetaData result = resultSet.getMetaData();
        final List<String> columns = new ArrayList<>();
        final int columnCount = result.getColumnCount();
        log.debug("  There are %d columns in this ResultSet".formatted(columnCount));

        for (int i = 1; i <= result.getColumnCount(); i++) {
            final String columnName = result.getColumnName(i);
            log.debug("    Adding column %s".formatted(columnName));
            columns.add(columnName);
        }

        int counter = 0;
        while (resultSet.next()) {
            for (final String columnName : columns) {
                try {
                    final String value = resultSet.getString(columnName);
                    config.set("%s.%d.%s".formatted(table, counter, columnName), value);
                } catch (final SQLException e) {
                    throw new IllegalStateException("Could not read SQL: " + e.getMessage(), e);
                }
            }
            counter++;
        }
        log.debug("  Saved " + (counter + 1) + " rows");
    }

    /**
     * Loads an existing {@code databaseBackupFile} into the database.
     * The existing database is saved into the {@code backupFolder}.
     */
    public void loadDatabaseFromBackup() {
        if (!databaseBackupFile.exists()) {
            return;
        }
        log.info("Loading database backup!");

        if (!backupFolder.isDirectory() && !backupFolder.mkdirs()) {
            log.warn("Could not create the backup folder!");
            return;
        }
        int backupNumber = 0;
        while (new File(backupFolder, "old-database-" + backupNumber + ".yml").exists()) {
            backupNumber++;
        }
        final String filename = "old-database-" + backupNumber + ".yml";
        log.info("Backing up old database!");
        try {
            backupDatabase(new File(backupFolder, filename));
        } catch (final IllegalArgumentException | IllegalStateException e) {
            log.warn("There was an error during old database backup process. This means that"
                    + " if the plugin loaded new database (from backup), the old one would be lost "
                    + "forever. Because of that the loading of backup was aborted! Reason: %s".formatted(e.getMessage()), e);
            return;
        }
        final ConfigAccessor config;
        try {
            config = configAccessorFactory.create(databaseBackupFile);
        } catch (final InvalidConfigurationException | FileNotFoundException e) {
            log.warn(e.getMessage(), e);
            return;
        }
        // create tables if they don't exist, so we can be 100% sure
        // that we can drop them without an error
        con.getDatabase().createTables();
        // drop all tables
        final Arguments args = new Arguments();
        con.updateSQL(UpdateType.DROP_LEVEL, args);
        con.updateSQL(UpdateType.DROP_COOLDOWN, args);
        con.updateSQL(UpdateType.DROP_MIGRATION, args);
        con.updateSQL(UpdateType.DROP_PLAYER_PROFILE, args);
        con.updateSQL(UpdateType.DROP_PLAYER, args);
        con.updateSQL(UpdateType.DROP_PROFILE, args);
        con.updateSQL(UpdateType.DROP_TRIGGERS, args);

        loadDatabaseFromBackup0(config);
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity", "PMD.NPathComplexity", "PMD.AvoidDuplicateLiterals"})
    private void loadDatabaseFromBackup0(final ConfigAccessor config) {
        con.getDatabase().createTables();

        final ConfigurationSection profile = config.getConfigurationSection("profile");
        if (profile != null) {
            for (final String key : profile.getKeys(false)) {
                con.updateSQL(UpdateType.ADD_PROFILE, new Arguments(
                        profile.getString(key + ".profileID")));
            }
        }
        final ConfigurationSection player = config.getConfigurationSection("player");
        if (player != null) {
            for (final String key : player.getKeys(false)) {
                con.updateSQL(UpdateType.ADD_PLAYER, new Arguments(
                        player.getString(key + ".playerID"),
                        player.getString(key + ".active_profile"),
                        player.getString(key + ".language"),
                        player.getString(key + ".active_skill")));
            }
        }
        final ConfigurationSection playerProfile = config.getConfigurationSection("player_profile");
        if (playerProfile != null) {
            for (final String key : playerProfile.getKeys(false)) {
                con.updateSQL(UpdateType.ADD_PLAYER_PROFILE, new Arguments(
                        playerProfile.getString(key + ".playerID"),
                        playerProfile.getString(key + ".profileID"),
                        playerProfile.getString(key + ".name")));
            }
        }
        final ConfigurationSection level = config.getConfigurationSection("level");
        if (level != null) {
            for (final String key : level.getKeys(false)) {
                con.updateSQL(UpdateType.ADD_LEVEL, new Arguments(
                        level.getString(key + ".profileID"),
                        level.getString(key + ".skill"),
                        level.getInt(key + ".level"),
                        level.getInt(key + ".experience")));
            }
        }
        final ConfigurationSection cooldown = config.getConfigurationSection("cooldown");
        if (cooldown != null) {
            for (final String key : cooldown.getKeys(false)) {
                con.updateSQL(UpdateType.ADD_COOLDOWN, new Arguments(
                        cooldown.getString(key + ".profileID"),
                        cooldown.getString(key + ".skill"),
                        cooldown.getString(key + ".time")));
            }
        }
        final ConfigurationSection triggers = config.getConfigurationSection("triggers");
        if (triggers != null) {
            for (final String key : triggers.getKeys(false)) {
                con.updateSQL(UpdateType.ADD_TRIGGERS, new Arguments(
                        triggers.getString(key, "profileID"),
                        triggers.getString(key, "trigger"),
                        triggers.getString(key, "instructions")
                ));
            }
        }
        if (!databaseBackupFile.delete()) {
            log.warn("Could not delete the backup file!");
        }
    }
}
