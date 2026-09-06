package io.zabrek.soulbound.listeners.death;

import io.zabrek.soulbound.api.DefaultListener;
import io.zabrek.soulbound.api.bukkit.event.PlayerLevelChangeEvent;
import io.zabrek.soulbound.api.data.LevelRecord;
import io.zabrek.soulbound.api.data.Skills;
import io.zabrek.soulbound.api.listeners.service.ListenerDataService;
import io.zabrek.soulbound.api.listeners.service.ListenerService;
import io.zabrek.soulbound.api.profile.OnlineProfile;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

/**
 * The listener to watch the death event entity.
 */
public class EntityDeath extends DefaultListener {

    /**
     * Creates a new instance of the listener.
     *
     * @param service the {@link ListenerService} for this listener.
     */
    public EntityDeath(final ListenerService service) {
        super(service);
    }

    /**
     * Calculates the amount of experience needed to reach a specific level.
     * <p>
     * Uses an exponential progression curve based on the formula:
     * {@code 100.0 * level^1.5}.
     *
     * @param level The level for which you want to calculate the required experience.
     * @return The total amount of experience required as a decimal value.
     */
    public static double getRequiredExperience(final int level) {
        return 100.0 * Math.pow(level, 1.5);
    }

    /**
     * The handler to watch if the player kills a mob.
     *
     * @param event   the event
     * @param profile the profile
     */
    public void onDeath(final EntityDeathEvent event, final OnlineProfile profile) {
        final double droppedExp = event.getDroppedExp();
        if (droppedExp <= 0) {
            return;
        }

        final double expToAdd = calcExpToAdd(droppedExp, event);
        if (expToAdd <= 0) {
            return;
        }

        final ListenerDataService dataService = service.getData();
        final List<LevelRecord> data = dataService.getLevels(profile);
        final Skills skill = dataService.getActiveSkill(profile);

        for (final LevelRecord level : data) {
            if (level.skill() != skill) {
                continue;
            }

            final LevelRecord levelRecord = calcLevel(level, expToAdd);
            new PlayerLevelChangeEvent(profile, false, levelRecord, expToAdd).callEvent();

            dataService.updateLevel(profile, levelRecord);
            break;
        }
    }

    private LevelRecord calcLevel(final LevelRecord record, final double expToAdd) {
        double experience = expToAdd + record.experience();
        int level = record.level();

        while (true) {
            final double requiredForNext = getRequiredExperience(level);
            if (experience < requiredForNext) {
                break;
            }
            experience -= requiredForNext;
            level++;
        }

        return new LevelRecord(record.skill(), level, experience);
    }

    private double calcExpToAdd(final double xp, final EntityDeathEvent event) {
        final double multiplier = MobComplexity.getComplexityOf(event.getEntityType());
        return xp * (1.0 + multiplier);
    }
}
