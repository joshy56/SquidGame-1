package dev._2lstudios.squidgame.arena.games;

import dev._2lstudios.jelly.math.Cuboid;
import dev._2lstudios.jelly.math.Vector3;
import dev._2lstudios.jelly.utils.NumberUtils;
import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.arena.ArenaState;
import dev._2lstudios.squidgame.arena.games.listeners.G1SemaphoreGameListener;
import dev._2lstudios.squidgame.arena.games.listeners.GameListener;
import dev._2lstudios.squidgame.events.ArenaDispatchActionEvent;
import dev._2lstudios.squidgame.events.PlayerGameWinEvent;
import dev._2lstudios.squidgame.player.SquidPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class G1RedGreenLightGame extends ArenaGameBase {

    private Cuboid barrier;
    private Cuboid killZone;
    private Cuboid goalZone;

    private boolean canWalk = true;
    private boolean playing = false;

    private static final PotionEffect NIGHT_VISION;
    private static final GameListener LISTENER;

    static {
        NIGHT_VISION = new PotionEffect(
                PotionEffectType.NIGHT_VISION,
                Integer.MAX_VALUE,
                1
        )
                .withAmbient(false)
                .withParticles(false)
                .withIcon(true);

        LISTENER = new G1SemaphoreGameListener();
    }

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
    public GameListener getEventsListener() {
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
