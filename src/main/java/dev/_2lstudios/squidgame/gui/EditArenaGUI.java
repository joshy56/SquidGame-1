package dev._2lstudios.squidgame.gui;

import dev._2lstudios.jelly.gui.InventoryGUI;
import dev._2lstudios.squidgame.arena.Arena;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class EditArenaGUI extends InventoryGUI {

    private final Arena arena;

    public EditArenaGUI(final Arena arena) {
        super("§d§lArena §f" + arena.getName(), 5 * 9);
        this.arena = arena;
    }

    @Override
    public void init() {
        this.addItem(
                1,
                this.createItem("§eFirst game", Material.ENDER_PEARL, "§r\n§aGreen Light§7, §cRed Light §7game.\n§r"),
                2, 2
        );

        this.addItem(2, this.createItem("§cSecond game", Material.IRON_PICKAXE), 3, 2);
        this.addItem(3, this.createItem("§eThird Game", Material.DIAMOND_SWORD,
                        "§r\n§7This game does not require configuration.\n§7It uses the values from the §bintermission lobby§7.\n§r"),
                4, 2);
        this.addItem(4, this.createItem("§cComing soon", Material.COAL), 5, 2);
        this.addItem(5, this.createItem("§cComing soon", Material.COAL), 6, 2);

        this.addItem(6, this.createItem("§eSixth game", Material.GLASS, "§r\n§bGlass §fGame§7.\n§r"), 7, 2);
        this.addItem(7,
                this.createItem("§eSeventh game", Material.STICK, "§r\n§dSquid§fGame§7, is the Final game.\n§r"), 8, 2);
        this.addItem(0,
                this.createItem("§bIntermission", Material.COMPASS, "§r\n§7Where players spawn after each game.\n§r"),
                4, 4);
        this.addItem(99, this.createItem("§cExit", Material.BARRIER), 6, 4);
    }

    @Override
    public void handle(int id, Player player) {
        switch (id) {
            case 0:
                new EditArenaWaitingLobbyGUI(this.arena, this).open(player);
                break;
            case 1:
                new EditArenaGame1GUI(this.arena, this).open(player);
                break;
            case 2:
                new EditArenaGame2GUI(arena, this).open(player);
                break;
            case 6:
                new EditArenaGame6GUI(this.arena, this).open(player);
                break;
            case 7:
                new EditArenaGame7GUI(this.arena, this).open(player);
                break;
            default:
                this.close(player);
                break;
        }
    }
}
