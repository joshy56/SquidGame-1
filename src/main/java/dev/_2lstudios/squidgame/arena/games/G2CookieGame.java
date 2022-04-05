package dev._2lstudios.squidgame.arena.games;

import com.google.common.collect.ImmutableList;
import dev._2lstudios.jelly.utils.NumberUtils;
import dev._2lstudios.jelly.utils.ObjectUtils;
import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.player.SquidPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 29/3/2022.
 */
public class G2CookieGame extends ArenaGameBase {

    private EnumSet<Shape> shapes;
    private Material shapeLine;
    private Map<SquidPlayer, Map.Entry<Integer, AtomicInteger>> players;

    static {
        ConfigurationSerialization.registerClass(Shape.class);
    }

    public G2CookieGame(final Arena arena, int gameTime) {
        super("§6Honeycomb", "second", gameTime, arena);
    }

    @Nullable
    public EnumSet<Shape> getShapes() {
        if (shapes == null)
            shapes = Optional.ofNullable(this.getArena().getConfig().getList("games.second.shapes"))
                    .map(
                            list -> list.stream()
                                    .filter(Shape.class::isInstance)
                                    .map(Shape.class::cast)
                                    .collect(Collectors.toList())
                    )
                    .map(EnumSet::copyOf)
                    .orElse(null);
        return shapes;
    }

    @Nullable
    public Material getShapeLine() {
        if (shapeLine == null)
            shapeLine = Optional.ofNullable(this.getArena().getConfig().getString("games.second.shape.material-delimiter"))
                    .map(
                            name -> {
                                try {
                                    return Material.valueOf(name);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }
                    )
                    .orElse(null);
        return shapeLine;
    }

    @Nullable
    public Map.Entry<Shape, Integer> getShapeAndProgressOf(SquidPlayer player) {
        if (players == null)
            return null;
        if (!ObjectUtils.checkEquals(player.getArena(), getArena()))
            return null;
        return Optional.ofNullable(getShapes())
                .map(s -> s.toArray(new Shape[0]))
                .map(
                        s -> {
                            Map.Entry<Integer, AtomicInteger> stats = players.get(player);
                            if(stats.getKey() <= s.length)
                                return new AbstractMap.SimpleEntry<>(s[stats.getKey()], stats.getValue().get());
                            return null;
                        }
                )
                .orElse(null);
    }

    private void generateShapes(Material shapeLine) {
        Optional.ofNullable(getShapes())
                .ifPresent(
                        shapes -> shapes.stream()
                                .map(Shape::getPoints)
                                .forEach(
                                        list -> list.stream()
                                                .map(blockVector -> getArena().getWorld().getBlockAt(blockVector.getBlockX(), blockVector.getBlockY(), blockVector.getBlockZ()))
                                                .forEach(block -> block.setType(shapeLine))
                                )
                );
    }

    private void shuffleShapesAndApplyToPlayers() {
        Shape[] shapes = Optional.ofNullable(getShapes()).map(s -> s.toArray(new Shape[0])).orElse(new Shape[0]);

        if (shapes.length >= 1)
            players = getArena().getPlayers().stream()
                    .map(
                            squidPlayer -> {
                                int shape = NumberUtils.randomNumber(
                                        1,
                                        shapes.length
                                );
                                return new AbstractMap.SimpleEntry<>(
                                        squidPlayer,
                                        new AbstractMap.SimpleEntry<>(shape, new AtomicInteger())
                                );
                            }
                    )
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public boolean mineShapeAndCheckIfWin(SquidPlayer player){
        return Optional.ofNullable(getShapeAndProgressOf(player))
                .map(
                        stats -> {
                            if(stats.getValue() == -1)
                                return true;
                            AtomicInteger progress = new AtomicInteger(stats.getValue());
                            progress.incrementAndGet();
                            if(stats.getKey().getPoints().size() >= progress.incrementAndGet())
                                progress.set(-1);
                            players.put(
                                    player,
                                    new AbstractMap.SimpleEntry<>(
                                            players.get(player).getKey(),
                                            progress
                                    )
                            );
                            return false;
                        }
                )
                .orElse(false);
    }

    @Override
    public void onExplainStart() {
        super.onExplainStart();
        generateShapes(Material.AIR);
    }

    @Override
    public void onStart() {
        Optional.ofNullable(getShapeLine())
                .ifPresent(this::generateShapes);
        shuffleShapesAndApplyToPlayers();
        players.forEach(
                (squidPlayer, stats) -> squidPlayer.teleport(
                        getShapeAndProgressOf(squidPlayer).getKey().getShootPoint()
                )
        );
    }

    @Override
    public void onTimeUp() {
        generateShapes(Material.AIR);
        players = null;
    }

    public enum Shape implements ConfigurationSerializable {
        CIRCLE,
        STAR,
        CUBE,
        TRIANGLE,
        UMBRELLA;

        private List<BlockVector> points;
        private Location shootPoint;

        Shape() {
            points = new ArrayList<>();
        }

        public Shape setPoints(@NotNull Iterable<BlockVector> points) {
            this.points = (List<BlockVector>) points;
            return this;
        }

        public ImmutableList<BlockVector> getPoints() {
            return ImmutableList.copyOf(points);
        }

        public Shape setShootPoint(Location shootPoint) {
            this.shootPoint = shootPoint;
            return this;
        }

        @Nullable
        public Location getShootPoint() {
            return shootPoint;
        }

        @NotNull
        @Override
        public Map<String, Object> serialize() {
            LinkedHashMap<String, Object> args = new LinkedHashMap<>();
            args.put(
                    "name",
                    name()
            );
            args.put(
                    "points",
                    getPoints()
            );
            Optional.ofNullable(getShootPoint())
                    .ifPresent(shootPoint -> args.put("spawn", shootPoint));
            return args;
        }

        @NotNull
        public static Shape deserialize(@NotNull Map<String, Object> args) {
            Shape shape = Optional.ofNullable(args.get("name"))
                    .filter(String.class::isInstance)
                    .map(String::valueOf)
                    .map(
                            name -> {
                                try {
                                    return Shape.valueOf(name);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }
                    )
                    .map(
                            s -> {
                                Optional.ofNullable(args.get("spawn"))
                                        .filter(Location.class::isInstance)
                                        .map(Location.class::cast)
                                        .ifPresent(s::setShootPoint);
                                return Optional.ofNullable(args.get("points"))
                                        .filter(Collection.class::isInstance)
                                        .map(Collection.class::cast)
                                        .map(collection -> new ArrayList<Object>(collection))
                                        .map(
                                                list -> list.stream()
                                                        .filter(BlockVector.class::isInstance)
                                                        .map(BlockVector.class::cast)
                                                        .collect(Collectors.toList())
                                        )
                                        .map(s::setPoints)
                                        .orElse(s);
                            }
                    )
                    .orElse(null);
            return shape;
        }
    }
}
