package busch.chunk.Listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class PlayerListener implements Listener {

    private final Plugin plugin;

    public PlayerListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = plugin.getConfig();
        UUID playerId = player.getUniqueId();
        boolean started = config.getBoolean("players." + playerId + ".started", false);
        String welcomeMessage;
        if (started) {
            welcomeMessage = ChatColor.GRAY + "[C]" + " " + ChatColor.GREEN + "Welcome back, " + player.getName() + "! You are being teleported to your chunk...";
            player.sendMessage(ChatColor.GREEN + welcomeMessage);
            // Check if the player has a chunk assigned in the config
            if (config.contains("players." + playerId + ".chunk")) {
                int chunkX = config.getInt("players." + playerId + ".chunk.x");
                int chunkZ = config.getInt("players." + playerId + ".chunk.z");
                World world = player.getWorld();
                Chunk chunk = world.getChunkAt(chunkX, chunkZ);
                int highestY = world.getHighestBlockYAt(chunk.getBlock(8, 0, 8).getLocation());
                for (int y = highestY; y >= 0; y--) {
                    Block block = chunk.getBlock(8, y, 8);
                    if (!block.getType().isAir()) {
                        Location location = new Location(world, block.getX() + 0.5, block.getY() + 1.0, block.getZ() + 0.5);
                        player.teleport(location);
                        player.sendMessage(ChatColor.GRAY + "[C]" + " " + ChatColor.GREEN + "Automatically teleported you to your chunk.");
                        return;
                    }
                }
            }
        } else {
            welcomeMessage = ChatColor.GRAY + "[C]" + " " + ChatColor.GREEN + "Welcome, " + player.getName() + "! Do /start to get your chunk!";
            player.sendMessage(ChatColor.GREEN + welcomeMessage);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = plugin.getConfig();
        UUID playerId = player.getUniqueId();
        // Check if the player has a chunk assigned in the config
        if (config.contains("players." + playerId + ".chunk")) {
            int chunkX = config.getInt("players." + playerId + ".chunk.x");
            int chunkZ = config.getInt("players." + playerId + ".chunk.z");
            int highestY = config.getInt("players." + playerId + ".chunk.highestY");
            int centerX = config.getInt("players." + playerId + ".chunk.center.x");
            int centerY = config.getInt("players." + playerId + ".chunk.center.y");
            int centerZ = config.getInt("players." + playerId + ".chunk.center.z");
            World world = player.getWorld();
            Location centerLocation = new Location(world, centerX, centerY, centerZ);
            Chunk chunk = world.getChunkAt(chunkX, chunkZ);
            int highestYInCenter = world.getHighestBlockYAt(chunkX * 16 + 8, chunkZ * 16 + 8);
            Location highestBlockLocation = new Location(world, centerX, highestYInCenter, centerZ);
            // Teleport the player to the center of the chunk
            player.teleport(centerLocation);
            // Teleport the player to the highest non-air block in the center of the chunk
            Bukkit.getScheduler().runTaskLater(plugin, () -> player.teleport(highestBlockLocation), 1); // Delay teleportation by 1 tick
            player.sendMessage(ChatColor.GRAY + "[C]" + " " + ChatColor.GREEN + "You have been automatically teleported to your chunk.");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = plugin.getConfig();
        if (!config.contains("players." + player.getUniqueId())) {
            // Player does not have a chunk assigned in the config, prevent block break
            event.setCancelled(true);
            player.sendMessage(ChatColor.GRAY + "[C]" + " " + ChatColor.RED + "You can't break blocks without a chunk assigned in the config. Use /start.");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = plugin.getConfig();
        if (!config.contains("players." + player.getUniqueId())) {
            // Player does not have a chunk assigned in the config, prevent block place
            event.setCancelled(true);
            player.sendMessage(ChatColor.GRAY + "[C]" + " " + ChatColor.RED + "You can't break blocks without a chunk assigned in the config. Use /start.");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = plugin.getConfig();
        if (!config.contains("players." + player.getUniqueId())) {
            // Player does not have a chunk assigned in the config, prevent interaction
            event.setCancelled(true);
            player.sendMessage(ChatColor.GRAY + "[C]" + " " + ChatColor.RED + "You can't interact with items without a chunk assigned in the config. Use /start.");
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = plugin.getConfig();
        if (!config.contains("players." + player.getUniqueId())) {
            // Player does not have a chunk assigned in the config, prevent item pickup
            event.setCancelled(true);
            player.sendMessage(ChatColor.GRAY + "[C]" + " " + ChatColor.RED + "You can't pick-up items without a chunk assigned in the config. Use /start.");
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            FileConfiguration config = plugin.getConfig();
            if (!config.contains("players." + player.getUniqueId())) {
                // Player does not have a chunk assigned in the config, prevent damage
                event.setCancelled(true);
            }
        }
    }
}
