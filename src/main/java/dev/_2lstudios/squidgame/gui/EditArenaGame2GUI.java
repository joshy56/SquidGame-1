package dev._2lstudios.squidgame.gui;

import dev._2lstudios.jelly.gui.InventoryGUI;
import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.player.PlayerWand;
import dev._2lstudios.squidgame.player.SquidPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 2/4/2022.
 */
public class EditArenaGame2GUI extends InventoryGUI {

    private final Arena arena;

    public EditArenaGame2GUI(final Arena arena, final InventoryGUI prevGui) {
        super("§d§lArena §f" + arena.getName(), 45, prevGui);
        this.arena = arena;
    }

    @Override
    public void init() {
        this.addItem(
                0,
                createItem("§eSpawn point", Material.COMPASS, "§r\n§7Set at your current location\n§r"),
                2, 2
        );
        this.addItem(
                1,
                createItem(
                        "§eShape line",
                        Material.ITEM_FRAME,
                        "§r\n§7Set line that delimit shape\n§r"
                ),
                4, 2
        );

        this.addItem(
                2,
                createItem(
                        "§eShapes",
                        Material.PAINTING,
                        "§r\n§7Set shapes properties\n§r"
                ),
                6, 2
        );

        this.addItem(99, this.createItem("§cBack", Material.BARRIER), 5, 4);
    }

    @Override
    public void handle(int id, Player player) {
        SquidPlayer squidPlayer = (SquidPlayer) SquidGame.getInstance().getPlayerManager().getPlayer(player);

        if(id == 99){
            back(player);
            return;
        }

        if(id == 0) {
            arena.getConfig().setLocation("games.second.spawn", player.getLocation(), false);
            player.sendMessage("§eGame spawn§a set in your current location.");
        }else if(id == 1){
            Material material = Optional.ofNullable(player.getItemOnCursor())
                            .map(ItemStack::getType)
                                    .orElse(Material.AIR);
            if(material.isAir()){
                close(player);
                player.sendMessage("§eGame shape delimiter§c can't be air.");
                return;
            }else if(!material.isBlock()){
                close(player);
                player.sendMessage("§eGame shape delimiter§c must be and placable block.");
                return;
            }
            arena.getConfig().set("games.second.shape.material-delimiter", material.name());
            player.sendMessage("§eGame shape delimiter§a set in your current item in cursor.");
        }else if(id == 2){
            Bukkit.getConsoleSender().sendMessage(
                    "SquidGame#EditArenaGame2GUI(PressedButton#2)"
            );
            new ShapeSelectorGui(arena, this).open(player);
            return;
        }

        try{
            arena.getConfig().save();
        }catch (IOException e){
            e.printStackTrace();
        }
        close(player);
    }
}
