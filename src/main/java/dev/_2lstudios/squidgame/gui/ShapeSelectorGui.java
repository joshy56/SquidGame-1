package dev._2lstudios.squidgame.gui;

import dev._2lstudios.jelly.gui.InventoryGUI;
import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.arena.games.G2CookieGame;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.IOException;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 3/4/2022.
 */
public class ShapeSelectorGui extends InventoryGUI {
    private final Arena arena;

    public ShapeSelectorGui(final Arena arena, final InventoryGUI prevGui) {
        super("§d§lArena §f" + arena.getName(), 45, prevGui);
        this.arena = arena;
    }

    @Override
    public void init() {
        addItem(
                0,
                createItem(
                        "§eCIRCLE",
                        Material.ITEM_FRAME,
                        "§r\n§7Choose 'Circle' shape to edit it\n§r"
                ),
                1, 2
        );
        addItem(
                1,
                createItem(
                        "§eSTAR",
                        Material.ITEM_FRAME,
                        "§r\n§7Choose 'Star' shape to edit it\n§r"
                ),
                3, 2
        );
        addItem(
                2,
                createItem(
                        "§eCUBE",
                        Material.ITEM_FRAME,
                        "§r\n§7Choose 'Cube' shape to edit it\n§r"
                ),
                5, 2
        );
        addItem(
                3,
                createItem(
                        "§eTRIANGLE",
                        Material.ITEM_FRAME,
                        "§r\n§7Choose 'Triangle' shape to edit it\n§r"
                ),
                7, 2
        );
        addItem(
                4,
                createItem(
                        "§eUMBRELLA",
                        Material.ITEM_FRAME,
                        "§r\n§7Set at your current location\n§r"
                ),
                9, 2
        );

        addItem(
                99,
                createItem(
                        "§eGo back",
                        Material.BARRIER,
                        "§r\n§7Go to the previous menu or close\n§r"
                ),
                5, 4
        );

    }

    @Override
    public void handle(int id, Player player) {

        if(id == 99) {
            back(player);
            return;
        }

        switch (id){
            case 0: {
                new EditShapeStatsGui(arena, G2CookieGame.Shape.CIRCLE, this).open(player);
                break;
            }
            case 1: {
                new EditShapeStatsGui(arena, G2CookieGame.Shape.STAR, this).open(player);
                break;
            }
            case 2: {
                new EditShapeStatsGui(arena, G2CookieGame.Shape.CUBE, this).open(player);
                break;
            }
            case 3: {
                new EditShapeStatsGui(arena, G2CookieGame.Shape.TRIANGLE, this).open(player);
                break;
            }
            case 4: {
                new EditShapeStatsGui(arena, G2CookieGame.Shape.UMBRELLA, this).open(player);
                break;
            }
        }

        try {
            arena.getConfig().save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
