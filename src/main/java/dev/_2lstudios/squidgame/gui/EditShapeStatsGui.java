package dev._2lstudios.squidgame.gui;

import dev._2lstudios.jelly.gui.InventoryGUI;
import dev._2lstudios.jelly.utils.BlockUtils;
import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.arena.games.G2CookieGame;
import dev._2lstudios.squidgame.player.PlayerWand;
import dev._2lstudios.squidgame.player.SquidPlayer;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.BlockVector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 3/4/2022.
 */
public class EditShapeStatsGui extends InventoryGUI {

    private final Arena arena;
    private final G2CookieGame.Shape shape;

    public EditShapeStatsGui(final Arena arena, final G2CookieGame.Shape shape, final InventoryGUI prevGui) {
        super("§d§lArena §f" + arena.getName(), 45, prevGui);
        this.arena = arena;
        this.shape = shape;
    }

    @Override
    public void init() {
        addItem(
                0,
                createItem(
                        "§eSpawn shot point",
                        Material.COMPASS,
                        "§r\n§7Set at your current location\n§r"
                ),
                3, 2
        );
        addItem(
                1,
                createItem(
                        "§eMine points",
                        Material.PAINTING,
                        "§r\n§7Set \n§r"
                ),
                7, 2
        );

        addItem(99, this.createItem("§cBack", Material.BARRIER), 5, 4);
    }

    @Override
    public void handle(int id, Player player) {
        SquidPlayer squidPlayer = (SquidPlayer) SquidGame.getInstance().getPlayerManager().getPlayer(player);
        PlayerWand wand = squidPlayer.getWand();

        if(id == 99){
            back(player);
            return;
        }

        switch(id){
            case 0: {
                arena.getConfig().setLocation("games.second.shapes." + shape.name() + ".spawn", player.getLocation(), false);
                player.sendMessage("§eGame shape spawn§a set in your current location.");
                break;
            }
            case 1: {
                if(wand == null || !wand.isComplete()){
                    player.sendMessage("§eNot has a wand selection§c give a wand with '/sg wand' and select an area.");
                    return;
                }
                ItemStack delimiter = arena.getConfig().getItemStack("games.second.shapes." + shape.name() + ".material-delimiter");
                List<BlockVector> minePoints = new ArrayList<>();
                BlockUtils.cuboid(
                        wand.getFirstPoint().toLocation(arena.getWorld()),
                        wand.getSecondPoint().toLocation(arena.getWorld()),
                        block -> {
                            Bukkit.getConsoleSender().sendMessage(
                                    new String[]{
                                            "BlockType= " + block.getType(),
                                            "DelimiterType= " + delimiter.getType()
                                    }
                            );
                            if(block.getType() == delimiter.getType())
                                minePoints.add(block.getLocation().toVector().toBlockVector());
                        }
                );
                Bukkit.getConsoleSender().sendMessage(
                        minePoints.toString()
                );
                shape.setPoints(minePoints);
                arena.getConfig().set(
                        "games.second.shapes." + shape.name() + ".mine-points",
                        shape.getPoints()
                                /*.stream()
                                .map(BlockVector::serialize)
                                .collect(Collectors.toList())*/
                );
                player.sendMessage("§eGame shape shoot points§a set in your current wand selection.");
                break;
            }
        }

        try{
            arena.getConfig().save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
