package com.tke.island.controller;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.tke.island.api.Config;
import com.tke.island.data.IslandNPCType;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.Objects;
import java.util.Set;

public class IslandNPCTypeController {

    @Getter
    private static IslandNPCTypeController instance;

    @Getter
    private Set<IslandNPCType> types;

    public IslandNPCTypeController(){
        instance = this;
        this.types = Sets.newHashSet();
        load();
    }

    public IslandNPCType getNPCType(Material material){
        return this.types.stream().filter(d -> d.getMaterial() == material).findFirst().orElse(null);
    }

    private void load(){
        Config config = new Config("npcs.yml");
        config.saveDefaultConfig();
        if (config.existeConfig() && config.contains("NPCs")){
            Objects.requireNonNull(config.getConfig().getConfigurationSection("NPCs")).getKeys(false)
                    .forEach(name -> {
                        String display = config.getString("NPCs." + name + ".display").replace("&", "§");
                        String value = config.getString("NPCs." + name + ".skin.value");
                        String signature = config.getString("NPCs." + name + ".skin.signature");
                        Material materialData = Material.getMaterial(config.getString("NPCs." + name + ".block"));
                        String[] commands = Iterables.toArray(config.getStringList("NPCs." + name + ".commands"), String.class);
                        String[] messages = Iterables.toArray(config.getStringList("NPCs." + name + ".messages"), String.class);

                        IslandNPCType npcType = new IslandNPCType(name, display, materialData, commands, messages, signature, value);
                        this.types.add(npcType);
                    });
        }
    }

}
