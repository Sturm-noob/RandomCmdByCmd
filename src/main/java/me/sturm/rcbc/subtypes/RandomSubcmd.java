package me.sturm.rcbc.subtypes;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RandomSubcmd {

    private Map<String, RandomConditionBlock> blocks = new HashMap<>();

    public void addConditionBlock(String condition, RandomConditionBlock block) {
        blocks.put(condition, block);
    }

    public boolean isCondition(Player p, String cond, boolean isPAPI, String[] args) {
        cond = putArgs(cond, args);
        Location loc = p.getLocation();
        cond = cond.replace("%player%", p.getName()).replace("%x%", loc.getBlockX()+"")
                .replace("%y%", loc.getBlockY()+"").replace("%z%", loc.getBlockZ()+"")
                .replace("%world%", loc.getWorld().getName()).replace("%level%", p.getLevel()+"")
                .replace("%health%", p.getHealth()+"").replace("%food%", p.getFoodLevel()+"");
        if (isPAPI) cond = PlaceholderAPI.setPlaceholders(p, cond);
        return math(cond);
    }

    public String putArgs(String s, String[] args) {
        for (int i = 1; i <= args.length; i++) s = s.replace("%arg"+i+"%", args[i-1]);
        return s;
    }

    public boolean execute(Player p, boolean isPAPI, String[] args) {
        boolean res = false;
        for (Map.Entry<String, RandomConditionBlock> e : blocks.entrySet()) {
            if (isCondition(p, e.getKey(), isPAPI, args)) {
                res = true;
                e.getValue().execute(p, isPAPI, args);
            }
        }
        return res;
     }

    private boolean math(String cmd) {
        cmd = cmd.trim();
        if (cmd.contains("||")) {
            String[] cmds = cmd.split("\\|\\|");
            for (String otherCmd : cmds)
                if (math(otherCmd)) return true;
            return false;
        }
        if (cmd.contains("&&")) {
            String[] cmds = cmd.split("&&");
            for (String otherCmd : cmds)
                if (!math(otherCmd)) return false;
            return true;
        }
        if (cmd.length() > 0 && cmd.charAt(0) == '!') {
            return !math(cmd.substring(1));
        }
        char ch = '=';
        if (cmd.contains(">")) ch = '>';
        else if (cmd.contains("<")) ch = '<';
        String[] y = cmd.split(String.valueOf(ch));
        if (y.length != 2) {
            Bukkit.getLogger().info("Ошибка в условии: " + cmd);
            Bukkit.getLogger().info("Количество аргументов должно быть равно двум.");
            return true;
        }
        if (!isDouble(y[0].trim()) || !isDouble(y[1].trim())) {
            y[0] = y[0].trim();
            y[1] = y[1].trim();
            if (y[1].equals("int")) return isInt(y[0]);
            if (y[1].equals("double")) return isDouble(y[0]);
            if (ch == '=') return y[0].equals(y[1]);
            if (ch == '>') return y[0].compareTo(y[1]) > 0;
            if (ch == '<') return y[1].compareTo(y[0]) > 0;
        }
        else {
            double[] z = new double[]{Double.parseDouble(y[0]), Double.parseDouble(y[1])};
            if (ch == '=') return z[0] == z[1];
            if (ch == '>') return z[0] > z[1];
            if (ch == '<') return z[0] < z[1];
        }
        return false;
    }

    private boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
        }
        catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        }
        catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

}
