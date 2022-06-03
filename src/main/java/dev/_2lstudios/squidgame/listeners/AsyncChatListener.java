package dev._2lstudios.squidgame.listeners;

import dev._2lstudios.jelly.player.PluginPlayer;
import dev._2lstudios.jelly.utils.ObjectUtils;
import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.player.SquidPlayer;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 2/6/2022.
 */
public class AsyncChatListener implements Listener {
    private static final ChatRenderer PER_ARENA_CHAT_RENDERER;

    static {
        PER_ARENA_CHAT_RENDERER = new PerArenaChatRenderer();
    }

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        Optional.ofNullable(SquidGame.getInstance())
                .filter(
                        plugin -> plugin.getMainConfig().getBoolean(
                                "game-settings.per-arena-chat", true
                        )
                )
                .ifPresent(
                        plugin -> {
                            PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer());
                            if (pluginPlayer == null)
                                return;
                            SquidPlayer player = (SquidPlayer) pluginPlayer;

                            if(player.getArena() == null)
                                return;

                            event.viewers().removeIf(
                                    audience -> {
                                        if(!(audience instanceof Player))
                                            return false;
                                        PluginPlayer anotherPluginPlayer = plugin.getPlayerManager().getPlayer((Player) audience);
                                        if (anotherPluginPlayer == null)
                                            return false;
                                        SquidPlayer anotherPlayer = (SquidPlayer) anotherPluginPlayer;

                                        /*Render message logic*/
                                        return !(ObjectUtils.checkEquals(anotherPlayer.getArena(), player.getArena()));
                                    }
                            );
                        }
                );
    }

    private static final class PerArenaChatRenderer implements ChatRenderer {

        @Override
        public @NotNull Component render(@NotNull Player player, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience audience) {
            return Optional.ofNullable(SquidGame.getInstance())
                    .filter(
                            plugin -> plugin.getMainConfig().getBoolean(
                                    "game-settings.per-arena-chat", true
                            )
                    )
                    .map(
                            plugin -> {
                                /*Sender of message logic*/
                                PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayer(player);
                                if (pluginPlayer == null)
                                    return Component.empty();
                                SquidPlayer squidPlayer = (SquidPlayer) pluginPlayer;

                                /*Receiver of message logic*/
                                if(!(audience instanceof Player))
                                    return Component.empty();
                                PluginPlayer anotherPluginPlayer = plugin.getPlayerManager().getPlayer((Player) audience);
                                if (anotherPluginPlayer == null)
                                    return Component.empty();
                                SquidPlayer anotherPlayer = (SquidPlayer) anotherPluginPlayer;

                                /*Render message logic*/
                                if (ObjectUtils.checkEquals(anotherPlayer.getArena(), squidPlayer.getArena()))
                                    return Component.translatable("chat.type.text", sourceDisplayName, message);
                                return null;
                            }
                    )
                    .orElse(Component.empty());
        }
    }

}
