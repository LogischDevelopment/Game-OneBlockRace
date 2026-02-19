package tv.logisch.game.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.Nullable;
import tv.logisch.game.OneBlockRace;
import tv.logisch.game.manager.GameManager;

import java.util.Collection;
import java.util.List;

public class CoinsCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack ctx, String[] args) {
        CommandSender sender = ctx.getExecutor();
        if(!(sender instanceof org.bukkit.entity.Player player)) {
            if(sender == null) return;
            sender.sendMessage(OneBlockRace.instance().prefix()+"§cThis command can only be used by players.");
            return;
        }

        if(!player.hasPermission("logisch.oneblockrace.admin") && !GameManager.get().isHost(player.getUniqueId())) {
            player.sendMessage(OneBlockRace.instance().prefix()+"§cYou do not have permission to use this command.");
            return;
        }

        if(args.length < 2) {
            player.sendMessage(OneBlockRace.instance().prefix()+"§cUsage: /coins <player> <amount>");
            return;
        }

        String playerName = args[0];
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(OneBlockRace.instance().prefix()+"§cInvalid amount. Please enter a valid number.");
            return;
        }
        org.bukkit.entity.Player targetPlayer = org.bukkit.Bukkit.getPlayer(playerName);
        if(targetPlayer == null) {
            player.sendMessage(OneBlockRace.instance().prefix()+"§cPlayer not found.");
            return;
        }
        GameManager.get().teamManager().getTeam(targetPlayer).coins(amount);
        player.sendMessage(OneBlockRace.instance().prefix()+"§aSuccessfully added §f" + amount + " §acoins to §f" + targetPlayer.getName() + "§a's account.");
        return;
    }

    @Override
    public Collection<String> suggest(CommandSourceStack ctx, String[] args) {
        return List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        if(!(sender instanceof org.bukkit.entity.Player player)) return false;
        return player.hasPermission("logisch.oneblockrace.admin") || GameManager.get().isHost(player.getUniqueId());
    }

    @Override
    public @Nullable String permission() {
        return "logisch.oneblockrace.admin";
    }
}
