package me.sturm.rcbc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class RandomCmdByCmdPlugin extends JavaPlugin {

    private CmdManager manager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Lang.init(this);
        boolean isPAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        manager = new CmdManager(this);
        manager.load(isPAPI);
        getCommand("randomcmdbycmd").setExecutor(manager);
        getCommand("randomcmdbycmd").setTabCompleter(manager);
    }

    @Override
    public void onDisable() {
        manager.unload();
    }
}
