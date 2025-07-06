package tv.logisch.oneBlockRace.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import tv.logisch.oneBlockRace.enums.GameState;
import tv.logisch.oneBlockRace.gui.SettingGUI;
import tv.logisch.oneBlockRace.manager.GameManager;

public class EventCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if(!(sender instanceof org.bukkit.entity.Player p)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if(!p.hasPermission("logisch.event.admin") && !GameManager.get().isHost(p.getUniqueId())) {
            p.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if(args.length == 0) {
            p.sendMessage("Usage: /event <start|settings|info>");
            return true;
        }

        String subCommand = args[0].toLowerCase();
        if(subCommand.equalsIgnoreCase("start")) {
            if(!GameManager.get().state().equals(GameState.WAITING)) {
                p.sendMessage("§b§lOBR §8» §cThe game is already running!");
                return true;
            }
            GameManager.get().start();
            p.sendMessage("§b§lOBR §8» §aThe game has been started!");
            return true;
        } else if(subCommand.equalsIgnoreCase("settings")) {
            if(!GameManager.get().state().equals(GameState.WAITING)) {
                p.sendMessage("§b§lOBR §8» §cYou can only change settings when the game is not running!");
                return true;
            }
            SettingGUI.get(p).open();
            p.sendMessage("§b§lOBR §8» §aSettings menu opened!");
            return true;
        } else if(subCommand.equalsIgnoreCase("info")) {
            p.sendMessage("§b§lOBR §8» §7More information about the event will be displayed soon!");
            return true;
        } else {
            p.sendMessage("§b§lOBR §8» §cUnknown subcommand. Use /event <start|settings|info>");
            return true;
        }

    }
}
