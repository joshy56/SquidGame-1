package dev._2lstudios.squidgame.listeners;

import dev._2lstudios.squidgame.arena.games.*;
import dev._2lstudios.squidgame.events.ArenaDispatchActionEvent;
import dev._2lstudios.squidgame.events.PlayerGameWinEvent;
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

import java.util.Optional;

public class PlayerMoveListener implements Listener {

    private final SquidGame plugin;

    public PlayerMoveListener(final SquidGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent e) {
        if (e.getTo() == null)
            return;
        if (e.getFrom().distance(e.getTo()) <= 0.015)
            return;

        final SquidPlayer player = (SquidPlayer) this.plugin.getPlayerManager().getPlayer(e.getPlayer());
        final Arena arena = player.getArena();

        if (arena == null || player.isSpectator())
            return;

        ArenaGameBase currentGame = arena.getCurrentGame();
        /* Game 1: Handling */
        if (currentGame instanceof G1RedGreenLightGame)
            new ArenaDispatchActionEvent<>(
                    e,
                    arena,
                    player
            ).callEvent();

        /* Game 6: Handling */
        else if (currentGame instanceof G6GlassesGame) {
            new ArenaDispatchActionEvent<>(
                    e,
                    arena,
                    player
            ).callEvent();
        }

        /* Game 7: Handling */
        else if (currentGame instanceof G7SquidGame) {
            final Location loc = e.getTo().clone();
            final String killBlock = arena.getConfig().getString("games.seventh.kill-block", "sand");
            Material block;
            try {
                block = Material.valueOf(
                        arena.getConfig().getString("games.seventh.kill-block", Material.SAND.getTranslationKey())
                );
            }catch (IllegalArgumentException ignore){
                //ignore
                block = Material.SAND;
            }
            loc.subtract(0, 1, 0);

            if (loc.getBlock() != null && loc.getBlock().getType() != null
                    && loc.getBlock().getType().toString().equalsIgnoreCase(killBlock)) {
                arena.killPlayer(player);
            }
        }
    }
}
