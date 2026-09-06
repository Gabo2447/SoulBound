package io.zabrek.soulbound.api.data;

/**
 * The cooldown data.
 *
 * @param skill the skill identifier
 * @param time  the expired time.
 */
public record CooldownRecord(Skills skill, String time) {

}
