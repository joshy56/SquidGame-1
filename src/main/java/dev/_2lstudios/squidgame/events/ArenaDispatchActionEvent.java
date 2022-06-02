package dev._2lstudios.squidgame.events;

import com.google.common.base.Preconditions;
import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.player.SquidPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 26/5/2022.
 */
public class ArenaDispatchActionEvent<Action extends Event> extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS =  new HandlerList();
    }

    private final Action action;
    private final Arena arena;
    private final SquidPlayer player;

    public ArenaDispatchActionEvent(final @NotNull Action action, final @NotNull Arena arena, @Nullable SquidPlayer player) {
        if(Objects.isNull(action))
            throw new IllegalArgumentException("Action chosen is null...");
        if(action instanceof ArenaDispatchActionEvent)
            throw new IllegalArgumentException("Action chosen is instance of " + getClass().getSimpleName() + " possible cyclic error.");

        this.action = action;
        this.arena = Preconditions.checkNotNull(
                arena,
                "Arena chosen is null..."
        );
        this.player = player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList(){
        return HANDLERS;
    }

    @NotNull
    public Action getAction() {
        return action;
    }

    @NotNull
    public Arena getArena() {
        return arena;
    }

    @Nullable
    public SquidPlayer getPlayer() {
        return player;
    }
}
