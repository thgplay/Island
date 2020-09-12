package com.tke.island.listener;

import com.tke.island.controller.IslandController;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerListener implements Listener {

    IslandController controller;

    public PlayerListener(){
        this.controller = IslandController.getInstance();
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e){
        Player p = e.getPlayer();
        controller.getCache().remove(p.getUniqueId());
    }

    @EventHandler
    public void onKick(PlayerKickEvent e){
        Player p = e.getPlayer();
        controller.getCache().remove(p.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        controller.getCache().remove(p.getUniqueId());
    }


}
