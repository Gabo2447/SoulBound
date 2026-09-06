package io.zabrek.soulbound.api.data;

/**
 * The player level data.
 *
 * @param skill      the skill
 * @param level      the level
 * @param experience the experience
 */
public record LevelRecord(Skills skill, int level, double experience) {

}
