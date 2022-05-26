package dev._2lstudios.squidgame.arena.games;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.google.common.math.IntMath;
import dev._2lstudios.jelly.config.Configuration;
import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.arena.ArenaState;
import dev._2lstudios.squidgame.player.SquidPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.RoundingMode;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
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
    private ListIterator<Pointer> iterator;
    private boolean returning;
    private Integer timeBetweenFrame;

    public G4RopePullingGame(Arena arena, int gameTime) {
        super("§3Tug o' war", "four", gameTime, arena);
        getPointers();
        actualPointer = new AtomicReference<>(Pointer.empty());
    }

    @Override
    public Location getSpawnPosition() {
        final Configuration config = this.getArena().getConfig();
        final Location location = config.getLocation("arena.waiting_room", false);
        location.setWorld(this.getArena().getWorld());
        return location;
    }

    public List<Title> getTitles() {
        return getPointers().stream().collect(asTitles());
    }

    public Title getTitle() {
        return getPointers().stream()
                .map(
                        pointer -> Title.title(
                                pointer.getVisualRepresentation(),
                                Component.empty(),
                                Title.Times.of(
                                        Duration.ZERO, getTimeBetweenFrame(), Duration.ZERO
                                )
                        )
                )
                .reduce(
                        (left, right) -> Title.title(
                                TextComponent.ofChildren(
                                        left.title(),
                                        right.title()
                                ),
                                TextComponent.ofChildren(
                                        left.subtitle(),
                                        right.subtitle()
                                ),
                                left.times()
                        )
                )
                .orElse(
                        Title.title(
                                Component.text("¡¡Sin apuntadores!!"),
                                Component.empty(),
                                Title.Times.of(
                                        Duration.ZERO, getTimeBetweenFrame(), Duration.ZERO
                                )
                        )
                );
    }

    public List<Pointer> getPointers() {
        if (iterator == null) {
            //Take list of pointers from the config
            //Init iterator using iterator() method from the list get previously
            Bukkit.getConsoleSender().sendMessage(
                    "SG:Debug " + getClass().getSimpleName() + "#getPointers(Initializing iterator)"
            );
            iterator = Lists.newArrayList(
                    new Pointer(
                            Component.text('|', NamedTextColor.YELLOW),
                            Component.text('#', NamedTextColor.YELLOW),
                            1
                    ),
                    new Pointer(
                            Component.text('|', NamedTextColor.GOLD),
                            Component.text('#', NamedTextColor.GOLD),
                            2
                    ),
                    new Pointer(
                            Component.text('|', NamedTextColor.RED),
                            Component.text('#', NamedTextColor.RED),
                            3
                    ),
                    new Pointer(
                            Component.text('|', NamedTextColor.GOLD),
                            Component.text('#', NamedTextColor.GOLD),
                            2
                    ),
                    new Pointer(
                            Component.text('|', NamedTextColor.YELLOW),
                            Component.text('#', NamedTextColor.YELLOW),
                            1
                    )
            ).listIterator();
            Bukkit.getConsoleSender().sendMessage(
                    "SG:Debug " + getClass().getSimpleName() + "#getPointers(Pointers{ " + iterator.toString() + " })"
            );
        }
        return Lists.newArrayList(iterator);
    }

    public Duration getTimeBetweenFrame() {
        if (timeBetweenFrame == null) {
            timeBetweenFrame = 500;
        }
        return Duration.ofMillis(timeBetweenFrame);
    }

    public final Pointer getActualPointer() {
        return actualPointer.get();
    }

    @Nullable
    public Team getTeamOfPlayer(SquidPlayer player){
        if(Team.BLUE.getPlayers().contains(player.getBukkitPlayer().getUniqueId()))
            return Team.BLUE;
        if(Team.RED.getPlayers().contains(player.getBukkitPlayer().getUniqueId()))
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
                    Bukkit.getConsoleSender().sendMessage(
                            "SG:Debug G4RopePullingGame#dispenseTitleAnimation(" + title.toString() + ")"
                    );

                    if(!(pointer.equals(Pointer.empty())))
                        iterator.set(pointer.hover(false));
                    Bukkit.getConsoleSender().sendMessage(
                            "SG:Debug " + getClass().getSimpleName() + "#dispenseTitleAnimation(returning@" + returning + ")"
                    );
                    returning = ((returning || !iterator.hasNext()) && iterator.hasPrevious());
                    pointer = (returning) ? iterator.previous() : iterator.next();
                    pointer.hover(true);
                    iterator.set(pointer);
                    actualPointer.set(pointer);
                    dispenseTitleAnimation();
                },
                getTimeBetweenFrame().toMillis() / 50
        );
    }

    private Collector<Pointer, ?, List<Title>> asTitles() {
        return Collector.of(
                ArrayList::new,
                (left, right) -> left.add(
                        Title.title(
                                right.getVisualRepresentation(),
                                Component.empty(),
                                Title.Times.of(Duration.ZERO, getTimeBetweenFrame(), Duration.ZERO)
                        )
                ),
                (left, right) -> {
                    left.addAll(right);
                    return right;
                }
        );
    }

    private Collector<Pointer, ?, Title> asTitle() {
        return Collector.of(
                () -> {
                    return null;
                },
                (left, right) -> {

                },
                (left, right) -> {

                    return left;
                }
        );
    }

    @Override
    public void onStart() {
        List<UUID> playersUuids = getArena()
                .getPlayers()
                .stream()
                .map(squidPlayer -> squidPlayer.getBukkitPlayer().getUniqueId()).collect(Collectors.toList());
        int partitionSize = IntMath.divide(playersUuids.size(), 2, RoundingMode.UP);
        List<List<UUID>> teams = Lists.partition(playersUuids, partitionSize);
        Team.BLUE.getPlayers().addAll(teams.get(0));
        Team.RED.getPlayers().addAll(teams.get(1));
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

        public Integer getWeight() {
            return weight;
        }

        public static Pointer empty() {
            return EMPTY;
        }

    }

    public enum Team {
        BLUE,
        RED;

        private final Set<UUID> players;
        private final AtomicInteger points;
        Team(){
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

        public void reset(){
            players.clear();
            points.set(0);
        }

    }
}
