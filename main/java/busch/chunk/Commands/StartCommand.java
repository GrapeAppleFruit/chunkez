package busch.chunk.Commands;

import org.bukkit.Chunk;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class StartCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public StartCommand(JavaPlugin  plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("start")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                UUID uuid = player.getUniqueId();
                FileConfiguration config = plugin.getConfig();

                // Check if the player has already started
                if (config.getBoolean("players." + uuid + ".started")) {
                    player.sendMessage(ChatColor.GRAY + "[C]" + " " + ChatColor.RED + "You already have a chunk.");
                    return true;
                }

                // Generate a random chunk
                Chunk chunk = findSuitableChunk(player.getWorld(), config);

                if (chunk == null) {
                    player.sendMessage(ChatColor.GRAY + "[C] " + ChatColor.RED + "Couldn't find a suitable chunk,contact an admin.");
                    return true;
                }

                int centerX = chunk.getX() * 16 + 8;
                int centerZ = chunk.getZ() * 16 + 8;
                int highestYInCenter = player.getWorld().getHighestBlockYAt(chunk.getBlock(8, 0, 8).getLocation());

                // Save the chunk coordinates to the config
                config.set("players." + uuid + ".chunk.x", chunk.getX());
                config.set("players." + uuid + ".chunk.z", chunk.getZ());

                // Save the center and highest block coordinates of the chunk
                config.set("players." + uuid + ".chunk.center.x", centerX);
                config.set("players." + uuid + ".chunk.center.y", highestYInCenter);
                config.set("players." + uuid + ".chunk.center.z", centerZ);
                config.set("players." + uuid + ".chunk.highestY", highestYInCenter);

                // Set the player's "started" status to true
                config.set("players." + uuid + ".started", true);

                // Save the config file
                plugin.saveConfig();

                // Teleport the player to the center of the chunk
                Location location = new Location(player.getWorld(), centerX, highestYInCenter, centerZ);
                player.teleport(location);

                player.sendMessage(ChatColor.GRAY + "[C]" + " " + ChatColor.GREEN + "Teleported you to your chunk.");

                return true;
            } else {
                plugin.getLogger().warning("The /start command can only be used by players.");
            }
        }
        return false;
    }

    private Chunk findSuitableChunk(World world, FileConfiguration config) {
        int maxAttempts = 100; // Maximum number of attempts to find a suitable chunk
        int attempts = 0;

        while (attempts < maxAttempts) {
            int randomX = getRandomCoordinate();
            int randomZ = getRandomCoordinate();

            org.bukkit.Chunk chunk = world.getChunkAt(randomX, randomZ);

            if (!isChunkUsed(chunk, config)) {
                boolean hasWater = false;

                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        int highestY = world.getHighestBlockYAt(chunk.getBlock(x, 0, z).getLocation());

                        for (int y = highestY; y >= 0; y--) {
                            Material blockMaterial = world.getBlockAt(chunk.getBlock(x, y, z).getLocation()).getType();

                            if (blockMaterial == Material.WATER || blockMaterial == Material.LEGACY_STATIONARY_WATER) {
                                if (y > 55) { // Check if the water block is above Y level 55
                                    hasWater = true;
                                    break;
                                }
                            }
                        }

                        if (hasWater) {
                            break;
                        }
                    }

                    if (hasWater) {
                        break;
                    }
                }

                if (!hasWater) {
                    return chunk;
                }
            }

            attempts++;
        }

        return null;
    }

    private int getRandomCoordinate() {
        // Generate a random coordinate within a reasonable range
        return ThreadLocalRandom.current().nextInt(-500000, 500000);
    }

    private boolean isChunkUsed(Chunk chunk, FileConfiguration config) {
        if (!config.contains("players")) {
            return false;
        }

        for (String key : config.getConfigurationSection("players").getKeys(false)) {
            int x = config.getInt("players." + key + ".chunk.x");
            int z = config.getInt("players." + key + ".chunk.z");
            if (chunk.getX() == x && chunk.getZ() == z) {
                return true;
            }
        }
        return false;
    }
}
