package dev._2lstudios.squidgame.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.player.SquidPlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    private final SquidGame plugin;
    private static boolean enabled = false;

    public PlaceholderAPIHook(final SquidGame plugin) {
        this.plugin = plugin;
        enabled = true;
    }

    public String getIdentifier() {
        return plugin.getDescription().getName().toLowerCase();
    }

    public String getAuthor() {
        return plugin.getDescription().getAuthors().get(0);
    }

    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    /* Static Formatter */
    public static String formatString(final String text, final Player player) {
        if (enabled) {
            return PlaceholderAPI.setPlaceholders(player, text);
        } else {
            return text;
        }
    }

    /* Formatters */
    private String requestPlayerPlaceholder(final SquidPlayer player, final String identifier) {
        switch (identifier) {
            case "wins":
            case "deaths":
                return "0";
            default:
                return null;
        }
    }

    private String requestArenaPlaceholder(final Arena arena, final String identifier) {
        switch (identifier) {
            case "death":
                return arena.getDeathPlayer() != null ? arena.getDeathPlayer() : "None";
            case "joined":
                return arena.getJoinedPlayer() != null ? arena.getJoinedPlayer() : "None";
            case "leaved":
                return arena.getLeavedPlayer() != null ? arena.getLeavedPlayer() : "None";
            case "players":
                return arena.getPlayers().size() + "";
            case "winner":
                final SquidPlayer winner = arena.calculateWinner();
                final String name = winner != null ? winner.getBukkitPlayer().getName() : "None";
                return name;
            case "maxplayers":
                return arena.getMaxPlayers() + "";
            case "required":
                return arena.getMinPlayers() + "";
            case "time":
                return arena.getInternalTime() + "";
            case "spectators":
                return arena.getSpectators().size() + "";
            case "game":
                return arena.getCurrentGame() == null ? "None" : arena.getCurrentGame().getName();
            case "name":
                return arena.getName();
            default:
                return "";
        }
    }

    /* Handler */
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null && (identifier.startsWith("player_") || identifier.startsWith("arena_"))) {
            return "";
        }

        final SquidPlayer squidPlayer = (SquidPlayer) this.plugin.getPlayerManager().getPlayer(player);

        if (identifier.startsWith("player_")) {
            return this.requestPlayerPlaceholder(squidPlayer, identifier.split("_")[1]);
        }

        else if (identifier.startsWith("arena_")) {
            final Arena arena = squidPlayer != null ? squidPlayer.getArena() : null;
            if (arena == null) {
                return "";
            }
            return this.requestArenaPlaceholder(arena, identifier.split("_")[1]);
        }

        if(identifier.equalsIgnoreCase("prefix"))
            return ChatColor.translateAlternateColorCodes(
                    '&',
                    plugin.getMessagesConfig().getString("prefix", "")
            );

        return null;
    }
}