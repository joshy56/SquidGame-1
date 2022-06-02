package dev._2lstudios.squidgame.player;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 1/6/2022.
 */
public final class PlayerDataBackupBuilder {
    private GameMode gameMode;
    private double health;
    private int food, experience;
    private List<PotionEffect> activePotions;
    private ItemStack[] inventoryContent;
    private boolean flying, allowFlight;

    public static PlayerDataBackupBuilder copyOf(@NotNull Player player){
        return new PlayerDataBackupBuilder()
                .withGameMode(player.getGameMode())
                .withHealth(player.getHealth())
                .withFood(player.getFoodLevel())
                .withExperience(player.getTotalExperience())
                .withActivePotions((List<PotionEffect>) player.getActivePotionEffects())
                .withInventoryContent(player.getInventory().getContents())
                .withFlightAllowed(player.getAllowFlight())
                .withFly(player.isFlying());
    }

    public static PlayerDataBackupBuilder empty(){
        return new PlayerDataBackupBuilder();
    }

    private PlayerDataBackupBuilder(){
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public PlayerDataBackupBuilder withGameMode(@NotNull final GameMode gameMode) {
        this.gameMode = gameMode;
        return this;
    }

    public double getHealth() {
        return health;
    }

    public PlayerDataBackupBuilder withHealth(double health) {
        this.health = health;
        return this;
    }

    public int getFood() {
        return food;
    }

    public PlayerDataBackupBuilder withFood(int food) {
        this.food = food;
        return this;
    }

    public int getExperience() {
        return experience;
    }

    public PlayerDataBackupBuilder withExperience(int experience) {
        this.experience = experience;
        return this;
    }

    public List<PotionEffect> getActivePotions() {
        return activePotions;
    }

    public PlayerDataBackupBuilder withActivePotions(@Nullable final List<PotionEffect> activePotions) {
        this.activePotions = activePotions;
        return this;
    }

    public ItemStack[] getInventoryContent() {
        return inventoryContent;
    }

    public PlayerDataBackupBuilder withInventoryContent(ItemStack... inventoryContent) {
        this.inventoryContent = inventoryContent;
        return this;
    }

    public boolean isFlying() {
        return flying;
    }

    public PlayerDataBackupBuilder withFly(boolean flying) {
        this.flying = flying;
        return this;
    }

    public boolean isAllowFlight() {
        return allowFlight;
    }

    public PlayerDataBackupBuilder withFlightAllowed(boolean allowFlight) {
        this.allowFlight = allowFlight;
        return this;
    }

    public PlayerDataBackup build(){
        return new PlayerDataBackup(this);
    }
}
