package dev._2lstudios.squidgame.listeners;

import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.arena.games.ArenaGameBase;
import dev._2lstudios.squidgame.arena.games.G1RedGreenLightGame;
import dev._2lstudios.squidgame.events.ArenaDispatchActionEvent;
import dev._2lstudios.squidgame.events.PlayerGameWinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 26/5/2022.
 */
public class PlayerGameWinListener implements Listener {
    private final SquidGame plugin;

    public PlayerGameWinListener(SquidGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerWin(PlayerGameWinEvent<?> event){
        new ArenaDispatchActionEvent<PlayerGameWinEvent<?>>(event, event.getWinner().getArena())
                .callEvent();
    }
}
