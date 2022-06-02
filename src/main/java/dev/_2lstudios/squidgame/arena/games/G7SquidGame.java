package dev._2lstudios.squidgame.arena.games;

import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.arena.games.listeners.G7SquidGameListener;
import dev._2lstudios.squidgame.arena.games.listeners.GameListener;
import dev._2lstudios.squidgame.events.ArenaDispatchActionEvent;
import org.bukkit.event.Event;

public class G7SquidGame extends ArenaGameBase {
    private static final GameListener LISTENER;

    static {
        LISTENER = new G7SquidGameListener();
    }

    public G7SquidGame(final Arena arena, final int durationTime) {
        super("§dSquid§fGame", "seventh", durationTime, arena);
    }

    @Override
    public void onStart() {
        this.getArena().setPvPAllowed(true);
    }

    @Override
    public void onTimeUp() {
        this.getArena().killAllPlayers();
    }

    @Override
    public void onStop() {
        this.getArena().setPvPAllowed(false);
    }

    @Override
    public GameListener getEventsListener() {
        return LISTENER;
    }
}