package dev._2lstudios.squidgame.arena.games;

import dev._2lstudios.squidgame.player.SquidPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import dev._2lstudios.jelly.config.Configuration;
import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.arena.Arena;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public abstract class ArenaGameBase {

    private final String name;
    private final String configKey;
    private final Arena arena;
    private final int gameTime;
    private final Set<SquidPlayer> winners;

    public abstract static class GameListener implements Listener {
        public abstract <T extends Event> void onArenaHandleEvent(T event);
    }

    public ArenaGameBase(final String name, final String configKey, final int gameTime, final Arena arena) {
        this.name = name;
        this.configKey = configKey;
        this.arena = arena;
        this.gameTime = gameTime;
        this.winners = new HashSet<>(
                Math.min(
                        this.arena.getMaxPlayers(),
                        16
                )
        );
    }

    public void onExplainStart() {
        final String key = "games." + this.configKey + ".tutorial";
        this.broadcastTitleAfterSeconds(3, key + ".1.title", key + ".1.subtitle");
        this.broadcastTitleAfterSeconds(6, key + ".2.title", key + ".2.subtitle");
        this.broadcastTitleAfterSeconds(9, key + ".3.title", key + ".3.subtitle");
        this.broadcastTitleAfterSeconds(12, key + ".4.title", key + ".4.subtitle");
        this.broadcastTitleAfterSeconds(15, "events.game-start.title", "events.game-start.subtitle");
    }

    public void onStart() {
    }

    public void onTimeUp() {
    }

    public void onStop() {
    }

    public Set<SquidPlayer> getWinners() {
        return winners;
    }

    public Location getSpawnPosition() {
        final Configuration config = this.arena.getConfig();
        final Location location = config.getLocation("games." + this.configKey + ".spawn", false);
        location.setWorld(this.arena.getWorld());
        return location;
    }

    public void broadcastTitleAfterSeconds(int seconds, final String title, final String subtitle) {
        Bukkit.getScheduler().runTaskLater(SquidGame.getInstance(), () -> {
            this.arena.broadcastTitle(title, subtitle);
        }, seconds * 20L);
    }

    public void broadcastMessageAfterSeconds(int seconds, final String message) {
        Bukkit.getScheduler().runTaskLater(
                SquidGame.getInstance(),
                () -> this.arena.broadcastMessage(message),
                seconds * 20L);
    }

    public int getGameTime() {
        return this.gameTime;
    }

    public Arena getArena() {
        return this.arena;
    }

    public String getName() {
        return this.name;
    }

    public abstract GameEventsListener getEventsListener();

    public interface GameEventsListener extends Listener {
    }
}
