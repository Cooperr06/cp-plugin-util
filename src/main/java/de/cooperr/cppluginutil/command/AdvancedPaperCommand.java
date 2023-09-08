package de.cooperr.cppluginutil.command;

import de.cooperr.cppluginutil.base.PaperPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class AdvancedPaperCommand<T extends PaperPlugin> extends PaperCommand<T> implements TabExecutor {

    private final String name;
    private final BiConsumer<CommandSender, String[]> run;

    private final List<AdvancedPaperCommand<T>> subCommands;

    private final String permission;
    private final boolean allowConsole;

    public AdvancedPaperCommand(@NotNull T plugin, @NotNull String name, @NotNull BiConsumer<CommandSender, String[]> run,
                                @NotNull List<AdvancedPaperCommand<T>> subCommands, @NotNull String permission, boolean allowConsole) {
        super(plugin);

        this.name = name;
        this.run = run;
        this.subCommands = subCommands;
        this.permission = permission;
        this.allowConsole = allowConsole;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!allowConsole && !(sender instanceof Player)) {
            sendWrongSenderMessage(sender);
            return true;
        }

        var player = (Player) sender;

        if (!player.hasPermission(permission)) {
            sendNoPermissionMessage(player);
            return true;
        }

        // run this command if there are no possible subcommands
        if (args.length == 0) {
            run.accept(sender, args);
            return true;
        }

        var currentCommand = this;
        for (var i = 0; i < args.length; i++) {
            if (!currentCommand.subCommands().isEmpty()) {
                var finalI = i; // required because of access of non-final variable in lambda expression
                var subCommandOptional = currentCommand.subCommands().stream()
                        .filter(subCommand -> subCommand.name().equals(args[finalI]))
                        .findFirst();
                if (subCommandOptional.isEmpty()) { // if there is no subcommand for the current argument, the command is being executed with the args
                    currentCommand.run().accept(sender, args);
                    return true;
                }
                currentCommand = subCommandOptional.get(); // update the current command so the subcommands of the next command are compared with the args
            } else {
                currentCommand.run().accept(sender, args); // if there are no subcommands left, execute the current command with the args
                return true;
            }
        }
        return true;
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null; // TODO
    }

    @Override
    @NotNull
    public String name() {
        return name;
    }

    @NotNull
    public BiConsumer<CommandSender, String[]> run() {
        return run;
    }

    public List<AdvancedPaperCommand<T>> subCommands() {
        return subCommands;
    }

    public String permission() {
        return permission;
    }

    public boolean allowConsole() {
        return allowConsole;
    }

    static class Builder<T extends PaperPlugin> {

        private final T plugin;

        private final String name;
        private final BiConsumer<CommandSender, String[]> run;

        private final List<AdvancedPaperCommand<T>> subCommands = new ArrayList<>();

        private String permission;
        private boolean allowConsole;

        public Builder(T plugin, String name, BiConsumer<CommandSender, String[]> run) {
            this.plugin = plugin;
            this.name = name;
            this.run = run;
            this.allowConsole = false;
        }

        public Builder<T> subCommand(AdvancedPaperCommand<T> subCommand) {
            subCommands.add(subCommand);
            return this;
        }

        public Builder<T> permission(String permission) {
            this.permission = permission;
            return this;
        }

        public Builder<T> allowConsole() {
            this.allowConsole = true;
            return this;
        }

        public PaperCommand<T> build() {
            return new AdvancedPaperCommand<>(plugin, name, run, subCommands, permission, allowConsole);
        }
    }
}
