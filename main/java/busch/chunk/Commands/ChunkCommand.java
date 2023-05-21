package busch.chunk.Commands;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ChunkCommand implements CommandExecutor {

    private final org.bukkit.plugin.Plugin plugin;

    public ChunkCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        FileConfiguration config = plugin.getConfig();

        // Check if the player has started
        if (!config.getBoolean("players." + uuid + ".started")) {
            player.sendMessage(ChatColor.GRAY + "[C] " + ChatColor.RED + "Do /start before you can use this command.");
            return true;
        }

        int chunkX = config.getInt("players." + uuid + ".chunk.x");
        int chunkZ = config.getInt("players." + uuid + ".chunk.z");
        World world = player.getWorld();
        Chunk chunk = world.getChunkAt(chunkX, chunkZ);
        int centerX = chunkX * 16 + 8;
        int centerZ = chunkZ * 16 + 8;
        int highestY = world.getHighestBlockYAt(chunk.getBlock(8, 0, 8).getLocation());
        for (int y = highestY; y >= 0; y--) {
            Block block = chunk.getBlock(8, y, 8);
            if (!block.getType().isAir()) {
                Location location = new Location(world, centerX, block.getY() + 1, centerZ);
                player.teleport(location);
                player.sendMessage(ChatColor.GRAY + "[C]" + " " + ChatColor.GREEN + "Teleported you to your chunk.");
                break;
            }
        }

        return true;
    }
}
