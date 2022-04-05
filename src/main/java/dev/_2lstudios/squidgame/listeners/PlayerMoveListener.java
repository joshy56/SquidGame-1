package dev._2lstudios.squidgame.listeners;

import dev._2lstudios.squidgame.arena.games.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import dev._2lstudios.jelly.math.Vector3;
import dev._2lstudios.jelly.utils.BlockUtils;
import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.arena.ArenaState;
import dev._2lstudios.squidgame.player.SquidPlayer;

public class PlayerMoveListener implements Listener {

    private final SquidGame plugin;

    public PlayerMoveListener(final SquidGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent e) {
        if(e.getTo() == null)
            return;
        if (e.getFrom().distance(e.getTo()) <= 0.015)
            return;

        final SquidPlayer player = (SquidPlayer) this.plugin.getPlayerManager().getPlayer(e.getPlayer());
        final Arena arena = player.getArena();

        if (arena == null || player.isSpectator()) {
            return;
        }

        ArenaGameBase currentGame = arena.getCurrentGame();
        /* Game 1: Handling */
        if (currentGame instanceof G1RedGreenLightGame) {
            final G1RedGreenLightGame game = (G1RedGreenLightGame) currentGame;

            if (arena.getState() == ArenaState.EXPLAIN_GAME) {
                if (game.getBarrier().isBetween(e.getTo())) {
                    e.setCancelled(true);
                    e.setTo(e.getFrom());
                }
            }

            else if (arena.getState() == ArenaState.IN_GAME) {
                if (!game.isCanWalk()) {
                    final Vector3 playerPosition = new Vector3(e.getTo().getX(), e.getTo().getY(), e.getTo().getZ());
                    if (game.getKillZone().isBetween(playerPosition)) {
                        arena.killPlayer(player);
                    }
                }
            }
        }

        /* Game 6: Handling */
        else if (currentGame instanceof G6GlassesGame) {
            final Location loc = e.getTo().clone().subtract(0, 1, 0);
            final Block block = loc.getBlock();

            if (block != null && block.getType() == Material.GLASS) {
                final G6GlassesGame game = (G6GlassesGame) currentGame;

                if (game.isFakeBlock(loc.getBlock())) {
                    BlockUtils.destroyBlockGroup(loc.getBlock());
                    arena.broadcastSound(
                            this.plugin.getMainConfig().getSound("game-settings.sounds.glass-break", "GLASS"));
                }
            }
        }

        /* Game 7: Handling */
        else if (currentGame instanceof G7SquidGame) {
            final Location loc = e.getTo().clone();
            final String killBlock = arena.getConfig().getString("games.seventh.kill-block", "sand");

            loc.subtract(0, 1, 0);

            if (loc.getBlock() != null && loc.getBlock().getType() != null
                    && loc.getBlock().getType().toString().equalsIgnoreCase(killBlock)) {
                arena.killPlayer(player);
            }
        }
    }
}
