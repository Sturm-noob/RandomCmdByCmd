package me.sturm.rcbc.subtypes;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RandomConditionBlock {

    private Map<ExecutionBlock, Double> rands = new HashMap<>();
    private double sum = 0;

    public void addExecutionBlock(double weight, ExecutionBlock block) {
        sum += weight;
        rands.put(block, weight);
    }

    public void execute(Player p, boolean isPAPI, String[] args) {
        double res = Math.random() * sum;
        for (Map.Entry<ExecutionBlock, Double> entry : rands.entrySet()) {
            res -= entry.getValue();
            if (res <= 0) {
                entry.getKey().execute(p, isPAPI, args);
                break;
            }
        }
    }
}
