package tv.logisch.oneBlockRace.commands.completions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventCompletion implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if(args.length == 0) {
            return List.of("start", "settings", "info");
        }
        if(args.length == 1) {
            return Stream.of("start", "settings", "info").filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        }

        return List.of();

    }
}
