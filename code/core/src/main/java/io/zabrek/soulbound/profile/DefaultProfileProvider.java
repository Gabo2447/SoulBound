package io.zabrek.soulbound.profile;

import io.zabrek.soulbound.api.profile.OnlineProfile;
import io.zabrek.soulbound.api.profile.Profile;
import io.zabrek.soulbound.api.profile.ProfileProvider;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Default implementation for {@link ProfileProvider}.
 */
public class DefaultProfileProvider implements ProfileProvider {

    /**
     * The server instance used to get player from UUID.
     */
    private final Server server;

    /**
     * Default profile provider constructor.
     *
     * @param server the server instance used to get player from UUID
     */
    public DefaultProfileProvider(final Server server) {
        this.server = server;
    }

    @Override
    public Profile getProfile(final OfflinePlayer offlinePlayer) {
        return new Profile() {
            @Override
            public OfflinePlayer getPlayer() {
                return offlinePlayer;
            }

            @Override
            public UUID getProfileUUID() {
                return offlinePlayer.getUniqueId();
            }

            @Override
            public String getProfileName() {
                final String name = offlinePlayer.getName();
                return name != null ? name : offlinePlayer.getUniqueId().toString();
            }

            @Override
            public Optional<OnlineProfile> getOnlineProfile() {
                final Player player = offlinePlayer.getPlayer();
                if (player == null || !player.isOnline()) {
                    return Optional.empty();
                }
                return Optional.of(getProfile(player));
            }

            @Override
            public String toString() {
                return offlinePlayer.getName() + " with profile " + this.getProfileName();
            }

            @Override
            public boolean equals(final Object obj) {
                return obj instanceof final Profile profile && getProfileUUID().equals(profile.getProfileUUID());
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(getProfileUUID());
            }
        };
    }

    @Override
    public OnlineProfile getProfile(final Player player) {
        return new OnlineProfile() {

            @Override
            public Player getPlayer() {
                return player;
            }

            @Override
            public UUID getProfileUUID() {
                return player.getUniqueId();
            }

            @Override
            public String getProfileName() {
                return player.getName();
            }

            @Override
            public Optional<OnlineProfile> getOnlineProfile() {
                if (player.isOnline()) {
                    return Optional.of(this);
                }
                return Optional.empty();
            }

            @Override
            public String toString() {
                return player.getName() + " with profile " + this.getProfileName();
            }

            @Override
            public boolean equals(final Object obj) {
                return obj instanceof final Profile profile && getProfileUUID().equals(profile.getProfileUUID());
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(getProfileUUID());
            }
        };
    }

    @Override
    public List<OnlineProfile> getOnlineProfiles() {
        return server.getOnlinePlayers().stream().map(this::getProfile).toList();
    }

    @Override
    public Profile getProfile(final UUID key) {
        final OfflinePlayer player = server.getOfflinePlayer(key);
        return getProfile(player);
    }
}
