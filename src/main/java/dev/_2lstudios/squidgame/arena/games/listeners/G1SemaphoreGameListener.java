package dev._2lstudios.squidgame.arena.games.listeners;

import dev._2lstudios.jelly.math.Vector3;
import dev._2lstudios.jelly.player.PluginPlayer;
import dev._2lstudios.jelly.utils.ObjectUtils;
import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.arena.ArenaState;
import dev._2lstudios.squidgame.arena.games.ArenaGameBase;
import dev._2lstudios.squidgame.arena.games.G1RedGreenLightGame;
import dev._2lstudios.squidgame.events.ArenaDispatchActionEvent;
import dev._2lstudios.squidgame.events.PlayerGameWinEvent;
import dev._2lstudios.squidgame.player.SquidPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Optional;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 2/6/2022.
 */
public final class G1SemaphoreGameListener extends GameListener {
    @Override
    @EventHandler
    public <T extends Event> void onArenaHandleEvent(ArenaDispatchActionEvent<T> event) {
        T action = event.getAction();
        if (action instanceof PlayerGameWinEvent)
            if (((PlayerGameWinEvent<?>) action).getGame() instanceof G1RedGreenLightGame)
                onPlayerWin((PlayerGameWinEvent<G1RedGreenLightGame>) action);


        if (action instanceof PlayerMoveEvent)
            onPlayerMove((ArenaDispatchActionEvent<PlayerMoveEvent>) event);
    }

    private void onPlayerWin(PlayerGameWinEvent<G1RedGreenLightGame> event) {
        Bukkit.getConsoleSender().sendMessage(
                "[SG]:Debug G1@GameListener@onPlayerMove()"
        );
        if (event.isCancelled())
            return;

        G1RedGreenLightGame game = event.getGame();
        SquidPlayer player = event.getWinner();
        Arena arena = player.getArena();
        if (!game.getArena().equals(arena))
            return;
        if (!(arena.getCurrentGame() instanceof G1RedGreenLightGame))
            return;
        if (game.getWinners().contains(player))
            return;


        Optional.ofNullable(game.getGoalZone())
                .ifPresent(
                        goalZone -> {
                            Location position = player.getBukkitPlayer().getLocation();
                            if (goalZone.isBetween(position)) {
                                game.getWinners().add(player);

                                player.sendTitle("events.game-pass.title", "events.game-pass.subtitle", 3);
                                player.playSound(
                                        arena.getMainConfig().getSound("game-settings.sounds.player-pass-game", "LEVELUP")
                                );
                            } else
                                event.setCancelled(true);
                        }
                );
    }

    private void onPlayerMove(ArenaDispatchActionEvent<PlayerMoveEvent> event) {
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
                            if (!(currentGame instanceof G1RedGreenLightGame))
                                return;

                            if (currentGame.getWinners().contains(player))
                                return;

                            G1RedGreenLightGame game = (G1RedGreenLightGame) currentGame;
                            if (arena.getState() == ArenaState.EXPLAIN_GAME) {
                                PlayerMoveEvent moveEvent = event.getAction();
                                Location from = moveEvent.getFrom(), to = moveEvent.getTo();
                                Optional.ofNullable(game.getBarrier())
                                        .filter(spawnZone -> !spawnZone.isBetween(to))
                                        .ifPresent(
                                                spawnZone -> {
                                                    moveEvent.setCancelled(true);
                                                    if (spawnZone.isBetween(from))
                                                        moveEvent.setTo(from);
                                                    else
                                                        moveEvent.setTo(game.getSpawnPosition());
                                                }
                                        );
                            } else if (arena.getState() == ArenaState.IN_GAME)
                                if (game.isCanWalk())
                                    Optional.ofNullable(game.getGoalZone())
                                            .filter(goalZone -> goalZone.isBetween(player.getBukkitPlayer().getLocation()))
                                            .ifPresent(goalZone -> new PlayerGameWinEvent<>(game, player).callEvent());
                                else
                                    Optional.ofNullable(game.getKillZone())
                                            .filter(killZone -> killZone.isBetween(player.getBukkitPlayer().getLocation()))
                                            .ifPresent(killZone -> arena.killPlayer(player));
                        }
                );
    }

}
