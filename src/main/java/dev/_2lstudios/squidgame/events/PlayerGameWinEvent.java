package dev._2lstudios.squidgame.events;

import dev._2lstudios.squidgame.arena.games.ArenaGameBase;
import dev._2lstudios.squidgame.player.SquidPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 26/5/2022.
 */
public class PlayerGameWinEvent<Game extends ArenaGameBase> extends Event implements Cancellable {
    private static final HandlerList handlers;

    private final Game game;
    private final SquidPlayer winner;
    private boolean cancelled;

    static {
        handlers = new HandlerList();
    }

    public PlayerGameWinEvent(final Game game, final SquidPlayer winner) {
        super(true);
        this.game = game;
        this.winner = winner;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlersList(){
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Game getGame() {
        return game;
    }

    public SquidPlayer getWinner() {
        return winner;
    }
}
