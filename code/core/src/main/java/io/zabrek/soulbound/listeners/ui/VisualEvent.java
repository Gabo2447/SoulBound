package io.zabrek.soulbound.listeners.ui;

import io.zabrek.soulbound.api.DefaultListener;
import io.zabrek.soulbound.api.bukkit.event.PlayerLevelChangeEvent;
import io.zabrek.soulbound.api.bukkit.event.PlayerVisualEffectEvent;
import io.zabrek.soulbound.api.listeners.service.ListenerService;
import io.zabrek.soulbound.api.profile.OnlineProfile;
import io.zabrek.soulbound.api.profile.Profile;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.ThreadLocalRandom;

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
     * Applies visual and auditory effects for a player visual effect event if the profile is online.
     *
     * @param event the visual effect event containing location, particle, and sound data
     * @param profile the target player profile
     */
    public void applyParticleEffect(final PlayerVisualEffectEvent event, final Profile profile) {
        profile.getOnlineProfile().ifPresent(onlineProfile -> spawnParticle(event));
    }

    private void spawnParticle(final PlayerVisualEffectEvent event) {
        final Location location = event.getLocation();
        final World world = location.getWorld();

        if (world == null) {
            return;
        }

        final float randomPitch = 0.8f + (ThreadLocalRandom.current().nextFloat() * 0.4f);
        world.playSound(location, event.getSound(), 1.0f, randomPitch);

        final int totalParticles = event.getTotalParticles();
        if (totalParticles <= 0) {
            return;
        }

        final double radius = event.getRadius();
        final double increment = 2.0d * Math.PI / totalParticles;
        final double originX = location.getX();
        final double originY = location.getY() + 0.1d;
        final double originZ = location.getZ();
        final Particle particle = event.getParticle();

        for (int i = 0; i < event.getTotalParticles(); i++) {
            final double angle = i * increment;
            final double particleX = originX + radius * Math.cos(angle);
            final double particleZ = originZ + radius * Math.sin(angle);

            world.spawnParticle(particle, particleX, originY, particleZ, 1, 0, 0, 0, 0);
        }
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
