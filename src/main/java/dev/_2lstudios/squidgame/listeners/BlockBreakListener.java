package dev._2lstudios.squidgame.listeners;

import dev._2lstudios.squidgame.arena.ArenaState;
import dev._2lstudios.squidgame.arena.games.ArenaGameBase;
import dev._2lstudios.squidgame.arena.games.G2CookieGame;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.player.SquidPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BlockBreakListener implements Listener {
    private final SquidGame plugin;

    public BlockBreakListener(final SquidGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent e) {
        final Player bukkitPlayer = e.getPlayer();
        final SquidPlayer squidPlayer = (SquidPlayer) this.plugin.getPlayerManager().getPlayer(bukkitPlayer);
        final Arena arena = squidPlayer.getArena();

        e.setCancelled(arena != null);

        if(arena == null || squidPlayer.isSpectator())
            return;

        ArenaGameBase currentGame = arena.getCurrentGame();

        /* Game 2: Handling*/
        if(currentGame instanceof G2CookieGame){
            if(arena.getState() != ArenaState.IN_GAME)
                return;

            G2CookieGame cookieGame = (G2CookieGame) currentGame;
            Map.Entry<G2CookieGame.Shape, Integer> shape = cookieGame.getShapeAndProgressOf(squidPlayer);

            /* Chequear si el jugador ya rompio todos los bloques y marcarlo para la siguiente ronda */

            if(shape.getKey().getPoints().contains(e.getBlock().getLocation().toVector().toBlockVector())) {
                if(cookieGame.mineShapeAndCheckIfWin(squidPlayer)) {
                    squidPlayer.sendTitle("events.game-pass.title", "events.game-pass.subtitle", 3);
                    squidPlayer.playSound(
                            arena.getMainConfig().getSound("game-settings.sounds.player-pass-game", "LEVELUP"));
                } else
                    bukkitPlayer.sendBlockChange(e.getBlock().getLocation(), Material.AIR.createBlockData());
            }
            else
                arena.killPlayer(squidPlayer, true);
        }
    }
}