package io.zabrek.soulbound.api.bukkit.event;

import io.zabrek.soulbound.api.profile.Profile;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.HandlerList;

/**
 * Fired when a circular particle and sound effect is triggered for a player.
 */
@SuppressWarnings("PMD.DataClass")
public class PlayerVisualEffectEvent extends ProfileEvent {

    /**
     * HandlerList of this event.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * The origin location of the effect.
     */
    private final Location location;

    /**
     * The radius of the effect in blocks.
     */
    private final double radius;

    /**
     * The total number of particles to spawn.
     */
    private final int totalParticles;

    /**
     * The sound played alongside the effect.
     */
    private final Sound sound;

    /**
     * The particle type displayed in the effect.
     */
    private final Particle particle;

    /**
     * Constructs the visual effect event.
     *
     * @param who target player profile
     * @param location origin point of the effect
     * @param radiusEffect effect radius in blocks
     * @param totalParticles particle count to spawn
     * @param sound sound to play alongside the effect
     * @param particle particle type to display
     */
    public PlayerVisualEffectEvent(final Profile who, final Location location, final double radiusEffect, final int totalParticles,
                                   final Sound sound, final Particle particle) {
        super(who);
        this.location = location;
        this.radius = radiusEffect;
        this.totalParticles = totalParticles;
        this.sound = sound;
        this.particle = particle;
    }

    /**
     * Gets the HandlerList of this event.
     *
     * @return the HandlerList
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * Gets the origin location of the effect.
     *
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the radius of the visual effect in blocks.
     *
     * @return the radius
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Gets the total number of particles to spawn.
     *
     * @return the total particles
     */
    public int getTotalParticles() {
        return totalParticles;
    }

    /**
     * Gets the sound played alongside the effect.
     *
     * @return the sound
     */
    public Sound getSound() {
        return sound;
    }

    /**
     * Gets the particle type displayed in the effect.
     *
     * @return the particle type
     */
    public Particle getParticle() {
        return particle;
    }
}
