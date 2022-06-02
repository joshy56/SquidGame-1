package dev._2lstudios.squidgame.listeners;

import dev._2lstudios.squidgame.arena.ArenaState;
import dev._2lstudios.squidgame.arena.games.ArenaGameBase;
import dev._2lstudios.squidgame.arena.games.G2CookieGame;
import dev._2lstudios.squidgame.events.ArenaDispatchActionEvent;
import org.bukkit.Bukkit;
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
            new ArenaDispatchActionEvent<>(e, arena, squidPlayer).callEvent();
        }
    }
}