package dev._2lstudios.squidgame.listeners;

import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.arena.ArenaState;
import dev._2lstudios.squidgame.player.SquidPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class EntityDamageListener implements Listener {

    private final SquidGame plugin;

    public EntityDamageListener(final SquidGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent e) {
        final Entity entity = e.getEntity();
        if (!(entity instanceof Player))
            return;

        final SquidPlayer player = (SquidPlayer) plugin.getPlayerManager().getPlayer((Player) entity);
        if (player == null)
            return;

        final Arena arena = player.getArena();
        if (arena == null)
            return;

        DamageCause cause = e.getCause();
        e.setCancelled(
                (cause == DamageCause.FALL || cause == DamageCause.ENTITY_ATTACK || cause == DamageCause.ENTITY_EXPLOSION)
                        && (arena.getState() != ArenaState.IN_GAME || !arena.isPvPAllowed())
        );


        if (!e.isCancelled() && player.getBukkitPlayer().getHealth() - e.getDamage() <= 0
                && !player.isSpectator()) {
            arena.killPlayer(player);
            e.setCancelled(true);
        }
    }
}
