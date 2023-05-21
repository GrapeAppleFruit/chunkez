package busch.chunk.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.UUID;

public class ChunkListener implements Listener {

    private File configFile;
    private FileConfiguration config;
    private int checkerTaskId;

    private final Plugin plugin;

    public ChunkListener(JavaPlugin plugin) {
        this.plugin = plugin;
        configFile = new File(plugin.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = plugin.getConfig();
        Location to = event.getTo();

        // Check if the player has a chunk assigned in the config
        if (config.contains("players." + player.getUniqueId())) {
            // Check if the player is in a valid chunk
            if (!isValidChunk(player, to)) {
                // Check if the player is OP
                if (player.isOp()) {
                    return;
                }
                event.setCancelled(true);
                player.sendMessage(ChatColor.GRAY + "[C]" + " " + ChatColor.RED +"You can't leave your or another player's chunk.");
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();

        // Check if the player is in a valid chunk
        if (!isValidChunk(player, location)) {
            // Check if the player is OP
            if (player.isOp()) {
                return;
            }
            event.setCancelled(true);
            player.sendMessage(ChatColor.GRAY + "[C]" + " " + ChatColor.RED + "You can't break blocks outside your or other players' chunks.");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();

        // Check if the player is in a valid chunk
        if (!isValidChunk(player, location)) {
            // Check if the player is OP
            if (player.isOp()) {
                return;
            }
            event.setCancelled(true);
            player.sendMessage(ChatColor.GRAY + "[C]" + " " + ChatColor.RED + "You can't place blocks outside your or other players' chunks.");
        }
    }

    private boolean isValidChunk(Player player, Location to) {
        UUID uuid = player.getUniqueId();
        FileConfiguration config = plugin.getConfig();

        if (config == null) {
            return false;
        }

        ConfigurationSection playersSection = config.getConfigurationSection("players");

        if (playersSection == null) {
            return false;
        }

        int toChunkX = to.getChunk().getX();
        int toChunkZ = to.getChunk().getZ();

        // Check if the player is in a chunk owned by them
        if (config.getBoolean("players." + uuid + ".started")) {
            int playerChunkX = config.getInt("players." + uuid + ".chunk.x");
            int playerChunkZ = config.getInt("players." + uuid + ".chunk.z");

            if (toChunkX == playerChunkX && toChunkZ == playerChunkZ) {
                return true;
            }
        }

        // Check if the player is in a chunk owned by another player
        for (String key : playersSection.getKeys(false)) {
            UUID otherUuid = UUID.fromString(key);
            if (!otherUuid.equals(uuid) && config.getBoolean("players." + key + ".started")) {
                int otherChunkX = config.getInt("players." + key + ".chunk.x");
                int otherChunkZ = config.getInt("players." + key + ".chunk.z");

                if (toChunkX == otherChunkX && toChunkZ == otherChunkZ) {
                    return true;
                }
            }
        }

        // Player is not in a valid chunk
        return false;
    }
}


