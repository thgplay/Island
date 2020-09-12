package com.tke.island.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tke.island.SkyBlock;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.InputStream;
import java.io.InputStreamReader;

@Data
public class IslandNPC {

    NPC npc;
    Location spawn;
    IslandNPCType npcType;

    public IslandNPC(IslandNPCType type){
        this.npcType = type;
    }

    public void spawn(Location location){
        this.spawn = location;
        if (spawn != null){

            new BukkitRunnable() {
                @Override
                public void run() {
                    npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, npcType.getDisplayName());
                    npc.spawn(spawn);
                }
            }.runTask(SkyBlock.getInstance());
        }
    }

    public void setSkin(String value, String signature) {
        if (value == null || signature == null || value.equals("") || signature.equals("")) return;
//			((SkinnableEntity)npc.getEntity()).getProfile().getProperties().put("textures", new Property("textures", value, signature));
        ((SkinnableEntity)npc.getEntity()).setSkinPersistent("island", signature, value);
    }

    public void despawn(){
        if (this.npc != null && this.npc.isSpawned())
            this.npc.despawn();
    }

}
