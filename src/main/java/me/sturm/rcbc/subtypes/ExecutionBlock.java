package me.sturm.rcbc.subtypes;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ExecutionBlock {

    private String message = "";
    private boolean isConsole = true;
    private String command;
    private List<String> otherCmds = new ArrayList<>();

    public ExecutionBlock(String command, String message, boolean isConsole) {
        this.command = command;
        this.isConsole = isConsole;
        this.message = message;
    }

    public void addOtherCmd(String cmd) {
        this.otherCmds.add(cmd);
    }

    public void execute(Player p, boolean isPAPI, String[] args) {
        String finalCmd = command;
        String finalMessage = message;
        for (int i = 1; i <= args.length; i++) {
            finalCmd = finalCmd.replace("%arg"+i+"%", args[i-1]);
            if (finalMessage != null) finalMessage = finalMessage.replace("%arg"+i+"%", args[i-1]);
        }
        Location loc = p.getLocation();
        finalCmd = finalCmd.replace("%player%", p.getName()).replace("%x%", loc.getBlockX()+"")
                .replace("%y%", loc.getBlockY()+"").replace("%z%", loc.getBlockZ()+"")
                .replace("%world%", loc.getWorld().getName()).replace("%level%", p.getLevel()+"")
                .replace("%health%", p.getHealth()+"").replace("%food%", p.getFoodLevel()+"");
        if (finalMessage != null) finalMessage = finalMessage.replace("%player%", p.getName()).replace("%x%", loc.getBlockX()+"")
                .replace("%y%", loc.getBlockY()+"").replace("%z%", loc.getBlockZ()+"")
                .replace("%world%", loc.getWorld().getName()).replace("%level%", p.getLevel()+"")
                .replace("%health%", p.getHealth()+"").replace("%food%", p.getFoodLevel()+"");
        if (isPAPI) {
            finalCmd = PlaceholderAPI.setPlaceholders(p, finalCmd);
            if (finalMessage != null) finalMessage = PlaceholderAPI.setPlaceholders(p, finalMessage);
        }
        if (!finalCmd.startsWith("null")) Bukkit.dispatchCommand(isConsole ? Bukkit.getConsoleSender() : p, finalCmd);
        if (finalMessage != null) p.sendMessage(ChatColor.translateAlternateColorCodes('&', finalMessage));

        for (String x : otherCmds) {
            if (isPAPI) x = PlaceholderAPI.setPlaceholders(p, x);
            for (int i = 1; i <= args.length; i++) {
                x = x.replace("%arg"+i+"%", args[i-1]);
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), x.replace("%player%", p.getName()).replace("%x%", loc.getBlockX()+"")
                    .replace("%y%", loc.getBlockY()+"").replace("%z%", loc.getBlockZ()+"")
                    .replace("%world%", loc.getWorld().getName()).replace("%level%", p.getLevel()+"")
                    .replace("%health%", p.getHealth()+"").replace("%food%", p.getFoodLevel()+""));
        }
    }

}
