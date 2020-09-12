package com.tke.island.listener;

import com.tke.island.controller.IslandController;
import com.tke.island.data.Island;
import com.tke.island.data.Role;
import com.tke.island.util.IslandMessages;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class BlockListener implements Listener {

    IslandController controller;

    public BlockListener(){
        controller = IslandController.getInstance();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        Player p = e.getPlayer();
        Block block = e.getBlockPlaced();
        Island island = controller.getIsland(p, block.getLocation());
        if (island == null) return;
        Role role = island.getRole(p.getUniqueId());
        if (!role.isHigherOrEqualThan(Role.Member) && !p.isOp()){
            e.setCancelled(true);
            p.sendMessage(IslandMessages.NO_PERMISSION_TO_BUILD.getMessage());
            return;
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        Player p = e.getPlayer();
        Block block = e.getBlock();
        Island island = controller.getIsland(p, block.getLocation());
        if (island == null) return;
        Role role = island.getRole(p.getUniqueId());
        if (!role.isHigherOrEqualThan(Role.Member) && !p.isOp()){
            e.setCancelled(true);
            p.sendMessage(IslandMessages.NO_PERMISSION_TO_BUILD.getMessage());
            return;
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if (e.getClickedBlock() == null) return;
        Island island = controller.getIsland(p, e.getClickedBlock().getLocation());
        if (island == null) return;
        Role role = island.getRole(p.getUniqueId());
        if (!role.isHigherOrEqualThan(Role.Member) && !p.isOp()){
            e.setCancelled(true);
            p.sendMessage(IslandMessages.NO_PERMISSION_TO_BUILD.getMessage());
            return;
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e){
        Player p = e.getPlayer();
        Island island = controller.getIsland(p, p.getLocation());
        if (island == null) return;
        Role role = island.getRole(p.getUniqueId());
        if (!role.isHigherOrEqualThan(Role.Member) && !p.isOp()){
            e.setCancelled(true);
        }
    }

}
