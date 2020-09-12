package com.tke.island.util;

import com.tke.island.SkyBlock;
import com.tke.island.api.Config;
import lombok.*;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;

@AllArgsConstructor
public enum IslandMessages {

    ISLAND_LIMIT_EXCEEDED("§cIsland limit has been exceeded."),
    ISLAND_LIMIT_PER_PLAYER_EXCEEDED("§cYou are not allowed to have more islands."),
    NOT_PERMISSION("§cYou do not have permission."),
    NO_PERMISSION_TO_BUILD("§cYou are not allowed to build on this island."),
    NO_PERMISSION_TO_VISIT("§cYou are not allowed to visit this island."),
    YOU_HAVE_NO_ISLAND("§cYou have no island."),
    PLAYER_ISLAND_DOES_NOT_EXIST("§cThis player does not have an island."),
    ISLAND_DOES_NOT_EXIST("§cIsland does not exists."),
    PLAYER_ADDED("§aPlayer added."),
    PLAYER_REMOVED("§aPlayer removed."),
    PLAYER_ALREADY_ADDED("§cThat player is already a member of the island."),
    PLAYER_IS_NOT_A_MEMBER("§cThat player is not a member of the island."),
    PLAYER_PROMOTED("§aPlayer was successfully promoted."),
    PROMOTE_FAILED("§cYou cannot promote someone with a position greater than or equal to yours."),
    KICK_FAILED("§cYou cannot kick someone with a position greater than or equal to yours."),
    FORMAT_CHAT_ISLAND("§a[$role] §f[$rank] §5[$name] §8>> §d$message"),
    NOT_MEMBER("§cYou are not a member of that island."),
    ISLAND_DELETE("§cIsland deleted!"),
    BIOME_NULL("§cThis biome does not exist.")






    ;

    @Getter @Setter
    String message;

    public static void load(){
        Config config = new Config("config.yml");
        Arrays.stream(values()).forEach(islandMessages -> {
            val msg = ChatColor.translateAlternateColorCodes('&', config.getString("Messages." + islandMessages.name()));
            islandMessages.setMessage(msg);
        });
    }


}
