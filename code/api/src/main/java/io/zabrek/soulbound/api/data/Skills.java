package io.zabrek.soulbound.api.data;

/**
 * The list of available skills.
 */
public enum Skills {

    /**
     * Seismic Impact skill.
     */
    SEISMIC_IMPACT("Seismic Impact", "seismic_impact"),

    /**
     * Streak of Good Luck skill.
     */
    STREAK_OF_GOOD_LUCK("Streak of Good Luck", "streak_of_good_luck"),

    /**
     * Piercing Arrow skill.
     */
    PIERCING_ARROW("Piercing Arrow", "piercing_arrow"),

    /**
     * Adrenaline in the Blood skill.
     */
    ADRENALINE_IN_THE_BLOOD("Adrenaline in the Blood", "adrenaline_in_the_blood"),

    /**
     * Obsidian Skin skill.
     */
    OBSIDIAN_SKIN("Obsidian Skin", "obsidian_skin");

    /**
     * Default skill.
     */
    public static final Skills DEFAULT_SKILL = SEISMIC_IMPACT;

    /**
     * The name to display.
     */
    private final String displayName;

    /**
     * The identifier.
     */
    private final String identifier;

    Skills(final String displayName, final String identifier) {
        this.displayName = displayName;
        this.identifier = identifier;
    }

    /**
     * Gets a skill by its string identifier.
     *
     * @param identifier the unique string identifier of the skill
     * @return a containing the matching skill, or Seismic Impact (default)
     */
    public static Skills fromId(final String identifier) {
        if (identifier.isBlank()) {
            return DEFAULT_SKILL;
        }

        for (final Skills skill : values()) {
            if (skill.identifier.equalsIgnoreCase(identifier)) {
                return skill;
            }
        }

        return DEFAULT_SKILL;
    }

    /**
     * Gets the id for this skill.
     *
     * @return the id
     */
    public String getId() {
        return identifier;
    }

    /**
     * Gets the display name for the player.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
}
