package com.tke.island.util;

import com.google.common.collect.Maps;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.math.BlockVector3;
import com.tke.island.SkyBlock;
import com.tke.island.api.Config;
import com.tke.island.data.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;


public class IslandUtil {

    @Getter
    private static World world = Bukkit.getWorld("island");
    @Getter
    private static BukkitWorld bukkitWorld = new BukkitWorld(world);
    @Getter @Setter
    private static int startX = -50000,
            startZ = -50000,
            endX = 1000000,
            endZ = 1000000,
            y = 75,
            offSet = 5000,
            startBorder = 500,
            limitIsland = 1
    ;


    @SneakyThrows
    public static void paste(Location location, File file){
//        ClipboardFormat format = ClipboardFormats.findByFile(file);
//        Clipboard clipboard;
//        assert format != null;
//        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
//            clipboard = reader.read();
//        }
        Objects.requireNonNull(ClipboardFormats.findByFile(file)).load(file).paste(getBukkitWorld(), BlockVector3.at(location.getX(), location.getY(), location.getZ()));
//        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(world), -1)) {
//            Operation operation = new ClipboardHolder(clipboard)
//                    .createPaste(editSession)
//                    .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
//                    .ignoreAirBlocks(false)
//                    .build();
//            Operations.complete(operation);
//        }
    }

    public static Location unserializer(String path){
        if (path == null || path.equals("")) return null;
        String[] split = path.split(":");
        World world = Bukkit.getWorld(split[0]);
        assert world != null;
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);
        float yaw = Float.parseFloat(split[4]);
        float pitch = Float.parseFloat(split[5]);
        return new Location(world,x,y,z,yaw,pitch);
    }

    public static String serializer(Location location){
        if (location == null || location.getWorld() == null) return "";
        return location.getWorld().getName().trim() + ":" +
                location.getX() + ":" +
                location.getY() + ":" +
                location.getZ() + ":" +
                location.getYaw() + ":" +
                location.getPitch();
    }

    public static Map<UUID, Role> unserializerRoles(String path){
        Map<UUID, Role> map = Maps.newHashMap();
            String[] split = path.split(",");
            Stream.of(split).forEach(s -> {
                UUID uuid = UUID.fromString(s.split(":")[0]);
                Role role = Role.byNameOrDisplay(s.split(":")[1]);
                map.put(uuid, role);
            });
        return map;
    }

    public static String serializerRoles(Map<UUID, Role> roles){
        StringBuilder sb = new StringBuilder();
        roles.forEach((uuid, role) -> sb.append(uuid.toString()).append(":").append(role.getName()).append(","));
        return sb.toString();
    }

    public static void load(){
        FileConfiguration config = SkyBlock.getInstance().getConfig();
        setEndX(config.getInt("Config.endX"));
        setEndZ(config.getInt("Config.endZ"));
        setStartX(config.getInt("Config.startX"));
        setStartZ(config.getInt("Config.startZ"));
        setY(config.getInt("Config.defaultY"));
        setOffSet(config.getInt("Config.offSet"));
        setStartBorder(config.getInt("Config.startBorder"));
    }

}
