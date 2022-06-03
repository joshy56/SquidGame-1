package dev._2lstudios.squidgame.arena.games;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.math.IntMath;
import dev._2lstudios.jelly.config.Configuration;
import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.arena.ArenaState;
import dev._2lstudios.squidgame.arena.games.listeners.G4RopePullingGameListener;
import dev._2lstudios.squidgame.arena.games.listeners.GameListener;
import dev._2lstudios.squidgame.player.SquidPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.RoundingMode;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 29/3/2022.
 */
public class G4RopePullingGame extends ArenaGameBase {
    private final AtomicReference<Pointer> actualPointer;
    private List<Pointer> pointers;
    private AtomicInteger counter;
    private boolean returning;
    private Integer timeBetweenFrame;
    private static final GameListener LISTENER;

    static {
        LISTENER = new G4RopePullingGameListener();
    }

    public G4RopePullingGame(Arena arena, int gameTime) {
        super("§3Tug o' war", "four", gameTime, arena);
        getPointers();
        actualPointer = new AtomicReference<>(Pointer.empty());
        counter = new AtomicInteger();
    }

    @Override
    public Location getSpawnPosition() {
        final Configuration config = this.getArena().getConfig();
        final Location location = config.getLocation("arena.waiting_room", false);
        location.setWorld(this.getArena().getWorld());
        return location;
    }

    @Override
    public GameListener getEventsListener() {
        return LISTENER;
    }

    public Title getTitle() {
        Component title = getPointers()
                .stream()
                .map(Pointer::getRepresentation)
                .collect(Component.toComponent());
        Component subtitle = getPointers()
                .stream()
                .map(pointer -> Component.text(pointer.getWeight(), (pointer.isHovered()) ? pointer.getRepresentation().style().decorate(TextDecoration.BOLD) : pointer.getRepresentation().style()))
                .collect(Component.toComponent());
        return Title.title(
                title,
                subtitle,
                Title.Times.of(
                        Duration.ZERO, getTimeBetweenFrame().plusMillis(50), Duration.ZERO
                )
        );
    }

    public List<Pointer> getPointers() {
        if (pointers == null) {
            //Take list of pointers from the config
            //Init iterator using iterator() method from the list get previously
            pointers = Lists.newArrayList(
                    new Pointer(
                            Component.text('|', NamedTextColor.YELLOW),
                            Component.text('|', Style.style(NamedTextColor.YELLOW, TextDecoration.BOLD)),
                            1
                    ),
                    new Pointer(
                            Component.text('|', NamedTextColor.YELLOW),
                            Component.text('|', Style.style(NamedTextColor.YELLOW, TextDecoration.BOLD)),
                            1
                    ),
                    new Pointer(
                            Component.text('|', NamedTextColor.GOLD),
                            Component.text('|', Style.style(NamedTextColor.GOLD, TextDecoration.BOLD)),
                            2
                    ),
                    new Pointer(
                            Component.text('|', NamedTextColor.RED),
                            Component.text('|', Style.style(NamedTextColor.RED, TextDecoration.BOLD)),
                            3
                    ),
                    new Pointer(
                            Component.text('|', NamedTextColor.GOLD),
                            Component.text('|', Style.style(NamedTextColor.GOLD, TextDecoration.BOLD)),
                            2
                    ),
                    new Pointer(
                            Component.text('|', NamedTextColor.YELLOW),
                            Component.text('|', Style.style(NamedTextColor.YELLOW, TextDecoration.BOLD)),
                            1
                    ),
                    new Pointer(
                            Component.text('|', NamedTextColor.GOLD),
                            Component.text('|', Style.style(NamedTextColor.GOLD, TextDecoration.BOLD)),
                            2
                    ),
                    new Pointer(
                            Component.text('|', NamedTextColor.RED),
                            Component.text('|', Style.style(NamedTextColor.RED, TextDecoration.BOLD)),
                            3
                    ),
                    new Pointer(
                            Component.text('|', NamedTextColor.GOLD),
                            Component.text('|', Style.style(NamedTextColor.GOLD, TextDecoration.BOLD)),
                            2
                    ),
                    new Pointer(
                            Component.text('|', NamedTextColor.YELLOW),
                            Component.text('|', Style.style(NamedTextColor.YELLOW, TextDecoration.BOLD)),
                            1
                    ),
                    new Pointer(
                            Component.text('|', NamedTextColor.YELLOW),
                            Component.text('|', Style.style(NamedTextColor.YELLOW, TextDecoration.BOLD)),
                            1
                    )
            );
        }
        return pointers;
    }

