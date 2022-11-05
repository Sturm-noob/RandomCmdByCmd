package me.sturm.rcbc.subtypes;

import me.sturm.rcbc.RandomEx;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RandomCmd extends Command implements PluginIdentifiableCommand {

    private Plugin pl;
    private RandomEx ex;

    protected RandomCmd(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public Plugin getPlugin() {
        return pl;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return ex.onCommand(sender, this, commandLabel, args);
    }

    public static List<RandomCmd> parseCommands(Plugin pl, boolean isPAPI) {
        pl.reloadConfig();
        List<RandomCmd> result = new ArrayList<>();
        Configuration cfg = pl.getConfig();
        for (String cmdName : cfg.getKeys(false)) {
            RandomCmd cmd = new RandomCmd(cmdName, "", "/"+cmdName, new ArrayList<>());
            Map<Integer, RandomSubcmd> cmds = new HashMap<>();
            for (String indexName : cfg.getConfigurationSection(cmdName).getKeys(false)) {
                int index = parseNonNegInt(indexName);
                RandomSubcmd subcmd = new RandomSubcmd();
                for (String condition : cfg.getConfigurationSection(cmdName+"."+indexName).getKeys(false)) {
                    RandomConditionBlock conditionBlock = new RandomConditionBlock();
                    ConfigurationSection sec = cfg.getConfigurationSection(cmdName+"."+indexName+"."+condition);
                    for (String executableCmd : sec.getKeys(false)) {
                        ExecutionBlock block = new ExecutionBlock(executableCmd,
                                sec.getString(executableCmd+".message"), sec.getBoolean(executableCmd+".console"));
                        sec.getStringList(executableCmd+".other").forEach(block::addOtherCmd);
                        conditionBlock.addExecutionBlock(sec.getDouble(executableCmd+".weight"), block);
                    }
                    subcmd.addConditionBlock(condition, conditionBlock);
                }
                cmds.put(index, subcmd);
            }
            RandomEx ex = new RandomEx(cmdName, isPAPI, cmds);
            cmd.ex = ex;
            result.add(cmd);
        }
        return result;
    }

    public void unload() throws Exception {
        String name = this.getName();
        Map<String, Command> command = Bukkit.getCommandMap().getKnownCommands();
        Command cmd = command.get(name);
        InputStream stream = cmd.getClass().getResourceAsStream("/plugin.yml");
        PluginDescriptionFile desc = new PluginDescriptionFile(stream);
        command.remove(cmd.getName());
        command.remove(desc.getName().toLowerCase() + ":" + cmd.getName());
        for (String aliases : cmd.getAliases()) {
            command.remove(aliases);
            command.remove(desc.getName().toLowerCase() + ":" + aliases);
        }
    }

    public static int parseNonNegInt(String s) {
        try {
            int i = Integer.parseInt(s);
            return i >= 0 ? i : -i;
        }
        catch (Exception e) {return 0;}
    }
}
