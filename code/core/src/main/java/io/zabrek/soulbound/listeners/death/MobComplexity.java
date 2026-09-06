package io.zabrek.soulbound.listeners.death;

import org.bukkit.entity.EntityType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Calculates the difficulty to kill mobs.
 */
public enum MobComplexity {

    // --- Mobs Passives / Easy (0.0 - 0.5) ---
    /**
     * The chicken. Neutrals.
     */
    CHICKEN(EntityType.CHICKEN, 0.05),
    /**
     * The rabbit. Neutrals.
     */
    RABBIT(EntityType.RABBIT, 0.08),
    /**
     * The sheep. Neutrals.
     */
    SHEEP(EntityType.SHEEP, 0.1),
    /**
     * The normal pig. Neutrals.
     */
    PIG(EntityType.PIG, 0.1),
    /**
     * The cow. Neutrals.
     */
    COW(EntityType.COW, 0.1),
    /**
     * The villager.
     */
    VILLAGER(EntityType.VILLAGER, 0.2),

    // --- Mobs Medium (0.6 - 2.5) ---
    /**
     * The spider. Medium.
     */
    SPIDER(EntityType.SPIDER, 0.8),
    /**
     * The zombie. Medium.
     */
    ZOMBIE(EntityType.ZOMBIE, 1.0),
    /**
     * The skeleton. Medium.
     */
    SKELETON(EntityType.SKELETON, 1.2),
    /**
     * The creeper. Medium.
     */
    CREEPER(EntityType.CREEPER, 1.5),
    /**
     * The iron golem. Medium.
     */
    IRON_GOLEM(EntityType.IRON_GOLEM, 3.5),

    // --- Mobs Advanced / Bosses ---
    /**
     * The warden. Advanced.
     */
    WARDEN(EntityType.WARDEN, 12.5),
    /**
     * The whiter. Advanced.
     */
    WITHER(EntityType.WITHER, 15.0),
    /**
     * The ender dragon. Advanced.
     */
    ENDER_DRAGON(EntityType.ENDER_DRAGON, 25.5);

    /**
     * Internal map that associates each entity type with its complexity value.
     */
    private static final Map<EntityType, Double> COMPLEXITY_MAP = new EnumMap<>(EntityType.class);

    static {
        for (final MobComplexity mob : values()) {
            COMPLEXITY_MAP.put(mob.entityType, mob.complexity);
        }
    }

    /**
     * The entity.
     */
    private final EntityType entityType;

    /**
     * The complexity to kill.
     */
    private final double complexity;

    MobComplexity(final EntityType entityType, final double complexity) {
        this.entityType = entityType;
        this.complexity = complexity;
    }

    /**
     * Static method to get the complexity for any mob.
     *
     * @param type the mob
     * @return the complexity
     */
    public static double getComplexityOf(final EntityType type) {
        return COMPLEXITY_MAP.getOrDefault(type, 1.0);
    }

    /**
     * Gets the entity type.
     *
     * @return the entity type
     */
    public EntityType getEntityType() {
        return entityType;
    }

    /**
     * Gets the complexity.
     *
     * @return the complexity
     */
    public double getComplexity() {
        return complexity;
    }
}
