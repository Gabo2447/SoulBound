package io.zabrek.soulbound.database;

import java.util.function.Function;

/**
 * Type of the query.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public enum QueryType {

    /**
     * Get the triggers of a profile. ProfileID.
     */
    SELECT_TRIGGERS(prefix -> "SELECT triggers, instructions FROM " + prefix + "triggers WHERE profileID = ?;"),
    /**
     * Get the cooldown of a profile. ProfileID.
     */
    SELECT_COOLDOWN(prefix -> "SELECT skill, time FROM " + prefix + "cooldown WHERE profileID = ?;"),
    /**
     * Get the level of a profile. ProfileID.
     */
    SELECT_LEVEL(prefix -> "SELECT skill, level, experience FROM " + prefix + "level WHERE profileID = ?;"),
    /**
     * Get the current language of a profile. ProfileID.
     */
    SELECT_PLAYER(prefix -> "SELECT language, skill_active FROM " + prefix + "player WHERE playerID = ?;"),

    /**
     * Get all triggers.
     */
    LOAD_ALL_TRIGGERS(prefix -> "SELECT * FROM " + prefix + "triggers;"),
    /**
     * Get all players.
     */
    LOAD_ALL_PLAYER(prefix -> "SELECT * FROM " + prefix + "player;"),
    /**
     * Get all cooldowns.
     */
    LOAD_ALL_COOLDOWNS(prefix -> "SELECT * FROM " + prefix + "cooldown;"),
    /**
     * Get all levels.
     */
    LOAD_ALL_LEVELS(prefix -> "SELECT * FROM " + prefix + "level;"),
    /**
     * Get all players profiles.
     */
    LOAD_ALL_PLAYER_PROFILE(prefix -> "SELECT * FROM " + prefix + "player_profile;"),
    /**
     * Get all profiles.
     */
    LOAD_ALL_PROFILE(prefix -> "SELECT * FROM " + prefix + "profile;");

    /**
     * Function to create the SQL code from a prefix.
     */
    private final Function<String, String> statementCreator;

    QueryType(final Function<String, String> sqlTemplate) {
        this.statementCreator = sqlTemplate;
    }

    /**
     * Create the SQL code for the given table prefix.
     *
     * @param tablePrefix table prefix to use
     * @return SQL-code for the update
     */
    public String createSql(final String tablePrefix) {
        return statementCreator.apply(tablePrefix);
    }
}
