package me.sturm.rcbc;

import me.sturm.rcbc.subtypes.RandomSubcmd;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class RandomEx implements CommandExecutor {

    private boolean isPAPI;
    private Map<Integer, RandomSubcmd> cmds;
    private String name;

    public RandomEx(String name, boolean isPAPI, Map<Integer, RandomSubcmd> map) {
        this.name = name;
        this.isPAPI = isPAPI;
        this.cmds = map;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only player command");
            return false;
        }
        if (!sender.hasPermission("rcbc."+this.name)) return Lang.NO_PERMS.sendMessage(sender);
        Player player = (Player) sender;
        RandomSubcmd cmd = cmds.get(args.length);
        if (cmd == null) return Lang.ERROR_ARGS.sendMessage(sender, "%args%", String.valueOf(args.length));
        if (!cmd.execute(player, isPAPI, args)) return Lang.NO_RESULT.sendMessage(sender);
        return true;
    }
}
