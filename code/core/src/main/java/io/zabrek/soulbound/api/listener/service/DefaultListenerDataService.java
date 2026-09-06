package io.zabrek.soulbound.api.listener.service;

import io.zabrek.soulbound.api.data.CooldownRecord;
import io.zabrek.soulbound.api.data.LevelRecord;
import io.zabrek.soulbound.api.data.Skills;
import io.zabrek.soulbound.api.listeners.service.ListenerDataService;
import io.zabrek.soulbound.api.listeners.service.ListenerPlayerData;
import io.zabrek.soulbound.api.profile.Profile;
import io.zabrek.soulbound.data.PlayerDataStorage;
import io.zabrek.soulbound.database.data.PlayerData;

import java.util.List;

/**
 * Default implementation of the {@link ListenerDataService} interface.
 * This managing and retrieving persistent data associated with player listeners.
 *
 * @since 2.0.0
 */
@SuppressWarnings("PMD.TooManyMethods")
public class DefaultListenerDataService implements ListenerDataService {

    /**
     * The player data storage.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * Creates a new ListenerDataService.
     *
     * @param playerDataStorage the player data storage
     */
    public DefaultListenerDataService(final PlayerDataStorage playerDataStorage) {
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public ListenerPlayerData get(final Profile profile) {
        final PlayerData playerData = playerDataStorage.get(profile);
        return new DefaultListenerPlayerData(playerData.getLevels(), playerData.getCooldowns());
    }

    @Override
    public List<CooldownRecord> getCooldowns(final Profile profile) {
        final PlayerData playerData = playerDataStorage.get(profile);
        return playerData.getCooldowns();
    }

    @Override
    public List<LevelRecord> getLevels(final Profile profile) {
        final PlayerData playerData = playerDataStorage.get(profile);
        return playerData.getLevels();
    }

    @Override
    public String getLanguage(final Profile profile) {
        final PlayerData playerData = playerDataStorage.get(profile);
        return playerData.getLanguage().orElse("default");
    }

    @Override
    public Skills getActiveSkill(final Profile profile) {
        final PlayerData playerData = playerDataStorage.get(profile);
        return playerData.getSkill();
    }

    @Override
    public void update(final Profile profile, final ListenerPlayerData freshData) {
        final PlayerData playerData = playerDataStorage.get(profile);
        playerData.setCooldowns(freshData.getCooldown());
        playerData.setLevels(freshData.getLevel());
    }

    @Override
    public void updateLevel(final Profile profile, final LevelRecord freshData) {
        final PlayerData playerData = playerDataStorage.get(profile);
        playerData.updateLevel(freshData);
    }

    @Override
    public void updateLevel(final Profile profile, final List<LevelRecord> freshData) {
        final PlayerData playerData = playerDataStorage.get(profile);
        playerData.setLevels(freshData);
    }

    @Override
    public void updateCooldown(final Profile profile, final CooldownRecord freshData) {
        final PlayerData playerData = playerDataStorage.get(profile);
        playerData.addCooldown(freshData);
    }

    @Override
    public void updateCooldown(final Profile profile, final List<CooldownRecord> freshData) {
        final PlayerData playerData = playerDataStorage.get(profile);
        playerData.setCooldowns(freshData);
    }

    @Override
    public void updateLanguage(final Profile profile, final String language) {
        final PlayerData playerData = playerDataStorage.get(profile);
        playerData.setLanguage(language);
    }

    @Override
    public void updateActiveSkill(final Profile profile, final Skills skillID) {
        final PlayerData playerData = playerDataStorage.get(profile);
        playerData.setSkill(skillID);
    }

    @Override
    public void remove(final Profile profile) {
        final PlayerData playerData = playerDataStorage.get(profile);
        playerData.purgePlayer();
    }

    @Override
    public void removeCooldown(final Profile profile, final CooldownRecord removeData) {
        final PlayerData playerData = playerDataStorage.get(profile);
        playerData.removeCooldown(removeData);
    }

    @Override
    public void removeCooldown(final Profile profile, final List<CooldownRecord> removeData) {
        final PlayerData playerData = playerDataStorage.get(profile);
        for (final CooldownRecord record : removeData) {
            playerData.removeCooldown(record);
        }
    }
}
