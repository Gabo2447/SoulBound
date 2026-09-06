package io.zabrek.soulbound.database.data;

import io.zabrek.soulbound.api.config.ConfigAccessor;
import io.zabrek.soulbound.api.logger.SoulBoundLoggerFactory;
import io.zabrek.soulbound.api.profile.Profile;
import io.zabrek.soulbound.database.Connector;
import io.zabrek.soulbound.database.Saver;

/**
 * Factory to create PlayerData objects for profiles.
 */
public class PlayerDataFactory {

    /**
     * Factory to create new class-specific loggers.
     */
    private final SoulBoundLoggerFactory loggerFactory;

    /**
     * Saver to persist player data changes.
     */
    private final Saver saver;

    /**
     * The database connector.
     */
    private final Connector connector;

    /**
     * The config accessor to get the plugin config.
     */
    private final ConfigAccessor config;

    /**
     * Create a new Player Data Factory.
     *
     * @param loggerFactory the logger factory to create class-specific logger
     * @param saver         the saver to persist data changes
     * @param connector     the database connector to use
     * @param config        the config accessor to get the plugin config
     */
    public PlayerDataFactory(final SoulBoundLoggerFactory loggerFactory, final Saver saver, final Connector connector,
                             final ConfigAccessor config) {
        this.loggerFactory = loggerFactory;
        this.connector = connector;
        this.saver = saver;
        this.config = config;
    }

    /**
     * Create a new PlayerData.
     *
     * @param profile the profile to create the player data for
     * @return the newly created player data
     */
    public PlayerData createPlayerData(final Profile profile) {
        return new PlayerData(loggerFactory.create(PlayerData.class), saver, connector, profile, config);
    }
}
