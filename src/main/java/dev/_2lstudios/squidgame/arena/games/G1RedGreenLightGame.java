package dev._2lstudios.squidgame.arena.games;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dev._2lstudios.squidgame.arena.ArenaState;
import dev._2lstudios.squidgame.events.ArenaDispatchActionEvent;
import dev._2lstudios.squidgame.events.PlayerGameWinEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import dev._2lstudios.jelly.math.Cuboid;
import dev._2lstudios.jelly.math.Vector3;
import dev._2lstudios.jelly.utils.NumberUtils;
import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.player.SquidPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

public class G1RedGreenLightGame extends ArenaGameBase {

    private Cuboid barrier;
    private Cuboid killZone;
    private Cuboid goalZone;

    private boolean canWalk = true;
    private boolean playing = false;

    private static final PotionEffect NIGHT_VISION = new PotionEffect(
            PotionEffectType.NIGHT_VISION,
            Integer.MAX_VALUE,
            1
    )
            .withAmbient(false)
            .withParticles(false)
            .withIcon(true);

    public static final GameListener LISTENER = new GameListener() {

        @Override
        public <T extends Event> void onArenaHandleEvent(T event) {

        }

        @EventHandler
        public void onArenaDispatchEvent(ArenaDispatchActionEvent<?> event) {
            Event action = event.getAction();
            if (action instanceof PlayerGameWinEvent) {
                PlayerGameWinEvent<?> specificAction = (PlayerGameWinEvent<?>) action;
                if(specificAction.getGame() instanceof G1RedGreenLightGame)
                    onPlayerWin((PlayerGameWinEvent<G1RedGreenLightGame>) specificAction);
            }

            if(action instanceof PlayerMoveEvent){
                onPlayerMove((PlayerMoveEvent) action);
            }
        }

        private void onPlayerWin(PlayerGameWinEvent<G1RedGreenLightGame> event) {
            if (event.isCancelled())
                return;

            G1RedGreenLightGame game = event.getGame();
            SquidPlayer player = event.getWinner();
            Arena arena = player.getArena();
            if(!game.getArena().equals(arena))
                return;
            if(!(arena.getCurrentGame() instanceof G1RedGreenLightGame))
                return;

            Location position = player.getBukkitPlayer().getLocation();
            Optional.ofNullable(game.getGoalZone())
                    .ifPresent(
                            goalZone -> {
                                if (goalZone.isBetween(position)) {
                                    game.getWinners().add(player);

                                    player.sendTitle("events.game-pass.title", "events.game-pass.subtitle", 3);
                                    player.playSound(
                                            arena.getMainConfig().getSound("game-settings.sounds.player-pass-game", "LEVELUP"));
                                }
                                else
                                    event.setCancelled(true);
                            }
                    );
        }

        private void onPlayerMove(PlayerMoveEvent event){
            final SquidPlayer player = (SquidPlayer) SquidGame.getInstance().getPlayerManager().getPlayer(event.getPlayer());
            final Arena arena = player.getArena();

            if (arena == null || player.isSpectator()) {
                return;
            }

            ArenaGameBase currentGame = arena.getCurrentGame();
            /* Game 1: Handling */
            if (currentGame instanceof G1RedGreenLightGame) {
                final G1RedGreenLightGame game = (G1RedGreenLightGame) currentGame;

                if (arena.getState() == ArenaState.EXPLAIN_GAME) {
                    Optional.ofNullable(game.getBarrier())
                            .filter(spawnZone -> !spawnZone.isBetween(event.getTo()))
                            .ifPresent(
                                    spawnZone -> {
                                        event.setCancelled(true);
                                        if (spawnZone.isBetween(event.getFrom()))
                                            event.setTo(event.getFrom());
                                        else
                                            event.setTo(game.getSpawnPosition());
                                    }
                            );
                } else if (arena.getState() == ArenaState.IN_GAME) {
                    final Vector3 playerPosition = new Vector3(event.getTo().getX(), event.getTo().getY(), event.getTo().getZ());

                    if(game.getWinners().contains(player))
                        return;

                    if (game.isCanWalk())
                        Optional.ofNullable(game.getGoalZone())
                                .filter(goalZone -> goalZone.isBetween(playerPosition))
                                .ifPresent(
                                        goalZone -> new PlayerGameWinEvent<>(game, player).callEvent()
                                );
                    else
                        Optional.ofNullable(game.getKillZone())
                                .filter(killZone -> killZone.isBetween(playerPosition))
                                .ifPresent(killZone -> arena.killPlayer(player));
                }
            }
        }
    };

