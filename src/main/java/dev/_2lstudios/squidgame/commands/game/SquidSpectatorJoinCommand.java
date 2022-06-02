package dev._2lstudios.squidgame.commands.game;

import com.google.common.base.Strings;
import dev._2lstudios.jelly.annotations.Command;
import dev._2lstudios.jelly.commands.CommandContext;
import dev._2lstudios.jelly.commands.CommandExecutionTarget;
import dev._2lstudios.jelly.commands.CommandListener;
import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.player.PlayerManager;
import dev._2lstudios.squidgame.player.SquidPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 1/6/2022.
 */
@Command(
        name = "spectate",
        description = "Join an arena as spectator",
        permission = "sg.spectator.join",
        usage = "/sg spectate <arena>",
        arguments = {
                String.class
        },
        minArguments = 1,
        target = CommandExecutionTarget.ONLY_PLAYER
)
public class SquidSpectatorJoinCommand extends CommandListener {
    @Override
    public void handle(CommandContext context) throws Exception {
        final SquidGame plugin = (SquidGame) context.getPlugin();
        final SquidPlayer player = (SquidPlayer) context.getPluginPlayer();

        String arenaName = context.getArguments().getString(0);
        if(Strings.isNullOrEmpty(arenaName)) {
            player.sendMessage("commands.spectate.missed-argument.arena-name");
            return;
        }

        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if(arena == null) {
            player.sendMessage("commands.spectate.arena-not-found");
            return;
        }

        arena.addSpectator(player);
        player.sendMessage("commands.spectate.success-joined");
    }
}
