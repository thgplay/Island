package com.tke.island.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

@Data
@AllArgsConstructor
public class IslandNPCType {

    String name,displayName;
    Material material;
    String[] commands,message;
    String signature,value;


}
