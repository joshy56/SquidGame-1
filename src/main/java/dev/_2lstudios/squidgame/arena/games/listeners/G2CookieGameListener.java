package dev._2lstudios.squidgame.arena.games.listeners;

import dev._2lstudios.squidgame.SquidGame;
import dev._2lstudios.squidgame.arena.Arena;
import dev._2lstudios.squidgame.arena.ArenaState;
import dev._2lstudios.squidgame.arena.games.ArenaGameBase;
import dev._2lstudios.squidgame.arena.games.G2CookieGame;
import dev._2lstudios.squidgame.events.ArenaDispatchActionEvent;
import dev._2lstudios.squidgame.events.PlayerGameWinEvent;
import dev._2lstudios.squidgame.player.SquidPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;
import java.util.Optional;

/**
 * Created by joshy23 (justJoshy23 - joshy56) on 2/6/2022.
 */
public class G2CookieGameListener extends GameListener {
    @Override
    @EventHandler
    public <T extends Event> void onArenaHandleEvent(ArenaDispatchActionEvent<T> event) {
        T action = event.getAction();

        if (action instanceof PlayerGameWinEvent)
            if (((PlayerGameWinEvent<?>) action).getGame() instanceof G2CookieGame)
                onPlayerGameWin((PlayerGameWinEvent<G2CookieGame>) action);

        if (action instanceof PlayerInteractEvent)
            onPlayerInteract((PlayerInteractEvent) action);

        if (action instanceof BlockBreakEvent)
            onBlockBreak((BlockBreakEvent) action);
    }

    public void onPlayerGameWin(PlayerGameWinEvent<G2CookieGame> event) {

        Arena arena = event.getGame().getArena();
        if(arena == null || !arena.equals(event.getWinner().getArena()))
            return;
        if(arena.getState() != ArenaState.IN_GAME)
            return;

        G2CookieGame game = event.getGame();
        SquidPlayer player = event.getWinner();
        if(game.getWinners().contains(player))
            return;

        if(game.mineShapeAndCheckIfWin(player)){
            player.sendTitle("events.game-pass.title", "events.game-pass.subtitle", 3);
            player.playSound(
                    arena.getMainConfig().getSound("game-settings.sounds.player-pass-game", "LEVELUP"));
            game.getWinners().add(player);
        }
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        SquidPlayer player = (SquidPlayer) SquidGame.getInstance().getPlayerManager().getPlayer(event.getPlayer());
        if (player == null)
            return;

        Arena arena = player.getArena();
        if (arena == null || player.isSpectator())
            return;
        if (arena.getState() != ArenaState.IN_GAME)
            return;

        ArenaGameBase currentGame = arena.getCurrentGame();
        if (!(currentGame instanceof G2CookieGame))
            return;

        if (currentGame.getWinners().contains(player))
            return;

        G2CookieGame game = (G2CookieGame) currentGame;

        Action action = event.getAction();
        if (!event.hasBlock() && (action == Action.LEFT_CLICK_AIR || action == Action.RIGHT_CLICK_AIR))
            Optional
                    .ofNullable(game.getSearchDistance())
                    .map(
                            searchDistance -> player.getBukkitPlayer().rayTraceBlocks(searchDistance).getHitBlock()
                    )
                    .ifPresent(
                            block -> new BlockBreakEvent(
                                    block, player.getBukkitPlayer()
                            ).callEvent()
                    );
        else new BlockBreakEvent(event.getClickedBlock(), player.getBukkitPlayer()).callEvent();
    }

    public void onBlockBreak(BlockBreakEvent event) {
        SquidPlayer player = (SquidPlayer) SquidGame.getInstance().getPlayerManager().getPlayer(event.getPlayer());
        if (player == null)
            return;

        Arena arena = player.getArena();
        if (arena == null || player.isSpectator())
            return;
        if (arena.getState() != ArenaState.IN_GAME)
            return;

        ArenaGameBase currentGame = arena.getCurrentGame();
        if (!(currentGame instanceof G2CookieGame))
            return;
        if (currentGame.getWinners().contains(player))
            return;

        G2CookieGame game = (G2CookieGame) currentGame;
        Map.Entry<G2CookieGame.Shape, Integer> playerShape = game.getShapeAndProgressOf(player);
        if(playerShape == null)
            return;

        Block clickedBlock = event.getBlock();
        if (clickedBlock == null || clickedBlock.isEmpty())
            return;
        if (playerShape.getKey().getPoints().contains(clickedBlock.getLocation().toVector().toBlockVector()))
            if(game.mineShapeAndCheckIfWin(player))
                new PlayerGameWinEvent<>(game, player).callEvent();
            else player.getBukkitPlayer().sendBlockChange(
                    clickedBlock.getLocation(),
                    Material.AIR.createBlockData()
            );
        else arena.killPlayer(player, true);
    }
}
