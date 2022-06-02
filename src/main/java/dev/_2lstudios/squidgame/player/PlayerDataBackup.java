package dev._2lstudios.squidgame.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 1/6/2022.
 */
public class PlayerDataBackup {
    private final PlayerDataBackupBuilder builder;
    private static final PlayerDataBackup EMPTY;

    static {
        EMPTY = PlayerDataBackupBuilder.empty().build();
    }

    public PlayerDataBackup(@NotNull final PlayerDataBackupBuilder builder){
        this.builder = builder;
    }

    public PlayerDataBackup apply(@NotNull Player player){
        player.setGameMode(builder.getGameMode());
        player.setHealth(builder.getHealth());
        player.setFoodLevel(builder.getFood());
        player.setExp(builder.getExperience());
        player.addPotionEffects(builder.getActivePotions());
        player.getInventory().setContents(builder.getInventoryContent());
        player.setAllowFlight(builder.isAllowFlight());
        player.setFlying(builder.isFlying());
        return this;
    }

    public static PlayerDataBackup empty(){
        return EMPTY;
    }

}
