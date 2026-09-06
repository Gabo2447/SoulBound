package io.zabrek.soulbound.listeners.ui;

import io.zabrek.soulbound.api.DefaultListener;
import io.zabrek.soulbound.api.bukkit.event.PlayerLevelChangeEvent;
import io.zabrek.soulbound.api.listeners.service.ListenerService;
import io.zabrek.soulbound.api.profile.OnlineProfile;
import io.zabrek.soulbound.api.profile.Profile;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Listener responsible for handling visual feedback.
 */
public class VisualEvent extends DefaultListener {

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Creates a new instance of the listener.
     *
     * @param service the {@link ListenerService} for this listener
     * @param plugin  the plugin instance
     */
    public VisualEvent(final ListenerService service, final Plugin plugin) {
        super(service);
        this.plugin = plugin;
    }

    /**
     * Handles the level change event by displaying a temporary progress {@link BossBar}.
     *
     * @param event   the event
     * @param profile the player profile
     */
    public void onLevelChange(final PlayerLevelChangeEvent event, final Profile profile) {
        profile.getOnlineProfile().ifPresent(onlineProfile -> handlerBossBar(event, onlineProfile));
    }

    private void handlerBossBar(final PlayerLevelChangeEvent event, final OnlineProfile profile) {
        final double req = 100 * Math.pow(event.getLevel(), 1.5);
        final float progressFraction = (float) Math.clamp(event.getExperience() / req, 0.0, 1.0);

        final Component title = Component.text("Progress Skill - Level ", NamedTextColor.GREEN)
                .append(Component.text(event.getLevel(), NamedTextColor.DARK_GREEN));

        final BossBar bossBar = BossBar.bossBar(title, progressFraction, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS);

        final Player player = profile.getPlayer();
        player.showBossBar(bossBar);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.hideBossBar(bossBar);
            }
        }, 60L);
    }
}
