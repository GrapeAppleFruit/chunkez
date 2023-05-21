package busch.chunk.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.UUID;

public class ItemListener implements Listener {

    private final org.bukkit.plugin.Plugin plugin;

    public ItemListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            if (!isValidChunk(player, to)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.GRAY + "[C]" + " " + ChatColor.RED +"You can't throw enderpearls outside your chunk.");
            }
        }
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        // Check if the portal was created by a player
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (entity instanceof AbstractHorse) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GRAY + "[C]" + " " + ChatColor.RED +"Riding horses is disabled on this server.");
        }
        if (entity instanceof Pig) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GRAY + "[C]" + " " + ChatColor.RED +"Riding pigs is disabled on this server.");
        }
        if (entity instanceof Donkey) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GRAY + "[C]" + " " + ChatColor.RED +"Riding donkeys is disabled on this server.");
        }
        if (entity instanceof Llama) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GRAY + "[C]" + " " + ChatColor.RED +"Riding llamas is disabled on this server.");
        }
        if (entity instanceof Mule) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GRAY + "[C]" + " " + ChatColor.RED +"Riding mules is disabled on this server.");
        }
        if (entity instanceof SkeletonHorse) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GRAY + "[C]" + " " + ChatColor.RED +"Riding skeleton horses is disabled on this server.");
        }
        if (entity instanceof Strider) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GRAY + "[C]" + " " + ChatColor.RED +"Riding striders is disabled on this server.");
        }
        if (entity instanceof Minecart) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GRAY + "[C]" + " " + ChatColor.RED +"Riding minecarts is disabled on this server.");
        }
        if (entity instanceof Boat) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GRAY + "[C]" + " " + ChatColor.RED +"Riding boats is disabled on this server.");
        }
    }

    private boolean isValidChunk(Player player, Location to) {
        UUID uuid = player.getUniqueId();
        FileConfiguration config = plugin.getConfig();

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
        for (String key : config.getConfigurationSection("players").getKeys(false)) {
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
