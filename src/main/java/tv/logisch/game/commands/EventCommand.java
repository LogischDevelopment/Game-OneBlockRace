package tv.logisch.game.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import tv.logisch.game.enums.GameState;
import tv.logisch.game.gui.setting.DurationGUI;
import tv.logisch.game.manager.GameManager;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class EventCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack ctx, String[] args) {

        CommandSender sender = ctx.getExecutor();
        if(!(sender instanceof org.bukkit.entity.Player p)) {
            if(sender == null) return;
            sender.sendMessage("This command can only be used by players.");
            return;
        }

        if(!p.hasPermission("logisch.event.admin") && !GameManager.get().isHost(p.getUniqueId())) {
            p.sendMessage("You do not have permission to use this command.");
            return;
        }

        if(args.length == 0) {
            p.sendMessage("Usage: /event <start|settings|info>");
            return;
        }

        String subCommand = args[0].toLowerCase();
        if(subCommand.equalsIgnoreCase("start")) {
            if(!GameManager.get().state().equals(GameState.WAITING)) {
                p.sendMessage("§b§lOBR §8» §cThe game is already running!");
                return;
            }
            GameManager.get().start();
            p.sendMessage("§b§lOBR §8» §aThe game has been started!");
            return;
        } else if(subCommand.equalsIgnoreCase("settings")) {
            DurationGUI.get(p).open();
            p.sendMessage("§b§lOBR §8» §aSettings menu opened!");
            return;
        } else if(subCommand.equalsIgnoreCase("info")) {
            p.sendMessage("§b§lOBR §8» §7More information about the event will be displayed soon!");
            return;
        } else {
            p.sendMessage("§b§lOBR §8» §cUnknown subcommand. Use /event <start|settings|info>");
            return;
        }

    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack ctx, String[] args) {
        if (args.length == 0) {
            return List.of("start", "settings", "info");
        }
        if(args.length == 1) {
            return Stream.of("start", "settings", "info")
                    .filter(option -> option.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return List.of();
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        if(!(sender instanceof org.bukkit.entity.Player player)) return false;
        return player.hasPermission("logisch.event.admin") || GameManager.get().isHost(player.getUniqueId());
    }

    @Override
    public @Nullable String permission() {
        return "logisch.event.admin";
    }
}
