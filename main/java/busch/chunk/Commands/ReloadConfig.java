package busch.chunk.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ReloadConfig implements CommandExecutor {

    private final JavaPlugin plugin;

    public ReloadConfig(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("rc")) {
            if (!sender.hasPermission("chunk.config")) {
                sender.sendMessage(ChatColor.GRAY + "[C] " + ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }

            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GRAY + "[C] " + ChatColor.GREEN + "Config successfully reloaded.");

            return true;
        }
        return false;
    }
}