    public G1RedGreenLightGame(final Arena arena, final int durationTime) {
        super("§aGreen Light §7| §cRed Light", "first", durationTime, arena);
    }

    @Nullable
    public Cuboid getBarrier() {
        if (this.barrier == null) {
            this.barrier = this.getArena().getConfig().getCuboid("games.first.barrier");
        }

        return this.barrier;
    }

    @Nullable
    public Cuboid getKillZone() {
        if (this.killZone == null) {
            this.killZone = this.getArena().getConfig().getCuboid("games.first.killzone");
        }

        return this.killZone;
    }

    @Nullable
    public Cuboid getGoalZone() {
        if (this.goalZone == null) {
            this.goalZone = this.getArena().getConfig().getCuboid("games.first.goal");
        }

        return this.goalZone;
    }

    private void singDoll() {
        if (!this.playing) {
            return;
        }

        final int time = NumberUtils.randomNumber(2, 5);
        this.getArena().broadcastTitle("games.first.green-light.title", "games.first.green-light.subtitle");
        this.getArena().broadcastSound(
                this.getArena().getMainConfig().getSound("game-settings.sounds.green-light", "GHAST_MOAN"));
        this.canWalk = true;

        Bukkit.getScheduler().runTaskLater(SquidGame.getInstance(), () -> {
            this.getArena().broadcastTitle("games.first.red-light.title", "games.first.red-light.subtitle");
            this.getArena().broadcastSound(
                    this.getArena().getMainConfig().getSound("game-settings.sounds.red-light", "BLAZE_HIT"));
            Bukkit.getScheduler().runTaskLater(SquidGame.getInstance(), () -> {
                this.canWalk = false;
                final int waitTime = NumberUtils.randomNumber(2, 5);
                Bukkit.getScheduler().runTaskLater(SquidGame.getInstance(), this::singDoll, waitTime * 20L);
            }, 20);
        }, time * 20L);
    }

    @Override
    public void onStart() {
        this.playing = true;
        this.singDoll();
        this.getArena().setPvPAllowed(true);
        getArena().getAllPlayers().forEach(
                player -> NIGHT_VISION.apply(player.getBukkitPlayer())
        );
    }

    @Override
    public void onStop() {
        this.playing = false;
        getArena().getAllPlayers().forEach(
                player -> player.getBukkitPlayer().removePotionEffect(NIGHT_VISION.getType())
        );
    }

    @Override
    public GameEventsListener getEventsListener() {
        return LISTENER;
    }

    @Override
    public void onTimeUp() {
        this.getArena().setPvPAllowed(false);
        this.canWalk = false;
        this.playing = false;

        this.getArena().broadcastTitle("events.game-timeout.title", "events.game-timeout.subtitle");

        final List<SquidPlayer> death = new ArrayList<>();
        final List<SquidPlayer> alive = new ArrayList<>();

        for (final SquidPlayer squidPlayer : this.getArena().getPlayers()) {
            if (squidPlayer.isSpectator())
                continue;

            if (this.getWinners().contains(squidPlayer))
                alive.add(squidPlayer);
            else
                death.add(squidPlayer);
        }

        Bukkit.getScheduler().runTaskLater(SquidGame.getInstance(), () -> {
            for (final SquidPlayer player : death) {
                player.sendTitle("events.game-timeout-died.title", "events.game-timeout-died.subtitle", 3);
                player.playSound(
                        this.getArena().getMainConfig().getSound("game-settings.sounds.player-loss-game", "CAT_HIT"));
            }

            for (final SquidPlayer player : alive) {
                player.sendTitle("events.game-pass.title", "events.game-pass.subtitle", 3);
                player.playSound(
                        this.getArena().getMainConfig().getSound("game-settings.sounds.player-pass-game", "LEVELUP"));
            }
        }, 40L);

        Bukkit.getScheduler().runTaskLater(SquidGame.getInstance(), () -> {
            for (final SquidPlayer squidPlayer : death) {
                this.getArena().killPlayer(squidPlayer);
            }
        }, 80L);
    }

    public boolean isCanWalk() {
        return this.canWalk;
    }
}
