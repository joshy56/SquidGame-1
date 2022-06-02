package dev._2lstudios.squidgame.player;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import dev._2lstudios.jelly.player.PluginPlayer;
import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.hooks.PlaceholderAPIHook;
import org.jetbrains.annotations.Nullable;

public class SquidPlayer extends PluginPlayer {

    private Arena arena = null;
    private PlayerWand wand = null;
    private PlayerDataBackup backup;

    private final SquidGame plugin;

    public SquidPlayer(final SquidGame plugin, final Player player) {
        super(player);
        this.plugin = plugin;
    }

    public PlayerWand getWand() {
        return this.wand;
    }

    public PlayerWand createWand(final PlayerWand wand) {
        this.wand = wand;
        return this.wand;
    }

    public SquidPlayer clearBackup(){
        this.backup = PlayerDataBackupBuilder.empty().build();
        return this;
    }

    public SquidPlayer restoreFromBackup(){
        if(backup != null)
            backup.apply(getBukkitPlayer());
        return this;
    }

    public SquidPlayer resetBackup(){
        this.backup = PlayerDataBackupBuilder.copyOf(getBukkitPlayer()).build();
        return this;
    }

    @Nullable
    public Arena getArena() {
        if(arena == null)
            return null;
        if(!arena.getAllPlayers().contains(this))
            setArena(null);
        return arena;
    }

    public void setArena(@Nullable final Arena arena) {
        if (arena == null && this.arena != null) {
            this.arena.removePlayer(this);
            this.arena = null;
        } else if (arena != null && this.arena == null) {
            this.arena = arena;
            arena.addPlayer(this);
        }
    }

    public boolean isSpectator() {
        Arena currentArena = getArena();
        if(currentArena == null)
            return false;
        return currentArena.getSpectators().contains(this);
    }

    public void teleportToLobby() {
        this.teleport(this.plugin.getMainConfig().getLocation("lobby"));
    }

    public String getI18n(final String key) {
        return this.plugin.getMessagesConfig().getString(key);
    }

    private String formatMessage(final String message) {
        final String translatedMessage = this.getI18n(message);
        final String formatColor = ChatColor.translateAlternateColorCodes('&',
                translatedMessage == null
                        ? "§6§lWARNING: §eMissing translation key §7" + message + " §ein message.yml file"
                        : translatedMessage);
        final String replacedVariables = PlaceholderAPIHook.formatString(formatColor, this.getBukkitPlayer());
        return replacedVariables;
    }

    public void sendMessage(final String message) {
        this.getBukkitPlayer().sendMessage(this.formatMessage(message));
    }

    public void sendTitle(final String title, final String subtitle, final int duration) {
        super.sendTitle(this.formatMessage(title), this.formatMessage(subtitle), duration);
    }

    public void sendScoreboard(final String scoreboardKey) {
        this.plugin.getScoreboardHook().request(this.getBukkitPlayer(),
                this.plugin.getScoreboardConfig().getStringList(scoreboardKey));
    }
}
