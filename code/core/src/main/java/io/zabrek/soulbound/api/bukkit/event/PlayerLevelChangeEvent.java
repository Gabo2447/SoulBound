package io.zabrek.soulbound.api.bukkit.event;

import io.zabrek.soulbound.api.data.LevelRecord;
import io.zabrek.soulbound.api.data.Skills;
import io.zabrek.soulbound.api.profile.Profile;
import org.bukkit.event.HandlerList;

/**
 * Fires when a profile's skill level change.
 */
public class PlayerLevelChangeEvent extends ProfileEvent {

    /**
     * HandlerList of this event.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * ID of the changed skill.
     */
    private final Skills skillID;

    /**
     * The added experience.
     */
    private final double experience;

    /**
     * The final level.
     */
    private final int level;

    /**
     * Creates a new PlayerLevelChangeEvent.
     *
     * @param who        the {@link Profile} whose experience has added
     * @param isAsync    whether the event is async
     * @param record     the data changed
     * @param experience the experience added
     */
    public PlayerLevelChangeEvent(final Profile who, final boolean isAsync, final LevelRecord record, final double experience) {
        super(who, isAsync);
        this.skillID = record.skill();
        this.level = record.level();
        this.experience = experience;
    }

    /**
     * Get the HandlerList of this event.
     *
     * @return the HandlerList
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    /**
     * Get the added experience.
     *
     * @return the experience which was added
     */
    public double getExperience() {
        return experience;
    }

    /**
     * Get the skill ID which will be changed.
     *
     * @return the skill ID
     */
    public Skills getSkillID() {
        return skillID;
    }

    /**
     * Get the final level.
     *
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
