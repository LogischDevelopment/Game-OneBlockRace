package tv.logisch.oneBlockRace.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tv.logisch.oneBlockRace.OneBlockRace;
import tv.logisch.oneBlockRace.enums.GameState;
import tv.logisch.oneBlockRace.manager.GameManager;

public class SkipShopping implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if(!(sender instanceof Player p)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if(!GameManager.get().state().equals(GameState.SHOPPING)) {

            if(GameManager.get().state().equals(GameState.RUNNING) && p.hasPermission("logisch.oneblockrace.admin")) {
                if(GameManager.get().timeLeft() <= 30) {
                    p.sendMessage(Component.text(OneBlockRace.instance().prefix()+"§cYou can only vote to skip shopping if there are 30 seconds or less left."));
                    return true;
                }
                GameManager.get().timeLeft(30);
                return true;
            }

            p.sendMessage(Component.text(OneBlockRace.instance().prefix()+"§cYou can only vote to skip shopping during the shopping phase."));
            return true;
        }

        boolean success = GameManager.get().addSkippingVote(p);
        if(!success) {
            p.sendMessage(Component.text(OneBlockRace.instance().prefix()+"§cYou have already voted to skip shopping or the voting period has ended."));
            return true;
        }

        return true;

    }
}
