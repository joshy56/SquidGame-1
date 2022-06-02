package dev._2lstudios.squidgame.listeners;

import dev._2lstudios.jelly.utils.ObjectUtils;
import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.arena.games.ArenaGameBase;
import dev._2lstudios.squidgame.arena.games.G1RedGreenLightGame;
import dev._2lstudios.squidgame.events.ArenaDispatchActionEvent;
import dev._2lstudios.squidgame.events.PlayerGameWinEvent;
import dev._2lstudios.squidgame.player.SquidPlayer;
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
        SquidPlayer player = event.getWinner();
        Arena arena = player.getArena();
        if(arena == null || player.isSpectator())
            return;
        if(!ObjectUtils.checkEquals(arena.getCurrentGame(), event.getGame()))
            return;
        if(!ObjectUtils.checkEquals(arena, event.getGame().getArena()))
            return;

        new ArenaDispatchActionEvent<PlayerGameWinEvent<?>>(event, arena, event.getWinner())
                .callEvent();
    }
}
