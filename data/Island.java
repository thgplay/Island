package com.tke.island.data;

import com.boydti.fawe.util.EditSessionBuilder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.tke.island.SkyBlock;
import com.tke.island.api.Cuboid;
import com.tke.island.api.LuckPermsAPI;
import com.tke.island.controller.IslandNPCTypeController;
import com.tke.island.sql.Database;
import com.tke.island.util.IslandMessages;
import com.tke.island.util.IslandUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
public class Island {

    int id;
    UUID owner;
    long created;
    Location center,spawn;
    IslandNPC[] npcs;
    Map<UUID, Role> roles;
    int border;
    boolean visit;

    public Island(UUID owner, Location center){
        this.owner = owner;
        this.center = center;
        this.spawn = center.clone();
        this.border = IslandUtil.getStartBorder();
        this.created = System.currentTimeMillis();
        this.roles = new HashMap<>(Collections.singletonMap(owner, Role.Owner));
        this.visit = false;
    }

    public boolean isAt(Location location) {
        if (location == null) {
            return false;
        }
        int x = Math.abs(location.getBlockX() - center.getBlockX());
        int z = Math.abs(location.getBlockZ() - center.getBlockZ());
        return x < border && z < border;
    }

    public void setBiome(BiomeType biome){
        Location min = getMinimumLocation();
        min.setY(IslandUtil.getY());
        Location max = getMaximumLocation();
        max.setY(IslandUtil.getY());

        EditSession s = new EditSessionBuilder(IslandUtil.getBukkitWorld())
                .fastmode(false)
                .checkMemory(true)
                .build();
        new Cuboid(min, max).getLocations().forEach(loc -> s.setBiome(BlockVector2.at(loc.getBlockX(), loc.getBlockZ()), biome));
        s.flushSession();
    }

    public void clearTerrain(){
        Location min = getMinimumLocation();
        Location max = getMaximumLocation();

        EditSession editSession = new EditSessionBuilder(IslandUtil.getBukkitWorld())
                .fastmode(false)
                .checkMemory(true)
                .build();
        new Cuboid(min, max).getLocations()
                .forEach(loc -> {
                    editSession.setBlock(BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), BaseBlock.getState(0,0));
                });
        editSession.flushSession();

    }

    public Location getMinimumLocation(){
        return new Location(center.getWorld(), center.getX() - (IslandUtil.getStartBorder()/2), 0, center.getZ() - (IslandUtil.getStartBorder()/2));
    }

    public Location getMaximumLocation(){
        return new Location(center.getWorld(), center.getX() + (IslandUtil.getStartBorder()/2), 255, center.getZ() + (IslandUtil.getStartBorder()/2));
    }

    public Role getRole(UUID uuid){
        return this.roles.getOrDefault(uuid, Role.Unknown);
    }

    public void update() {
        SkyBlock.runAsynchronously(() -> Database.getInstance().update(this));
    }

    public IslandNPC getNPC(NPC npc){
        return Stream.of(this.npcs).filter(d -> d.getNpc() != null && d.getNpc().getUniqueId().equals(npc.getUniqueId())).findFirst().orElse(null);
    }

    public void spawnNPCs(){
        List<IslandNPC> n = Lists.newArrayList();
        Map<Material, Location> blocks = new Cuboid(center.clone().subtract(30, 5, 30), center.clone().add(30, 5, 30)).getMaterials();

        IslandNPCTypeController.getInstance().getTypes().stream().filter(d -> blocks.containsKey(d.getMaterial()))
                .forEach(type -> {
                    Location loc = blocks.get(type.getMaterial());
                    IslandNPC npc = new IslandNPC(type);
                    npc.spawn(loc.clone().add(0.5,1,0.5));
                    n.add(npc);
                });
        new BukkitRunnable() {
            @Override
            public void run() {
                n.forEach(islandNPC -> islandNPC.setSkin(islandNPC.getNpcType().getValue(), islandNPC.getNpcType().getSignature()));
                this.cancel();
            }
        }.runTaskLater(SkyBlock.getInstance(), 5*20);
        System.out.println(4);
        this.npcs = Iterables.toArray(n, IslandNPC.class);
    }

    public void despawnNPCs(){
        if (this.npcs != null)
            Arrays.stream(this.npcs).forEach(IslandNPC::despawn);
    }

    public void sendMessage(Player sender, String... message){
        Role role = getRole(sender.getUniqueId());
        if (role == Role.Unknown){
            sender.sendMessage(IslandMessages.NOT_MEMBER.getMessage());
            return;
        }

        StringBuilder sb = new StringBuilder();
        Stream.of(message).forEach(s -> sb.append(s).append(" "));

        String msg = IslandMessages.FORMAT_CHAT_ISLAND.getMessage()
                .replace("$role", "§a" + role.getName())
                .replace("$rank", LuckPermsAPI.getInstance().getPrefix(sender.getUniqueId()))
                .replace("$name", sender.getName())
                .replace("$message", sb.toString());

        this.roles.keySet().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline())
                player.sendMessage(msg);
        });

    }
}
