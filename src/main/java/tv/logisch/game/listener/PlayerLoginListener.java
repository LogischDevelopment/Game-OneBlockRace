package tv.logisch.game.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import tv.logisch.game.enums.GameState;
import tv.logisch.game.manager.GameManager;

public class PlayerLoginListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {

        if(GameManager.get().state().equals(GameState.STARTING) || GameManager.get().state().equals(GameState.ENDING) || GameManager.get().state().equals(GameState.ENDED)) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, Component.text("§cThe game is currently not accepting new players."));
            return;
        }

    }

}
