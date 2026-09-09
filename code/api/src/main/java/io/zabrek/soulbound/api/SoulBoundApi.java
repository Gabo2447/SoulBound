package io.zabrek.soulbound.api;

import io.zabrek.soulbound.api.bukkit.BukkitManager;
import io.zabrek.soulbound.api.logger.SoulBoundLogger;
import io.zabrek.soulbound.api.logger.SoulBoundLoggerFactory;
import io.zabrek.soulbound.api.profile.OnlineProfile;
import io.zabrek.soulbound.api.profile.Profile;
import io.zabrek.soulbound.api.profile.ProfileProvider;
import io.zabrek.soulbound.api.reload.Reloader;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

import java.util.UUID;

/**
 * The SoulBound API offers direct access to all methods related to SoulBound.
 * Accessing and modifying the current state of SoulBound will primarily be done through this api.
 * Getting an instance of this interface is done through the {@link SoulBoundApiService}.
 *
 * @since 2.0.0
 */
public interface SoulBoundApi {

    /**
     * Offers functionality to retrieve profiles for {@link Player}s, {@link OfflinePlayer}s and {@link UUID}s.
     * <br> <br>
     * Profiles are the SoulBound representation of players and their related data.
     * A profile always belongs to a player, but a player may have multiple profiles.
     *
     * @return the profile provider offering functionality to retrieve profiles
     * @see Profile
     * @see OnlineProfile
     * @since 2.0.0
     */
    @Contract(pure = true)
    ProfileProvider profiles();

    /**
     * Offers functionality to create loggers that are integrated with SoulBound.
     * <br> <br>
     * By creating a {@link SoulBoundLogger} for each class the filtering mechanism of SoulBound can be used.
     * Additionally, setting a topic sometimes helps to assign log records to a specific part of the code.
     *
     * @return the logger factory offering functionality to create loggers
     * @see SoulBoundLogger
     * @since 2.0.0
     */
    @Contract(pure = true)
    SoulBoundLoggerFactory loggerFactory();

    /**
     * Offers access to the reloader.
     * <br> <br>
     * The {@link Reloader} handles all functionality related to reloading SoulBound.
     * By hooking functions into the Reloader, you may have your own tools reloaded in sync with SoulBound.
     *
     * @return the reloader offering access to reloading
     * @since 2.0.0
     */
    @Contract(pure = true)
    Reloader reloader();

    /**
     * Offers access to shortcuts for the Bukkit API.
     *
     * @return the Bukkit manager
     * @since 2.0.0
     */
    @Contract(pure = true)
    BukkitManager bukkit();
}
