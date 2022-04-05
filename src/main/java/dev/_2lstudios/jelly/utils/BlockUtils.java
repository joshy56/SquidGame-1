package dev._2lstudios.jelly.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.function.Consumer;

public class BlockUtils {
    public static void destroyBlockGroup(final Block block, final boolean useParticles) {
        final Material target = block.getType();
        final Block up = block.getRelative(BlockFace.SOUTH);
        final Block down = block.getRelative(BlockFace.NORTH);
        final Block left = block.getRelative(BlockFace.EAST);
        final Block right = block.getRelative(BlockFace.WEST);

        if (useParticles) {
            block.breakNaturally();
        } else {
            block.setType(Material.AIR);
        }

        if (up.getType() == target) {
            destroyBlockGroup(up, useParticles);
        }

        if (down.getType() == target) {
            destroyBlockGroup(down, useParticles);
        }

        if (left.getType() == target) {
            destroyBlockGroup(left, useParticles);
        }

        if (right.getType() == target) {
            destroyBlockGroup(right, useParticles);
        }
    }

    public static void destroyBlockGroup(final Block block) {
        destroyBlockGroup(block, true);
    }

    public static void cuboid(final Location point1, final Location point2, Consumer<Block> visitor) {
        int xMin = Math.min(point1.getBlockX(), point2.getBlockX());
        int xMax = Math.max(point1.getBlockX(), point2.getBlockX());
        int yMin = Math.min(point1.getBlockY(), point2.getBlockY());
        int yMax = Math.max(point1.getBlockY(), point2.getBlockY());
        int zMin = Math.min(point1.getBlockZ(), point2.getBlockZ());
        int zMax = Math.max(point1.getBlockZ(), point2.getBlockZ());
        World world = point1.getWorld();

        for (int x = xMin; x < xMax; x++) {
            for (int y = yMin; y < yMax; y++) {
                for (int z = zMin; z < zMax; z++) {
                    visitor.accept(world.getBlockAt(x,y,z));
                }
            }
        }
    }
}
