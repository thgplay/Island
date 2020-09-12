package com.tke.island.controller;

import com.google.common.collect.Maps;
import com.tke.island.SkyBlock;
import com.tke.island.api.Config;
import com.tke.island.data.Island;
import com.tke.island.data.Role;
import com.tke.island.sql.Database;
import com.tke.island.task.IslandRemove;
import com.tke.island.util.IslandMessages;
import com.tke.island.util.IslandUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class IslandController {

    @Getter
    private static IslandController instance;

    @Getter
    private Map<UUID, Island> cache,index;
    private Location lastLocationIsland;

    public IslandController(){
        instance = this;
        this.index = Maps.newHashMap();
        this.cache = Maps.newHashMap();
        this.lastLocationIsland = IslandUtil.unserializer(Objects.requireNonNull(SkyBlock.getInstance().getConfig().getString("LastLocation")));
    }

    public void create(Player player){
        Location center;
        if (index.containsKey(player.getUniqueId())){
            player.sendMessage(IslandMessages.ISLAND_LIMIT_PER_PLAYER_EXCEEDED.getMessage());
            return;
        }

        center = getNextLocationAvailable();

        if (center == null){
            player.sendMessage(IslandMessages.ISLAND_LIMIT_EXCEEDED.getMessage());
            return;
        }

        IslandUtil.paste(center, SkyBlock.getInstance().getSchematic());
        Island island = new Island(player.getUniqueId(), center);
        island.spawnNPCs();
        this.index.put(player.getUniqueId(), island);
        Database.getInstance().create(island);


        try {
            if (!center.getChunk().isLoaded())
                center.getChunk().load();

            player.teleport(center);
        }catch (Exception ignored){}

        lastLocationIsland = center.clone();
    }

    public void delete(Player player, Island island){
        Role role = island.getRole(player.getUniqueId());
        if (role != Role.Owner && !player.isOp()){
            player.sendMessage(IslandMessages.NOT_PERMISSION.getMessage());
            return;
        }
        island.despawnNPCs();
        this.index.remove(player.getUniqueId());
        this.cache.remove(player.getUniqueId());
        player.sendMessage(IslandMessages.ISLAND_DELETE.getMessage());
        Database.getInstance().delete(island);

        new IslandRemove(island);
//        island.clearTerrain();
    }

    public void unloadAll(){
        Config config = new Config("config.yml");
        config.setString("LastLocation", IslandUtil.serializer(lastLocationIsland));
        config.saveConfig();
        this.getIndex().values().forEach(Island::despawnNPCs);
    }

    public Location getNextLocationAvailable(){
        if (IslandUtil.getWorld() == null) return null;
        if (lastLocationIsland == null) {
            lastLocationIsland = new Location(IslandUtil.getWorld(), IslandUtil.getStartX(), IslandUtil.getY(), IslandUtil.getStartZ());
            return lastLocationIsland;
        }
        double x,z;
        x = lastLocationIsland.getX() + IslandUtil.getOffSet();
        z = lastLocationIsland.getZ();
        if (x > IslandUtil.getEndX()){
            x = IslandUtil.getStartX();
            z = lastLocationIsland.getZ() + IslandUtil.getOffSet();
            if (z > IslandUtil.getEndZ())
                return null;
        }
        return new Location(IslandUtil.getWorld(), x, IslandUtil.getY(), z);
    }

    public void visit(Player player, Island island){
        Role role = island.getRole(player.getUniqueId());
        if (role == Role.Unknown && !island.isVisit() && !player.isOp()){
            player.sendMessage(IslandMessages.NO_PERMISSION_TO_VISIT.getMessage());
            return;
        }
        try {
            if (!island.getSpawn().getChunk().isLoaded())
                island.getSpawn().getChunk().load();

            player.teleport(island.getSpawn());
        }catch (Exception ignored){}

    }

    public Island getIsland(Player player, Location location){
        Island island;
        if (this.cache.containsKey(player.getUniqueId())) {
            island = this.cache.get(player.getUniqueId());
            if (island == null) return null;
            if (island.isAt(location))
                return island;
        }
        island = getIsland(location);
        cache.remove(player.getUniqueId());
        cache.put(player.getUniqueId(), island);
        return island;
    }

    public Island getIsland(Player player){
        Island island;
        Location location = player.getLocation();
        if (this.cache.containsKey(player.getUniqueId())) {
            island = this.cache.get(player.getUniqueId());
            if (island.isAt(location))
                return island;
        }
        island = getIsland(location);
        if (island == null) return null;
        cache.remove(player.getUniqueId());
        cache.put(player.getUniqueId(), island);
        return island;
    }

    public Island getOwnerIsland(Player player){
        return index.get(player.getUniqueId());
    }

    public Island getIsland(String playerName){
        Player player = Bukkit.getPlayerExact(playerName);
        if (player != null)
            return this.index.get(player.getUniqueId());
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        return this.index.get(offlinePlayer.getUniqueId());
    }


    public Island getIsland(Location location){
        return this.index.values().stream().filter(d -> d.isAt(location)).findFirst().orElse(null);
    }


}
