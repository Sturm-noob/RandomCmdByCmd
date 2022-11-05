package me.sturm.rcbc;

import me.sturm.rcbc.subtypes.RandomCmd;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class CmdManager implements CommandExecutor, TabCompleter {

    private Plugin plugin;
    private List<RandomCmd> cmds = new ArrayList<>();

    public CmdManager(Plugin pl) {
        this.plugin = pl;
    }

    public void load(boolean isPAPI) {
        cmds = RandomCmd.parseCommands(plugin, isPAPI);
        new BukkitRunnable() {

            @Override
            public void run() {
                cmds.forEach(x -> Bukkit.getCommandMap().register(plugin.getName(), x));
            }
        }.runTaskLater(plugin, 20);

    }

    public void unload() {
        cmds.forEach(x -> {
            try {
                x.unload();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("randomcmdbycmd.reload")) return !Lang.NO_PERMS.sendMessage(sender);
        new BukkitRunnable() {

            @Override
            public void run() {
                unload();
                plugin.reloadConfig();
                boolean isPAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
                load(isPAPI);
                Lang.init(plugin);
                Lang.PLUGIN_RELOAD.sendMessage(sender);
            }

        }.runTaskAsynchronously(plugin);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1 && sender.hasPermission("randomcmdbycmd.reload")) result.add("reload");
        return result;
    }
}
