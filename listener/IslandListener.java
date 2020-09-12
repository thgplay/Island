package com.tke.island.listener;

import com.google.common.collect.Sets;
import com.tke.island.SkyBlock;
import com.tke.island.controller.IslandController;
import com.tke.island.data.Island;
import com.tke.island.data.IslandNPC;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Set;

public class IslandListener implements Listener {

    Set<Player> cache;

    public IslandListener(){
        this.cache = Sets.newHashSet();
    }

    @EventHandler
    public void onInteract(NPCRightClickEvent e){

        Player p = e.getClicker();
        Island island = IslandController.getInstance().getIsland(p);
        if (island == null) return;
        IslandNPC npc = island.getNPC(e.getNPC());
        if (npc == null) return;
        e.setCancelled(true);
        if (cache.contains(p)) return;
        cache.add(p);
        Arrays.stream(npc.getNpcType().getMessage()).forEach(s -> {
            p.sendMessage(s.replace("&", "§").replace("@player", p.getName()));
        });

        new BukkitRunnable() {
            @Override
            public void run() {
                Arrays.stream(npc.getNpcType().getCommands()).forEach(s -> {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s.replace("@player", p.getName()));
                });
                cache.remove(p);
                this.cancel();
            }
        }.runTaskLater(SkyBlock.getInstance(), 20);
    }

}
