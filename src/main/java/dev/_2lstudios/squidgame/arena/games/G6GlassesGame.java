package dev._2lstudios.squidgame.arena.games;

import java.util.ArrayList;
import java.util.List;

import dev._2lstudios.squidgame.arena.games.listeners.G6GlassesGameListener;
import dev._2lstudios.squidgame.arena.games.listeners.GameListener;
import dev._2lstudios.squidgame.events.ArenaDispatchActionEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import dev._2lstudios.jelly.math.Cuboid;
import dev._2lstudios.jelly.math.Vector3;
import dev._2lstudios.jelly.utils.BooleanUtils;
import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.player.SquidPlayer;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

public class G6GlassesGame extends ArenaGameBase {

    private Cuboid glassZone;
    private Cuboid goalZone;

    private final List<Block> fakeBlocks;
    private static final GameListener LISTENER;

    static {
        LISTENER = new G6GlassesGameListener();
    }

    public G6GlassesGame(final Arena arena, final int durationTime) {
        super("§bGlasses", "sixth", durationTime, arena);

        this.fakeBlocks = new ArrayList<>();
    }

    private Cuboid getGlassZone() {
        if (this.glassZone == null) {
            this.glassZone = this.getArena().getConfig().getCuboid("games.sixth.glass");
        }

        return this.glassZone;
    }

    public Cuboid getGoalZone() {
        if (this.goalZone == null) {
            this.goalZone = this.getArena().getConfig().getCuboid("games.sixth.goal");
        }

        return this.goalZone;
    }

    public boolean isFakeBlock(final Block block) {
        return this.fakeBlocks.contains(block);
    }

    private void generateTiles(final Material material) {
        final World world = this.getArena().getWorld();

        Vector firstPoint = getGlassZone().getFirstPoint().toLocation(world).toVector(),
                secondPoint = getGlassZone().getSecondPoint().toLocation(world).toVector();
        final Vector maximum = Vector.getMaximum(firstPoint,secondPoint);
        final Vector minimum = Vector.getMinimum(firstPoint,secondPoint);


        world.getBlockAt(maximum.toLocation(world)).setType(Material.GLASS);
        world.getBlockAt(minimum.toLocation(world)).setType(Material.GLASS);

        // Obtener diferencia entre puntos X
        final int differenceBetweenX = (int) Math.abs(maximum.getX() - minimum.getX());
        // Obtener diferencia entre puntos Z
        final int differenceBetweenZ = (int) Math.abs(maximum.getZ() - minimum.getZ());
        // Verificar si se debe usar el Z como un indice
        final boolean useZAsIndex = differenceBetweenZ > differenceBetweenX;
        // Verificar si se debe aumentar o restar el valor indice
        final boolean shouldIncreaseIndex = useZAsIndex ? maximum.getZ() < minimum.getZ() : maximum.getX() < minimum.getX();

        // Obtener el width del suelo dependiendo hacia donde esté señalando el area
        final int groundWidth = (useZAsIndex ? differenceBetweenX : differenceBetweenZ) + 1;
        // Obtener el height del suelo dependiendo hacia donde esté señalando el area
        final int groundHeight = (useZAsIndex ? differenceBetweenZ : differenceBetweenX) + 1;

        // Parametros de las paltaformas
        final int size = groundWidth < 5 ? 1 : groundWidth < 7 ? 2 : 3;
        final int spaceXBetweenPlatforms = groundWidth - (size * 2);
        final int spaceZBetweenPlatforms = 3;

        // Indice de bloque (Incrementa o decrementa relativamente a la dirección de
        // generación)
        int blockIndex = (int) (useZAsIndex ? maximum.getZ() : maximum.getX());

        // Inicio del X, valor inmutable y absoluto.
        final int xStart = Math.min((int) maximum.getX(), (int) minimum.getX());
        // Inicio del Y, valor inmutable y absoluto
        final int yStart = (int) maximum.getY();
        // Inicio del Z, valor inmutable y absoluto.
        final int zStart = Math.max((int) maximum.getZ(), (int) minimum.getZ());

        // Obtener el número de pares de plataformas a generar dependiendo el tamaño
        final int platformGroups = groundHeight / (spaceZBetweenPlatforms + size);

        // Por cada grupo de plataforma (+ 1) iterar:
        for (int i = 0; i <= platformGroups; i++) {
            // Is first pair item a fake block?
            boolean isFirstFake = BooleanUtils.randomBoolean();

            // Por cada posición relativa X de la plataforma
            for (int xPadding = 0; xPadding < size; xPadding++) {
                // Por cada posición relativa Y de la plataforma
                for (int zPadding = 0; zPadding < size; zPadding++) {
                    // Define blocks
                    Block firstRowBlock, secondRowBlock;

                    // En caso que la coordenada Z deba de usarse como un indice:
                    if (useZAsIndex) {
                        // Sumarle valor relativo x padding al valor absoluto x start
                        int x = xStart + xPadding;
                        // Sumarle el valor del indice Z al valor relativo z padding
                        int z = shouldIncreaseIndex ? blockIndex + zPadding : blockIndex - zPadding;

                        // Generar bloque en las coordenadas dadas X Y Z
                        firstRowBlock = world.getBlockAt(x, yStart, z);

                        // Generar bloque en la misma posicion que el de arriba pero con una separación
                        secondRowBlock = world.getBlockAt(x + spaceXBetweenPlatforms + size, yStart, z);
                    } else {
                        // Sumarle el valor del indice X al valor relativo x padding
                        int x = shouldIncreaseIndex ? blockIndex + xPadding : blockIndex - xPadding;
                        // Sumarle valor relativo z padding al valor absoluto z start
                        int z = zStart + zPadding;

                        // Generar bloque en las coordenadas dadas X Y Z
                        firstRowBlock = world.getBlockAt(x, yStart, z);

                        // Generar bloque en la misma posicion que el de arriba pero con una separación
                        secondRowBlock = world.getBlockAt(x, yStart, z + spaceXBetweenPlatforms + size);
                    }

                    firstRowBlock.setType(material);
                    secondRowBlock.setType(material);

                    if (material != Material.AIR) {
                        if (isFirstFake) {
                            this.fakeBlocks.add(firstRowBlock);
                        } else {
                            this.fakeBlocks.add(secondRowBlock);
                        }
                    }
                }
            }

            final int separation = spaceZBetweenPlatforms + size;
            blockIndex = shouldIncreaseIndex ? blockIndex + separation : blockIndex - separation;
        }
    }

    @Override
    public void onExplainStart() {
        super.onExplainStart();
        this.generateTiles(Material.AIR);
    }

    @Override
    public void onStart() {
        this.generateTiles(Material.GLASS);
    }

    @Override
    public void onTimeUp() {
        this.generateTiles(Material.AIR);

        this.getArena().broadcastTitle("events.game-timeout.title", "events.game-timeout.subtitle");

        final List<SquidPlayer> alive = new ArrayList<>();
        final List<SquidPlayer> death = new ArrayList<>();

        for (final SquidPlayer squidPlayer : this.getArena().getPlayers()) {
            final Player player = squidPlayer.getBukkitPlayer();
            final Location location = player.getLocation();
            final Vector3 position = new Vector3(location.getX(), location.getY(), location.getZ());

            if (this.getGoalZone().isBetween(position)) {
                alive.add(squidPlayer);
            } else {
                death.add(squidPlayer);
            }
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

    @Override
    public GameListener getEventsListener() {
        return LISTENER;
    }
}