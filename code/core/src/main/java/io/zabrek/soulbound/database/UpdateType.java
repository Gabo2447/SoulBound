package io.zabrek.soulbound.database;

import java.util.function.Function;

/**
 * Type of the update.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public enum UpdateType {

    // --- INSERT OPERATIONS ---
    /**
     * Inserts a new trigger. Params: ProfileID, trigger, instructions.
     */
    ADD_TRIGGERS(prefix -> "INSERT INTO " + prefix + "triggers (profileID, triggers, instructions) VALUES (?, ?, ?);"),

    /**
     * Inserts a new player profile. Params: PlayerID, profileID, name.
     */
    ADD_PLAYER_PROFILE(prefix -> "INSERT INTO " + prefix + "player_profile (playerID, profileID, name) VALUES (?, ?, ?);"),

    /**
     * Inserts a new profile. Params: ProfileID.
     */
    ADD_PROFILE(prefix -> "INSERT INTO " + prefix + "profile (profileID) VALUES (?);"),

    /**
     * Inserts a new cooldown. Params: ProfileID, skill, time.
     */
    ADD_COOLDOWN(prefix -> "INSERT INTO " + prefix + "cooldown (profileID, skill, time) VALUES (?, ?, ?);"),

    /**
     * Inserts a new level record. Params: ProfileID, skill, level, experience.
     */
    ADD_LEVEL(prefix -> "INSERT INTO " + prefix + "level (profileID, skill, level, experience) VALUES (?, ?, ?, ?);"),

    /**
     * Inserts a new player. Params: PlayerID, active_profile, language, skill_active.
     */
    ADD_PLAYER(prefix -> "INSERT INTO " + prefix + "player (playerID, active_profile, language, skill_active) VALUES (?, ?, ?, ?);"),

    // --- REMOVE / DELETE OPERATIONS ---

    /**
     * Removes a specific trigger. Params: ProfileID, triggerID.
     */
    DELETE_TRIGGERS(prefix -> "DELETE FROM " + prefix + "triggers WHERE profileID = ? AND triggers = ?;"),

    /**
     * Removes a specific player profile association. Params: PlayerID, ProfileID.
     */
    DELETE_PLAYER_PROFILE(prefix -> "DELETE FROM " + prefix + "player_profile WHERE playerID = ? AND profileID = ?;"),

    /**
     * Removes a profile. Params: ProfileID.
     */
    DELETE_PROFILE(prefix -> "DELETE FROM " + prefix + "profile WHERE profileID = ?;"),

    /**
     * Removes a specific cooldown. Params: ProfileID, skill.
     */
    DELETE_COOLDOWN(prefix -> "DELETE FROM " + prefix + "cooldown WHERE profileID = ? AND skill = ?;"),

    /**
     * Removes a specific skill level. Params: ProfileID, skill.
     */
    DELETE_LEVEL(prefix -> "DELETE FROM " + prefix + "level WHERE profileID = ? AND skill = ?;"),

    /**
     * Deletes all triggers for a profile. Params: ProfileID.
     */
    DELETE_ALL_TRIGGERS(prefix -> "DELETE FROM " + prefix + "triggers WHERE profileID = ?;"),

    /**
     * Deletes all cooldowns for a profile. Params: ProfileID.
     */
    DELETE_ALL_COOLDOWNS(prefix -> "DELETE FROM " + prefix + "cooldown WHERE profileID = ?;"),

    /**
     * Deletes all levels for a profile. Params: ProfileID.
     */
    DELETE_ALL_LEVELS(prefix -> "DELETE FROM " + prefix + "level WHERE profileID = ?;"),

    /**
     * Deletes a player entirely. Params: PlayerID.
     */
    DELETE_PLAYER(prefix -> "DELETE FROM " + prefix + "player WHERE playerID = ?;"),

    /**
     * Removes a specific trigger definition globally. Params: Trigger name.
     */
    DELETE_GLOBAL_TRIGGER(prefix -> "DELETE FROM " + prefix + "triggers WHERE triggers = ?;"),

    // --- UPDATE OPERATIONS ---

    /**
     * Reassigns all triggers from one profile to another. Params: New ProfileID, Old ProfileID.
     */
    UPDATE_TRIGGERS_PROFILE(prefix -> "UPDATE " + prefix + "triggers SET profileID = ? WHERE profileID = ?;"),

    /**
     * Updates a profile name for a specific player. Params: Name, PlayerID, ProfileID.
     */
    UPDATE_PROFILE_NAME(prefix -> "UPDATE " + prefix + "player_profile SET name = ? WHERE playerID = ? AND profileID = ?;"),

    /**
     * Updates player level for a skill. Params: Level, Experience, ProfileID, Skill.
     */
    UPDATE_PLAYER_LEVEL(prefix -> "UPDATE " + prefix + "level SET level = ?, experience = ? WHERE profileID = ? AND skill = ?;"),

    /**
     * Updates cooldown timestamp for a skill. Params: Time, ProfileID, Skill.
     */
    UPDATE_PLAYER_COOLDOWN(prefix -> "UPDATE " + prefix + "cooldown SET time = ? WHERE profileID = ? AND skill = ?;"),

    /**
     * Updates player language setting. Params: Language, PlayerID.
     */
    UPDATE_PLAYER_LANGUAGE(prefix -> "UPDATE " + prefix + "player SET language = ? WHERE playerID = ?;"),

    /**
     * Updates player active skill. Params: Active Skill, PlayerID.
     */
    UPDATE_PLAYER_SKILL(prefix -> "UPDATE " + prefix + "player SET skill_active = ? WHERE playerID = ?;"),

    /**
     * Renames a trigger identifier across all profiles. Params: New Trigger Name, Old Trigger Name.
     */
    RENAME_TRIGGER_GLOBAL(prefix -> "UPDATE " + prefix + "triggers SET triggers = ? WHERE triggers = ?;"),

    // --- DDL DROP OPERATIONS ---
    /**
     * Drops the triggers table.
     */
    DROP_TRIGGERS(prefix -> "DROP TABLE " + prefix + "triggers;"),
    /**
     * Drops the player profile table.
     */
    DROP_PLAYER_PROFILE(prefix -> "DROP TABLE " + prefix + "player_profile;"),
    /**
     * Drops the profile table.
     */
    DROP_PROFILE(prefix -> "DROP TABLE " + prefix + "profile;"),
    /**
     * Drops the player table.
     */
    DROP_PLAYER(prefix -> "DROP TABLE " + prefix + "player;"),
    /**
     * Drops the cooldown table.
     */
    DROP_COOLDOWN(prefix -> "DROP TABLE " + prefix + "cooldown;"),
    /**
     * Drops the level table.
     */
    DROP_LEVEL(prefix -> "DROP TABLE " + prefix + "level;"),
    /**
     * Drops the migration table.
     */
    DROP_MIGRATION(prefix -> "DROP TABLE " + prefix + "migration;");

    /**
     * Function to create the SQL code from a prefix.
     */
    private final Function<String, String> statementCreator;

    UpdateType(final Function<String, String> sqlTemplate) {
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
