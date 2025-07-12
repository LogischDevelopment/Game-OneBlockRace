package tv.logisch.oneBlockRace.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import tv.logisch.oneBlockRace.enums.GameState;
import tv.logisch.oneBlockRace.manager.GameManager;

public class PlayerLoginListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {

        if(GameManager.get().state().equals(GameState.STARTING) || GameManager.get().state().equals(GameState.ENDING) || GameManager.get().state().equals(GameState.ENDED)) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, Component.text("§cThe game is currently not accepting new players."));
            return;
        }

    }

}
