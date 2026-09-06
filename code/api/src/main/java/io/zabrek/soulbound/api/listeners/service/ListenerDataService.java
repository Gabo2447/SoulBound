package io.zabrek.soulbound.api.listeners.service;

import io.zabrek.soulbound.api.data.CooldownRecord;
import io.zabrek.soulbound.api.data.LevelRecord;
import io.zabrek.soulbound.api.data.Skills;
import io.zabrek.soulbound.api.profile.Profile;

import java.util.List;

/**
 * Provides data for listener.
 *
 * @since 2.0.0
 */
public interface ListenerDataService {

    /**
     * Gets the data for this profile in the cache.
     *
     * @param profile the profile to get the data
     * @return data
     * @since 2.0.0
     */
    ListenerPlayerData get(Profile profile);

    /**
     * Gets the cooldowns data for this profile in the cache.
     *
     * @param profile the profile to get the data
     * @return data
     * @since 2.0.0
     */
    List<CooldownRecord> getCooldowns(Profile profile);

    /**
     * Gets the levels data for this profile in the cache.
     *
     * @param profile the profile to get the data
     * @return data
     * @since 2.0.0
     */
    List<LevelRecord> getLevels(Profile profile);

    /**
     * Gets the language for this profile.
     *
     * @param profile the profile to get the data
     * @return language
     * @since 2.0.0
     */
    String getLanguage(Profile profile);

    /**
     * Gets the active skill for this profile.
     *
     * @param profile the profile to get the skill
     * @return skill id
     * @since 2.0.0
     */
    Skills getActiveSkill(Profile profile);

    /**
     * Updates ALL data.
     *
     * @param profile   the profile to update
     * @param freshData the new data
     * @since 2.0.0
     */
    void update(Profile profile, ListenerPlayerData freshData);

    /**
     * Updates the level data.
     *
     * @param profile   the profile to update
     * @param freshData the new data
     */
    void updateLevel(Profile profile, LevelRecord freshData);

    /**
     * Updates the list of level data.
     *
     * @param profile   the profile to update
     * @param freshData the new data
     */
    void updateLevel(Profile profile, List<LevelRecord> freshData);

    /**
     * Updates the cooldown data.
     *
     * @param profile   the profile to update
     * @param freshData the new data
     * @since 2.0.0
     */
    void updateCooldown(Profile profile, CooldownRecord freshData);

    /**
     * Updates the list of cooldowns data.
     *
     * @param profile   the profile to update
     * @param freshData the new data
     */
    void updateCooldown(Profile profile, List<CooldownRecord> freshData);

    /**
     * Updates the language.
     *
     * @param profile  the profile to update
     * @param language the new language
     */
    void updateLanguage(Profile profile, String language);

    /**
     * Updates the active skill.
     *
     * @param profile the profile to update
     * @param skillID the new skill active
     */
    void updateActiveSkill(Profile profile, Skills skillID);

    /**
     * Deletes all data.
     *
     * @param profile the profile to remove the data
     * @since 2.0.0
     */
    void remove(Profile profile);

    /**
     * Deletes a cooldown from the database.
     *
     * @param profile    the profile to remove the cooldown
     * @param removeData the cooldown to remove
     * @since 2.0.0
     */
    void removeCooldown(Profile profile, CooldownRecord removeData);

    /**
     * Deletes a list of cooldown from the database.
     *
     * @param profile    the profile to remove the cooldowns
     * @param removeData the cooldowns to remove
     * @since 2.0.0
     */
    void removeCooldown(Profile profile, List<CooldownRecord> removeData);
}
