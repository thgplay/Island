package com.tke.island.task;

import com.tke.island.SkyBlock;
import com.tke.island.api.Cuboid;
import com.tke.island.api.GlueList;
import com.tke.island.data.Island;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.stream.Collectors;

public class IslandRemove extends BukkitRunnable {

    Island island;
    GlueList<Block> blocks;


    public IslandRemove(Island island){
        this.island = island;
        this.blocks = new GlueList<>();

        GlueList<Block> b = new Cuboid(island.getMinimumLocation(), island.getMaximumLocation())
                .getBlocks(true).stream()
                .filter(block -> block != null && block.getType() != Material.AIR).collect(Collectors.toCollection(GlueList::new));

        this.blocks.addAll(b);
        this.runTaskTimer(SkyBlock.getInstance(), 0L, 10L);
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 50; i++){
            if (this.blocks.isEmpty()) {
                this.cancel();
                return;
            }
            Block block = this.blocks.get(0);
            if (block != null){
                block.setType(Material.AIR);
                block.getState().update(true);
                blocks.remove(0);
            }
        }
        System.out.println((System.currentTimeMillis() - start) + "ms");
    }
}
