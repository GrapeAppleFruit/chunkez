package busch.chunk;

import busch.chunk.Commands.ChunkCommand;
import busch.chunk.Commands.ReloadConfig;
import busch.chunk.Commands.StartCommand;
import busch.chunk.Listeners.ChunkListener;
import busch.chunk.Listeners.ItemListener;
import busch.chunk.Listeners.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Chunk extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("start").setExecutor(new StartCommand(this));
        getCommand("chunk").setExecutor(new ChunkCommand(this));
        getCommand("rc").setExecutor(new ReloadConfig(this));
        getServer().getPluginManager().registerEvents(new ChunkListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    @Override
    public void onDisable() {
        saveConfig();
    }
}

