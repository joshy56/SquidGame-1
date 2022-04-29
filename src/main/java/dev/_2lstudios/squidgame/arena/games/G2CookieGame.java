package dev._2lstudios.squidgame.arena.games;

import com.google.common.collect.ImmutableList;
import dev._2lstudios.jelly.config.Configuration;
import dev._2lstudios.jelly.utils.NumberUtils;
import dev._2lstudios.jelly.utils.ObjectUtils;
import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.player.SquidPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
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
        players = new HashMap<>();
    }

    @Nullable
    public EnumSet<Shape> getShapes() {
        ConfigurationSection shapesSection = getArena().getConfig().getConfigurationSection("games.second.shapes");
        if(shapes == null)
        shapes = EnumSet.copyOf(
                shapesSection.getValues(false)
                .entrySet()
                .stream()
                .map(
                        entry -> {
                            try{
                                Shape shape = Shape.valueOf(entry.getKey());
                                Bukkit.getConsoleSender().sendMessage(
                                        entry.getValue().toString()
                                );
                                Configuration values = new Configuration(
                                        File.createTempFile("shapes", ".yml")
                                );
                                values.addDefaults(((ConfigurationSection) entry.getValue()).getValues(false));
                                return new AbstractMap.SimpleEntry<>(shape, values);
                            }catch (IllegalArgumentException | IOException e) {
                                e.printStackTrace();
                                return null;
                            }
                        }
                )
                .filter(Objects::nonNull)
                .map(
                        entry -> {
                            Configuration shapeSection = entry.getValue();
                            if(shapeSection.contains("spawn"))
                                Bukkit.getConsoleSender().sendMessage(
                                        "Shape( " + entry.getKey().name() + " )HasSpawn"
                                );
                            Location spawn = shapeSection.getLocation("spawn", false);
                            spawn.setWorld(getArena().getWorld());
                            List<BlockVector> points = (List<BlockVector>) shapeSection.getList("mine-points");
                            return entry.getKey()
                                    .setShootPoint(spawn)
                                    .setPoints(points);
                        }
                )
                .collect(Collectors.toSet())
        );
        /*if (shapes == null)
            shapes = Optional.ofNullable(this.getArena().getConfig().getList("games.second.shapes"))*//* Get configuration section per shape*//*
                    *//* Check if is a configuration section and extract data of shape and build it*//*
                    .map(
                            list -> list.stream()
                                    .filter(ConfigurationSection.class::isInstance)
                                    .map(ConfigurationSection.class::cast)
                                    .map(
                                            section -> {
                                                Location spawn = section.getLocation("spawn");
                                                List<BlockVector> points = Optional.ofNullable(section.getList("mine-points"))
                                                        .map(
                                                                l -> l.stream()
                                                                        .filter(BlockVector.class::isInstance)
                                                                        .map(BlockVector.class::cast)
                                                                        .collect(Collectors.toList())
                                                        )
                                                        .orElseGet(ArrayList::new);
                                                Bukkit.getConsoleSender().sendMessage(
                                                        "SG:Debug#Game2(getShapes#shapeName{ " + section.getName() + " })"
                                                );
                                                Bukkit.getConsoleSender().sendMessage(
                                                        "SG:Debug#Game2(getShapes#shapePointsSize{ " + points.size() + " })"
                                                );
                                                return Shape.valueOf(section.getName())
                                                        .setShootPoint(spawn)
                                                        .setPoints(points);
                                            }
                                    )
                                    .collect(Collectors.toList())
                    )
                    .map(EnumSet::copyOf)
                    .orElse(null);*/
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
                                        0,
                                        shapes.length - 1
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
                            AtomicInteger progress = new AtomicInteger(stats.getValue());
                            if (stats.getKey().getPoints().size() <= progress.incrementAndGet()) {
                                return true;
                            }
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

        this.getArena().broadcastTitle("events.game-timeout.title", "events.game-timeout.subtitle");

        final List<SquidPlayer> death = new ArrayList<>();
        final List<SquidPlayer> alive = new ArrayList<>();

        getArena().getPlayers().forEach(
                squidPlayer -> {
                    if(mineShapeAndCheckIfWin(squidPlayer))
                        alive.add(squidPlayer);
                    else
                        death.add(squidPlayer);
                }
        );

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


        players.clear();

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
