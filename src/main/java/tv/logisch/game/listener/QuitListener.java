package tv.logisch.game.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import tv.logisch.game.manager.GameManager;
import tv.logisch.game.scoreboard.Scoreboard;

public class QuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {

        e.quitMessage(Component.empty());
        for (var target : e.getPlayer().getServer().getOnlinePlayers()) {
            target.sendMessage(Component.text("§c§lQUIT §8» §f" + e.getPlayer().getName() + " §7left the game!"));
        }

        GameManager.get().itemManager().removePlayer(e.getPlayer());
        Scoreboard.scoreboards.removeIf(s -> s.getPlayer().getUniqueId().equals(e.getPlayer().getUniqueId()));

    }

}
