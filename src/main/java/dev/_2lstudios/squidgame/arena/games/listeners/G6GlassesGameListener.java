package dev._2lstudios.squidgame.arena.games.listeners;

import dev._2lstudios.jelly.math.Vector3;
import dev._2lstudios.jelly.player.PluginPlayer;
import dev._2lstudios.jelly.utils.BlockUtils;
import dev._2lstudios.jelly.utils.ObjectUtils;
import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.arena.ArenaState;
import dev._2lstudios.squidgame.arena.games.ArenaGameBase;
import dev._2lstudios.squidgame.arena.games.G6GlassesGame;
import dev._2lstudios.squidgame.events.ArenaDispatchActionEvent;
import dev._2lstudios.squidgame.player.SquidPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Optional;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 2/6/2022.
 */
public final class G6GlassesGameListener extends GameListener {
    @Override
    @EventHandler
    public <T extends Event> void onArenaHandleEvent(ArenaDispatchActionEvent<T> event) {
        T action = event.getAction();
        if(action instanceof PlayerMoveEvent)
            onPlayerMove((ArenaDispatchActionEvent<PlayerMoveEvent>) event);
    }

    private void onPlayerMove(ArenaDispatchActionEvent<PlayerMoveEvent> event){
        Optional.ofNullable(SquidGame.getInstance())
                .ifPresent(
                        plugin -> {
                            PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayer(event.getAction().getPlayer());
                            if (pluginPlayer == null)
                                return;
                            SquidPlayer player = (SquidPlayer) pluginPlayer;
                            if (!ObjectUtils.checkEquals(player, event.getPlayer()))
                                return;

                            Arena arena = event.getArena();
                            if (player.isSpectator())
                                return;
                            if (!ObjectUtils.checkEquals(arena, player.getArena()))
                                return;

                            ArenaGameBase currentGame = arena.getCurrentGame();
                            if (!(currentGame instanceof G6GlassesGame))
                                return;

                            if (currentGame.getWinners().contains(player))
                                return;

                            final Block block = event.getAction().getTo().clone().subtract(0, 1, 0).getBlock();
                            if (block.getType() != Material.GLASS)
                                return;
                            if (!(((G6GlassesGame) currentGame).isFakeBlock(block)))
                                return;
                            BlockUtils.destroyBlockGroup(block);
                            arena.broadcastSound(
                                    plugin.getMainConfig().getSound(
                                            "game-settings.sounds.glass-break",
                                            "GLASS"
                                    )
                            );
                            arena.killPlayer(player);
                        }
                );
    }

}
