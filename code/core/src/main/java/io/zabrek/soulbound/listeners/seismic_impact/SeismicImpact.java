package io.zabrek.soulbound.listeners.seismic_impact;

import io.zabrek.soulbound.api.DefaultListener;
import io.zabrek.soulbound.api.bukkit.event.PlayerVisualEffectEvent;
import io.zabrek.soulbound.api.data.LevelRecord;
import io.zabrek.soulbound.api.data.Skills;
import io.zabrek.soulbound.api.listeners.service.ListenerDataService;
import io.zabrek.soulbound.api.listeners.service.ListenerService;
import io.zabrek.soulbound.api.profile.OnlineProfile;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

/**
 * The player must deal damage.
 */
public class SeismicImpact extends DefaultListener {

    /**
     * The skill for this listener.
     */
    private static final Skills SKILL_LISTENER = Skills.SEISMIC_IMPACT;

    /**
     * The sound effect played when the skill visual effects are triggered.
     */
    private static final Sound EFFECT_SOUND = Sound.BLOCK_ANVIL_PLACE;

    /**
     * The particle effect displayed when the skill visual effects are triggered.
     */
    private static final Particle EFFECT_PARTICLE = Particle.CLOUD;

    /**
     * Minimum squared magnitude to prevent division by zero.
     */
    private static final double MIN_VECTOR_MAGNITUDE_SQUARED = 0.0001d;

    /**
     * Creates a new instance of the listener.
     *
     * @param service the {@link ListenerService} for this listener.
     */
    public SeismicImpact(final ListenerService service) {
        super(service);
    }

    /**
     * Handles when a player deals damage.
     *
     * @param event the Bukkit entity damage event
     * @param onlineProfile the profile of the player
     */
        public void onDamage(final EntityDamageEvent event, final OnlineProfile onlineProfile) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        final ListenerDataService dataService = service.getData();
        if (dataService.getActiveSkill(onlineProfile) != SKILL_LISTENER) {
            return;
        }

        final LevelRecord levelRecord = findSkillLevel(dataService, onlineProfile);
        if (levelRecord == null) {
            return;
        }

        event.setCancelled(true);

        final Player player = onlineProfile.getPlayer();
        final Location center = player.getLocation();
        final int level = levelRecord.level();

        final double radius = 1.0d * level;
        final double damage = 1.5d * level;

        triggerVisualEffect(onlineProfile, center, radius);
        applyShockwave(player, center, radius, damage);
    }

    private @Nullable LevelRecord findSkillLevel(final ListenerDataService dataService, final OnlineProfile onlineProfile) {
        for (final LevelRecord record : dataService.getLevels(onlineProfile)) {
            if (record.skill() == SKILL_LISTENER) {
                return record;
            }
        }
        return null;
    }

    private void triggerVisualEffect(final OnlineProfile onlineProfile, final Location center, final double radius) {
        final Location particleLocation = center.clone().add(0, 0.1, 0);
        final double radiusEffect = Math.clamp(radius / 5.0d, 1.0d, 7.5d);
        final int totalParticles = 25;

        new PlayerVisualEffectEvent(onlineProfile, particleLocation, radiusEffect, totalParticles, EFFECT_SOUND, EFFECT_PARTICLE)
                .callEvent();
    }

    private void applyShockwave(final Player attacker, final Location center, final double radius, final double damage) {
        final double centerX = center.getX();
        final double centerY = center.getY();
        final double centerZ = center.getZ();
        final double radiusSquared = radius * radius;

        for (final Entity entity : attacker.getNearbyEntities(radius, radius, radius)) {
            if (!(entity instanceof final LivingEntity victim)
                    || victim.equals(attacker)
                    || victim.isDead()) {
                continue;
            }

            final Location victimLocation = victim.getLocation();
            final double diffX = victimLocation.getX() - centerX;
            final double diffY = victimLocation.getY() - centerY;
            final double diffZ = victimLocation.getZ() - centerZ;

            final double distSquared = diffX * diffX + diffY * diffY + diffZ * diffZ;
            if (distSquared > radiusSquared) {
                continue;
            }

            victim.damage(damage, attacker);
            victim.setVelocity(calculateKnockbackVector(diffX, diffZ));
        }
    }

    private Vector calculateKnockbackVector(final double diffX, final double diffZ) {
        double pushX = diffX;
        double pushZ = diffZ;
        double flatDistSq = pushX * pushX + pushZ * pushZ;

        if (flatDistSq < MIN_VECTOR_MAGNITUDE_SQUARED) {
            final ThreadLocalRandom random = ThreadLocalRandom.current();
            pushX = random.nextDouble() - 0.5d;
            pushZ = random.nextDouble() - 0.5d;
            flatDistSq = pushX * pushX + pushZ * pushZ;
        }

        final double invLen = 1.0d / Math.sqrt(flatDistSq);
        return new Vector(pushX * invLen * 1.2d, 0.5d, pushZ * invLen * 1.2d);
    }
}
