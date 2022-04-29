package dev._2lstudios.squidgame.arena.games;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import dev._2lstudios.squidgame.arena.Arena;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collector;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 29/3/2022.
 */
public class G4RopePullingGame extends ArenaGameBase {
    private AtomicReference<Pointer> actualPointer;
    private ArrayDeque<Pointer> pointers;
    private boolean returning;
    private int timeBetweenFrame;

    public G4RopePullingGame(String name, String configKey, int gameTime, Arena arena) {
        super("§3Tug o' war", "four", gameTime, arena);
    }

    public List<Title> getTitles(){
        return getPointers().stream().collect(asTitles());
    }

    public List<Pointer> getPointers(){
        return Lists.newArrayList(
                (returning) ? pointers.descendingIterator() : pointers.iterator()
        );
    }

    public final Pointer getActualPointer(){
        return actualPointer.get();
    }

    public void a(){
        getTitles().forEach(
                title -> {
                    getArena().getPlayers().forEach(
                            sPlayer -> sPlayer.getBukkitPlayer().showTitle(title)
                    );
                    returning = !returning;
                }
        );

    }

    private Collector<Pointer, ?, List<Title>> asTitles(){
        return Collector.of(
                ArrayList::new,
                (left, right) -> left.add(
                        Title.title(
                                right.getVisualRepresentation(),
                                Component.empty(),
                                Title.Times.of(Duration.ZERO, Duration.ofMillis(right.getWeight()), Duration.ZERO)
                        )
                ),
                (left, right) -> {
                    left.addAll(right);
                    return right;
                }
        );
    }

    public final class Pointer {
        private final Component visualRepresentation;
        private final Integer weight;

        public Pointer(final @NotNull Component visualRepresentation, final @NotNull Integer weight) {
            this.visualRepresentation = Preconditions.checkNotNull(visualRepresentation);
            this.weight = Preconditions.checkNotNull(weight);
        }

        public Component getVisualRepresentation() {
            return visualRepresentation;
        }

        public Integer getWeight() {
            return weight;
        }
    }
}
