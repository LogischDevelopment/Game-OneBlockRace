package tv.logisch.game.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import tv.logisch.game.OneBlockRace;
import tv.logisch.game.enums.GameState;
import tv.logisch.game.manager.GameManager;

import java.util.Collection;
import java.util.List;

public class SkipCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack ctx, String[] args) {

        CommandSender sender = ctx.getExecutor();

        if(!(sender instanceof Player p)) {
            if(sender == null) return;
            sender.sendMessage(OneBlockRace.instance().prefix()+"§cThis command can only be used by players.");
            return;
        }

        if(!GameManager.get().state().equals(GameState.SHOPPING)) {
            if(GameManager.get().state().equals(GameState.RUNNING) && p.hasPermission("logisch.oneblockrace.admin")) {
                if(GameManager.get().timeLeft() <= 30) {
                    p.sendMessage(Component.text(OneBlockRace.instance().prefix()+"§cYou can only vote to skip shopping if there are 30 seconds or less left."));
                    return;
                }
                GameManager.get().timeLeft(30);
                return;
            }
            p.sendMessage(Component.text(OneBlockRace.instance().prefix()+"§cYou can only vote to skip shopping during the shopping phase."));
            return;
        }

        boolean success = GameManager.get().addSkippingVote(p);
        if(!success) {
            p.sendMessage(Component.text(OneBlockRace.instance().prefix()+"§cYou have already voted to skip shopping or the voting period has ended."));
            return;
        }

    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack ctx, String @NotNull [] args) {
        return List.of();
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player player;
    }

    @Override
    public @Nullable String permission() {
        return null;
    }
}
