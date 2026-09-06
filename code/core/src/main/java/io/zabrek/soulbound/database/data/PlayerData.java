package io.zabrek.soulbound.database.data;

import io.zabrek.soulbound.api.config.ConfigAccessor;
import io.zabrek.soulbound.api.data.CooldownRecord;
import io.zabrek.soulbound.api.data.LevelRecord;
import io.zabrek.soulbound.api.data.Skills;
import io.zabrek.soulbound.api.logger.SoulBoundLogger;
import io.zabrek.soulbound.api.profile.Profile;
import io.zabrek.soulbound.database.Arguments;
import io.zabrek.soulbound.database.Connector;
import io.zabrek.soulbound.database.QueryType;
import io.zabrek.soulbound.database.Saver;
import io.zabrek.soulbound.database.UpdateType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents an object storing all profile-related data, which can load and save it.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class PlayerData {

    /**
     * The profileID of the data.
     */
    private final String profileID;

    /**
     * Custom {@link SoulBoundLogger} instance for this class.
     */
    private final SoulBoundLogger log;

    /**
     * The database saver for player data.
     */
    private final Saver saver;

    /**
     * The database connector.
     */
    private final Connector connector;

    /**
     * The profile this data belongs to.
     */
    private final Profile profile;

    /**
     * The config accessor.
     */
    private final ConfigAccessor config;

    /**
     * List of levels the player has.
     */
    private final List<LevelRecord> levels = new CopyOnWriteArrayList<>();

    /**
     * List of cooldowns the player has.
     */
    private final List<CooldownRecord> cooldown = new CopyOnWriteArrayList<>();

    /**
     * The language for the profile.
     */
    @Nullable
    private String profileLanguage;

    /**
     * The current skill for the profile.
     */
    private Skills skillActive;

    /**
     * Loads the PlayerData of the given {@link Profile}.
     *
     * @param log       the custom logger for this class
     * @param saver     the saver to persist data changes
     * @param connector the database connector to use
     * @param profile   the profile to load the data for
     * @param config    the config accessor
     */
    public PlayerData(final SoulBoundLogger log, final Saver saver, final Connector connector, final Profile profile,
                      final ConfigAccessor config) {
        this.log = log;
        this.saver = saver;
        this.connector = connector;
        this.profile = profile;
        this.profileID = profile.getProfileUUID().toString();
        this.config = config;
        this.profileLanguage = null;
        this.skillActive = Skills.DEFAULT_SKILL;

        try {
            loadAllPlayerData();
        } catch (final IllegalStateException e) {
            log.error("Could not load player data for %s: %s".formatted(profileID, e.getMessage()));
        }
    }

    private void loadAllPlayerData() {
        log.debug("Loading player data from database for %s".formatted(profileID));
        final Arguments profileArgs = new Arguments(profileID);

        connector.querySQL(QueryType.SELECT_COOLDOWN, profileArgs, resultSet -> {
            while (resultSet.next()) {
                loadCooldown(resultSet.getString("skill"), resultSet.getString("time"));
            }
            log.debug("Loaded %d cooldowns for %s".formatted(cooldown.size(), profileID));
        }, "Could not load cooldowns.");

        connector.querySQL(QueryType.SELECT_LEVEL, profileArgs, resultSet -> {
            while (resultSet.next()) {
                loadLevel(resultSet.getString("skill"), resultSet.getInt("level"), resultSet.getDouble("experience"));
            }
            log.debug("Loaded %d level for %s".formatted(levels.size(), profileID));
        }, "Could not load level.");

        final String playerUniqueID = profile.getPlayer().getUniqueId().toString();
        final Arguments playerArgs = new Arguments(playerUniqueID);

        connector.querySQL(QueryType.SELECT_PLAYER, playerArgs, resultSet -> {
            if (resultSet.next()) {
                profileLanguage = resultSet.getString("language");
                skillActive = Skills.fromId(resultSet.getString("skill_active"));
                log.debug("Loaded player language '%s' for %s".formatted(profileLanguage, profile));
            } else {
                setupProfile();
            }
        }, "Could not load player data.");
    }

    private void loadCooldown(final String skill, final String time) {
        // final SkillIdentifier identifier = identifierRegistry.getFactory(SkillIdentifier.class).parseIdentifier(skill);
        final Skills skillID = Skills.fromId(skill);
        final CooldownRecord record = new CooldownRecord(skillID, time);
        cooldown.add(record);
    }

    private void loadLevel(final String skill, final int level, final double experience) {
        // final SkillIdentifier identifier = identifierRegistry.getFactory(SkillIdentifier.class).parseIdentifier(skill);
        final Skills skillID = Skills.fromId(skill);
        final LevelRecord record = new LevelRecord(skillID, level, experience);
        levels.add(record);
    }

    private void setupProfile() {
        log.debug("Profile not found in database. Setting up new profile in database for %s".formatted(profileID));
        final String playerUniqueID = profile.getPlayer().getUniqueId().toString();

        saver.add(new Saver.Record(UpdateType.ADD_PROFILE, profileID));
        saver.add(new Saver.Record(UpdateType.ADD_PLAYER, playerUniqueID, profileID, "en", Skills.DEFAULT_SKILL.getId()));
        saver.add(new Saver.Record(UpdateType.ADD_PLAYER_PROFILE, playerUniqueID, profileID,
                config.getString("profile.initial_name", "default")));

        setupLevel();
    }

    private void setupLevel() {
        for (final Skills skill : Skills.values()) {
            final LevelRecord record = new LevelRecord(skill, 1, 0.0);
            levels.add(record);
            saver.add(new Saver.Record(UpdateType.ADD_LEVEL, profileID, record.skill(), record.level(), record.experience()));
        }
    }

    /**
     * Get the cooldowns entries.
     *
     * @return an unmodifiable list of the profiles cooldowns entries
     */
    public List<CooldownRecord> getCooldowns() {
        return List.copyOf(cooldown);
    }

    /**
     * Sets player's cooldown.
     *
     * @param records the cooldowns
     */
    public void setCooldowns(final List<CooldownRecord> records) {
        log.debug("Setting cooldown for %s with %d cooldowns".formatted(profileID, records.size()));
        this.cooldown.clear();
        this.cooldown.addAll(records);
        refreshCooldown(this.cooldown);
    }

    /**
     * Adds a new cooldown for the profile.
     *
     * @param record the new cooldown
     */
    public void addCooldown(final CooldownRecord record) {
        log.debug("Adding cooldown for %s".formatted(profileID));
        cooldown.add(record);
        saver.add(new Saver.Record(UpdateType.ADD_COOLDOWN, profileID, record.skill().getId(), record.time()));
    }

    /**
     * Removes a cooldown for the profile.
     *
     * @param record the cooldown to remove
     */
    public void removeCooldown(final CooldownRecord record) {
        log.debug("Removing cooldown for %s".formatted(profileID));
        if (cooldown.remove(record)) {
            refreshCooldown(cooldown);
        }
    }

    /**
     * Get the levels entries.
     *
     * @return an unmodifiable list of the profiles levels entries
     */
    public List<LevelRecord> getLevels() {
        return List.copyOf(levels);
    }

    /**
     * Set's the player levels.
     *
     * @param records the levels
     */
    public void setLevels(final List<LevelRecord> records) {
        log.debug("Setting level for %s with %d levels".formatted(profileID, records.size()));
        this.levels.clear();
        this.levels.addAll(records);
        refreshLevels(this.levels);
    }

    /**
     * Updates or add any player level.
     *
     * @param record the level to update
     */
    public void updateLevel(final LevelRecord record) {
        log.debug("Updating level for %s on skill %s".formatted(profileID, record.skill().getId()));
        levels.removeIf(level -> level.skill() == record.skill());
        levels.add(record);
        saver.add(new Saver.Record(UpdateType.UPDATE_PLAYER_LEVEL, record.level(), record.experience(), profileID, record.skill().getId()));
    }

    /**
     * Gets player's language.
     *
     * @return the language this profile uses
     */
    public Optional<String> getLanguage() {
        return Optional.ofNullable(profileLanguage);
    }

    /**
     * Sets player's language.
     *
     * @param lang language to set
     */
    public void setLanguage(@Nullable final String lang) {
        if (Objects.equals(profileLanguage, lang)) {
            return;
        }

        log.debug("Setting language for %s to '%s'".formatted(profile, lang));
        this.profileLanguage = lang;
        saver.add(new Saver.Record(UpdateType.UPDATE_PLAYER_LANGUAGE, lang, profile.getPlayer().getUniqueId().toString()));
    }

    /**
     * Gets player's skill.
     *
     * @return the skill this profile uses
     */
    public Skills getSkill() {
        return skillActive;
    }

    /**
     * Sets player's skill.
     *
     * @param skill skill to set
     */
    public void setSkill(final Skills skill) {
        if (skillActive == skill) {
            return;
        }

        log.debug("Setting active skill for %s to '%s'".formatted(profile, skill));
        this.skillActive = skill;
        saver.add(new Saver.Record(UpdateType.UPDATE_PLAYER_SKILL, skillActive.getId(), profile.getPlayer().getUniqueId().toString()));
    }

    /**
     * Purges all profile's data from the database and from this object.
     */
    public void purgePlayer() {
        log.debug("Purging all data for %s".formatted(profileID));

        cooldown.clear();
        levels.clear();

        saver.add(new Saver.Record(UpdateType.DELETE_ALL_COOLDOWNS, profileID));
        saver.add(new Saver.Record(UpdateType.DELETE_ALL_LEVELS, profileID));
    }

    private void refreshCooldown(final List<CooldownRecord> records) {
        log.debug("Refreshing cooldown in database for %s (currently %d items)".formatted(profileID, records.size()));
        saver.add(new Saver.Record(UpdateType.DELETE_ALL_COOLDOWNS, profileID));
        for (final CooldownRecord record : records) {
            saver.add(new Saver.Record(UpdateType.ADD_COOLDOWN, profileID, record.skill(), record.time()));
        }
    }

    private void refreshLevels(final List<LevelRecord> records) {
        log.debug("Refreshing levels in database for %s".formatted(profileID));
        saver.add(new Saver.Record(UpdateType.DELETE_ALL_LEVELS, profileID));
        for (final LevelRecord record : records) {
            saver.add(new Saver.Record(UpdateType.ADD_LEVEL, profileID, record.skill(), record.level(), record.experience()));
        }
    }
}
