package dev._2lstudios.squidgame.arena.games;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.arena.ArenaState;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collector;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 29/3/2022.
 */
public class G4RopePullingGame extends ArenaGameBase {
    private AtomicReference<Pointer> actualPointer;
    private ListIterator<Pointer> iterator;
    private boolean returning;
    private Integer timeBetweenFrame;

    public G4RopePullingGame(String name, String configKey, int gameTime, Arena arena) {
        super("§3Tug o' war", "four", gameTime, arena);
        getPointers();
    }

    public List<Title> getTitles() {
        return Streams.stream(iterator).collect(asTitles());
    }

    public List<Pointer> getPointers() {
        if (iterator == null) {
            //Take list of pointers from the config
            //Init iterator using iterator() method from the list get previously
            iterator = Lists.newArrayList(
                    new Pointer(
                            Component.text('|'),
                            Component.text('#'),
                            1
                    ),
                    new Pointer(
                            Component.text('|'),
                            Component.text('#'),
                            2
                    ),
                    new Pointer(
                            Component.text('|'),
                            Component.text('#'),
                            3
                    ),
                    new Pointer(
                            Component.text('|'),
                            Component.text('#'),
                            2
                    ),
                    new Pointer(
                            Component.text('|'),
                            Component.text('#'),
                            1
                    )
            ).listIterator();
        }
        return Lists.newArrayList(iterator);
    }

    public int getTimeBetweenFrame(){
        if(timeBetweenFrame == null){
            timeBetweenFrame = 10;
        }
        return timeBetweenFrame;
    }

    public final Pointer getActualPointer() {
        return actualPointer.get();
    }

    public void dispenseTitleAnimation() {
        Bukkit.getScheduler().runTaskLater(
                JavaPlugin.getPlugin(SquidGame.class),
                task -> {
                    if(getArena().getState() == ArenaState.FINISHING_GAME)
                        return;
                    Pointer pointer = actualPointer.get();
                    getTitles().forEach(
                            title -> getArena().getPlayers().forEach(
                                    squidPlayer -> squidPlayer.getBukkitPlayer().showTitle(
                                            title
                                    )
                            )
                    );

                    iterator.set(pointer.hover(false));
                    returning = ((returning || !iterator.hasNext()) && iterator.hasPrevious());
                    pointer = (returning) ? iterator.previous() : iterator.next();
                    pointer.hover(true);
                    iterator.set(pointer);
                    actualPointer.set(pointer);
                    dispenseTitleAnimation();
                },
                getTimeBetweenFrame()
        );
    }

    private Collector<Pointer, ?, List<Title>> asTitles() {
        return Collector.of(
                ArrayList::new,
                (left, right) -> left.add(
                        Title.title(
                                right.getVisualRepresentation(),
                                Component.empty(),
                                Title.Times.of(Duration.ZERO, Duration.ofMillis(getTimeBetweenFrame()), Duration.ZERO)
                        )
                ),
                (left, right) -> {
                    left.addAll(right);
                    return right;
                }
        );
    }

    @Override
    public void onStart() {
        dispenseTitleAnimation();
    }

    public final class Pointer {
        private final Component visualRepresentation;
        private final Component hoverRepresentation;
        private final Integer weight;
        private boolean hovered;

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
    }
}