    public Duration getTimeBetweenFrame() {
        if (timeBetweenFrame == null) {
            timeBetweenFrame = Optional.ofNullable(SquidGame.getInstance())
                    .map(
                            plugin -> Math.max(
                                    plugin.getMainConfig().getInt("game-settings.game-4.title-update-delay", 50),
                                    50
                            )
                    )
                    .orElse(50);
        }
        return Duration.ofMillis(timeBetweenFrame);
    }

    public final Pointer getActualPointer() {
        return actualPointer.get();
    }

    @Nullable
    public Team getTeamOfPlayer(SquidPlayer player) {
        if (Team.BLUE.getPlayers().contains(player.getBukkitPlayer().getUniqueId()))
            return Team.BLUE;
        if (Team.RED.getPlayers().contains(player.getBukkitPlayer().getUniqueId()))
            return Team.RED;
        return null;
    }

    public void dispenseTitleAnimation() {
        Bukkit.getScheduler().runTaskLater(
                JavaPlugin.getPlugin(SquidGame.class),
                task -> {
                    if (getArena().getState() == ArenaState.FINISHING_GAME)
                        return;
                    Pointer pointer = actualPointer.get();
                    Title title = getTitle();

                    getArena().getPlayers().forEach(
                            squidPlayer -> squidPlayer.getBukkitPlayer().showTitle(title)
                    );
                    pointer = pointers.get(counter.get());
                    pointers.set(
                            counter.getAndUpdate(
                                    operand -> {
                                        if (operand == pointers.size() - 1)
                                            returning = true;
                                        if (operand == 0)
                                            returning = false;
                                        return (returning) ? --operand : ++operand;
                                    }
                            ),
                            pointer.hover(false)
                    );
                    pointer = pointers.get(counter.get());
                    pointers.set(counter.get(), pointer.hover(true));
                    actualPointer.set(pointer);
                    dispenseTitleAnimation();
                },
                getTimeBetweenFrame().toMillis() / 50
        );
    }

    @Override
    public void onStart() {
        List<UUID> playersUuids = getArena()
                .getPlayers()
                .stream()
                .map(squidPlayer -> squidPlayer.getBukkitPlayer().getUniqueId()).collect(Collectors.toList());
        int partitionSize = IntMath.divide(playersUuids.size(), 2, RoundingMode.UP);
        if (partitionSize == 2) {
            List<List<UUID>> teams = Lists.partition(playersUuids, partitionSize);
            Team.BLUE.getPlayers().addAll(teams.get(0));
            Team.RED.getPlayers().addAll(teams.get(1));
        }
        dispenseTitleAnimation();
    }

    @Override
    public void onTimeUp() {
        Team.BLUE.reset();
        Team.RED.reset();
        actualPointer.set(
                Pointer.empty()
        );
    }

    public static final class Pointer {
        private final Component visualRepresentation;
        private final Component hoverRepresentation;
        private final Integer weight;
        private boolean hovered;
        private static final Pointer EMPTY;

        static {
            EMPTY = new Pointer(
                    Component.empty(),
                    Component.empty(),
                    0
            );
        }

        public Pointer(final @NotNull Component visualRepresentation, final @NotNull Component hoverRepresentation, final @NotNull Integer weight) {
            this.visualRepresentation = Preconditions.checkNotNull(visualRepresentation);
            this.hoverRepresentation = Preconditions.checkNotNull(hoverRepresentation);
            this.weight = Preconditions.checkNotNull(weight);
        }

        public Component getVisualRepresentation() {
            return visualRepresentation;
        }

        public Component getRepresentation() {
            return (hovered) ? getHoverRepresentation() : getVisualRepresentation();
        }

        public Component getHoverRepresentation() {
            return hoverRepresentation;
        }

        public Pointer hover(boolean isHovered) {
            hovered = isHovered;
            return this;
        }

        public boolean isHovered() {
            return hovered;
        }

        public Integer getWeight() {
            return weight;
        }

        public static Pointer empty() {
            return EMPTY;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "{weight=" + getWeight() + ", hovered=" + isHovered() + ", representation=" + getVisualRepresentation().toString() + ", hoverRepresentation=" + getHoverRepresentation().toString() + "}";
        }
    }

    public enum Team {
        BLUE,
        RED;

        private final Set<UUID> players;
        private final AtomicInteger points;

        Team() {
            players = Collections.synchronizedSet(
                    new HashSet<>()
            );
            points = new AtomicInteger();
        }

        public Set<UUID> getPlayers() {
            return players;
        }

        public AtomicInteger getPoints() {
            return points;
        }

        public void reset() {
            players.clear();
            points.set(0);
        }

    }
}
