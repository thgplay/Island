package com.tke.island.api;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.util.List;

public class WorldEditAPI {

    @Getter
    private static WorldEditAPI instance;

    @Getter
    private WorldEditPlugin worldEdit;

    public WorldEditAPI(){
        instance = this;
        hook();
    }

    private void hook(){
        try {
            worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        }catch (Exception e){
            Bukkit.getServer().shutdown();
        }
    }

    public BukkitPlayer getPlayer(Player player){
        return worldEdit.wrapPlayer(player);
    }

}
