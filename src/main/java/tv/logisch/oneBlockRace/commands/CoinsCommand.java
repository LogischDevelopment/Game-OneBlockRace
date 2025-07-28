package tv.logisch.oneBlockRace.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import tv.logisch.oneBlockRace.manager.GameManager;

public class CoinsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if(!(sender instanceof org.bukkit.entity.Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if(!player.hasPermission("logisch.oneblockrace.admin") && !GameManager.get().isHost(player.getUniqueId())) {
            player.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }

        if(args.length < 2) {
            player.sendMessage("§cUsage: /coins <player> <amount>");
        }

        String playerName = args[0];
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid amount. Please enter a valid number.");
            return true;
        }
        org.bukkit.entity.Player targetPlayer = org.bukkit.Bukkit.getPlayer(playerName);
        if(targetPlayer == null) {
            player.sendMessage("§cPlayer not found.");
            return true;
        }
        GameManager.get().teamManager().getTeam(targetPlayer).coins(amount);
        player.sendMessage("§aSuccessfully added §f" + amount + " §acoins to §f" + targetPlayer.getName() + "§a's account.");
        return true;

    }
}
