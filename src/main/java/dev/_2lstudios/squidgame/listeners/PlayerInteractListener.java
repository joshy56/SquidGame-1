package dev._2lstudios.squidgame.listeners;

import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.arena.ArenaState;
import dev._2lstudios.squidgame.arena.games.ArenaGameBase;
import dev._2lstudios.squidgame.arena.games.G2CookieGame;
import dev._2lstudios.squidgame.events.ArenaDispatchActionEvent;
import fr.skytasul.guardianbeam.Laser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.player.PlayerWand;
import dev._2lstudios.squidgame.player.SquidPlayer;
import org.bukkit.util.RayTraceResult;

import java.util.Optional;

public class PlayerInteractListener implements Listener {

    private final SquidGame plugin;

    public PlayerInteractListener(final SquidGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent e) {
        final SquidPlayer player = (SquidPlayer) this.plugin.getPlayerManager().getPlayer(e.getPlayer());

        /* Player wand selection: Handling */
        if (player.getWand() != null && e.getItem() != null && e.getItem().getType().equals(Material.BLAZE_ROD)) {
            final PlayerWand wand = player.getWand();

            if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                wand.setFirstPoint(e.getClickedBlock().getLocation());
                e.getPlayer().sendMessage("§aSet §dfirst §apoint §7(§e" + wand.getFirstPoint().toString() + "§7)");
            } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                wand.setSecondPoint(e.getClickedBlock().getLocation());
                e.getPlayer().sendMessage("§aSet §bsecond §apoint §7(§e" + wand.getSecondPoint().toString() + "§7)");
            }

            e.setCancelled(true);
            return;
        }

        Arena arena = player.getArena();
        if (arena == null || player.isSpectator())
            return;

        ArenaGameBase currentGame = arena.getCurrentGame();

        /* Game 2: Handling */
        if (currentGame instanceof G2CookieGame)
            new ArenaDispatchActionEvent<>(e, arena, player).callEvent();
    }
}
