package dev._2lstudios.squidgame.events;

import com.google.common.base.Preconditions;
import dev._2lstudios.squidgame.arena.Arena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

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

    public ArenaDispatchActionEvent(final @NotNull Action action, final @NotNull Arena arena) {
        super(true);
        Preconditions.checkArgument(
                Objects.isNull(action),
                "Action chosen is null..."
        );
        Preconditions.checkArgument(
                action instanceof ArenaDispatchActionEvent,
                "Action chosen is instance of " + getClass().getSimpleName()
        );
        this.action = action;
        this.arena = Preconditions.checkNotNull(
                arena,
                "Arena chosen is null..."
        );
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlersList(){
        return HANDLERS;
    }

    public Action getAction() {
        return action;
    }

    public Arena getArena() {
        return arena;
    }
}
