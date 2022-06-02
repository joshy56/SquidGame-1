package dev._2lstudios.squidgame.arena.games.listeners;

import dev._2lstudios.squidgame.events.ArenaDispatchActionEvent;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 2/6/2022.
 */
public abstract class GameListener implements Listener {
    public abstract <T extends Event> void onArenaHandleEvent(ArenaDispatchActionEvent<T> event);
}
