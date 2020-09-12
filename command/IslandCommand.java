package com.tke.island.command;

import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.tke.island.controller.IslandController;
import com.tke.island.data.Island;
import com.tke.island.data.Role;
import com.tke.island.util.IslandMessages;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IslandCommand implements CommandExecutor {

    IslandController controller;

    public IslandCommand(){
        this.controller = IslandController.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("island")){
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 0) {
                    player.sendMessage(new String[]{
                            "",
                            "   §e§lISLAND §7- Commands:",
                            " §7• §f/island go §7OR §f/island go §6<player> §8- §7Go to an island.",
                            " §7• §f/island invite §6<player> §8- §7Invite the player to go to your island.",
                            " §7• §f/island biome §6<biome> §8- §7Define island biome.",
                            " §7• §f/island sethome §8- §7Define island spawn.",
                            " §7• §f/island open §8- §7Enable visitors on your island.",
                            " §7• §f/island close §8- §7Disable visitors on your island.",
                            " §7• §f/island add §6<player> §8- §7Add the player on the island.",
                            " §7• §f/island remove §6<player> §8- §7Remove the player on the island.",
                            " §7• §f/island promote §6<player> §8- §7Promote the player on the island.",
                            " §7• §f/island members §8- §7List of members of the island.",
                            " §7• §f/island chat §6<message> §8- §7Private chat on the island.",
                            " §7• §f/island delete §8- §7Delete your island.",
                    });
                    return true;
                }
                if (args[0].equalsIgnoreCase("go")) {
                    Island island;
                    if (args.length == 1) {
                        island = controller.getOwnerIsland(player);
                        if (island == null) {
                            player.sendMessage(IslandMessages.YOU_HAVE_NO_ISLAND.getMessage());
                            return true;
                        }
                        player.teleport(island.getSpawn());
                    } else {
                        String name = args[1];
                        island = controller.getIsland(name);
                        if (island == null) {
                            player.sendMessage(IslandMessages.PLAYER_ISLAND_DOES_NOT_EXIST.getMessage());
                            return true;
                        }
                        controller.visit(player, island);
                    }
                } else if (args[0].equalsIgnoreCase("invite")) {
                    if (args.length == 1) {
                        player.sendMessage("§cUse: §f/island invite §7<player>");
                        return true;
                    }
                    Player target = Bukkit.getPlayerExact(args[1]);
                    if (target == null || !target.isOnline()) {
                        player.sendMessage("§cPlayer offline.");
                        return true;
                    }
                    player.sendMessage("§aInvitation sent.");
                    BaseComponent[] sb = new ComponentBuilder("§eYou were invited to go to the island of §f" + player.getName())
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island go " + player.getName()))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click here to go to the island.").create())).create();
                    target.spigot().sendMessage(sb);
                } else if (args[0].equalsIgnoreCase("sethome")) {
                    Island island = controller.getIsland(player);
                    if (island == null) {
                        player.sendMessage(IslandMessages.ISLAND_DOES_NOT_EXIST.getMessage());
                        return true;
                    }
                    Role role = island.getRole(player.getUniqueId());
                    if (!role.isHigherOrEqualThan(Role.Assistant)) {
                        player.sendMessage(IslandMessages.NOT_PERMISSION.getMessage());
                        return true;
                    }
                    island.setSpawn(player.getLocation());
                    island.update();
                    player.sendMessage("§aSpawn defined.");
                } else if (args[0].equalsIgnoreCase("open")) {
                    Island island = controller.getIsland(player);
                    if (island == null) {
                        player.sendMessage(IslandMessages.ISLAND_DOES_NOT_EXIST.getMessage());
                        return true;
                    }
                    Role role = island.getRole(player.getUniqueId());
                    if (!role.isHigherOrEqualThan(Role.Assistant)) {
                        player.sendMessage(IslandMessages.NOT_PERMISSION.getMessage());
                        return true;
                    }
                    island.setVisit(true);
                    island.update();
                    player.sendMessage("§aOpen island.");
                } else if (args[0].equalsIgnoreCase("close")) {
                    Island island = controller.getIsland(player);
                    if (island == null) {
                        player.sendMessage(IslandMessages.ISLAND_DOES_NOT_EXIST.getMessage());
                        return true;
                    }
                    Role role = island.getRole(player.getUniqueId());
                    if (!role.isHigherOrEqualThan(Role.Assistant)) {
                        player.sendMessage(IslandMessages.NOT_PERMISSION.getMessage());
                        return true;
                    }
                    island.setVisit(false);
                    island.update();
                    player.sendMessage("§aClose island.");
                } else if (args[0].equalsIgnoreCase("add")) {
                    if (args.length == 1) {
                        player.sendMessage("§cUse: §f/island add §7<player>");
                        return true;
                    }
                    Player target = Bukkit.getPlayerExact(args[1]);
                    if (target == null || !target.isOnline()) {
                        player.sendMessage("§cPlayer offline.");
                        return true;
                    }
                    Island island = controller.getIsland(player);
                    if (island == null) {
                        player.sendMessage(IslandMessages.ISLAND_DOES_NOT_EXIST.getMessage());
                        return true;
                    }
                    Role role = island.getRole(player.getUniqueId());
                    if (!role.isHigherOrEqualThan(Role.Assistant)) {
                        player.sendMessage(IslandMessages.NOT_PERMISSION.getMessage());
                        return true;
                    }
                    if (island.getRole(target.getUniqueId()) != Role.Unknown) {
                        player.sendMessage(IslandMessages.PLAYER_ALREADY_ADDED.getMessage());
                        return true;
                    }
                    island.getRoles().put(target.getUniqueId(), Role.Guest);
                    island.update();
                    player.sendMessage(IslandMessages.PLAYER_ADDED.getMessage());
                } else if (args[0].equalsIgnoreCase("remove")) {
                    if (args.length == 1) {
                        player.sendMessage("§cUse: §f/island remove §7<player>");
                        return true;
                    }
                    Player target = Bukkit.getPlayerExact(args[1]);
                    if (target == null || !target.isOnline()) {
                        player.sendMessage("§cPlayer offline.");
                        return true;
                    }
                    Island island = controller.getIsland(player);
                    if (island == null) {
                        player.sendMessage(IslandMessages.ISLAND_DOES_NOT_EXIST.getMessage());
                        return true;
                    }
                    Role role = island.getRole(player.getUniqueId());
                    if (!role.isHigherOrEqualThan(Role.Assistant)) {
                        player.sendMessage(IslandMessages.NOT_PERMISSION.getMessage());
                        return true;
                    }
                    Role targetRole = island.getRole(target.getUniqueId());
                    if (targetRole == Role.Unknown) {
                        player.sendMessage(IslandMessages.PLAYER_IS_NOT_A_MEMBER.getMessage());
                        return true;
                    }
                    if (targetRole.isHigherOrEqualThan(targetRole)) {
                        player.sendMessage(IslandMessages.KICK_FAILED.getMessage());
                        return true;
                    }
                    island.getRoles().remove(target.getUniqueId());
                    island.update();
                    player.sendMessage(IslandMessages.PLAYER_REMOVED.getMessage());
                } else if (args[0].equalsIgnoreCase("promote")) {
                    if (args.length == 1) {
                        player.sendMessage("§cUse: §f/island remove §7<player>");
                        return true;
                    }
                    Player target = Bukkit.getPlayerExact(args[1]);
                    if (target == null || !target.isOnline()) {
                        player.sendMessage("§cPlayer offline.");
                        return true;
                    }
                    if (player.getName().equals(target.getName())) {
                        player.sendMessage("§cYou cannot promote yourself.");
                        return true;
                    }
                    Island island = controller.getIsland(player);
                    if (island == null) {
                        player.sendMessage(IslandMessages.ISLAND_DOES_NOT_EXIST.getMessage());
                        return true;
                    }
                    Role role = island.getRole(player.getUniqueId());
                    if (!role.isHigherOrEqualThan(Role.Owner)) {
                        player.sendMessage(IslandMessages.NOT_PERMISSION.getMessage());
                        return true;
                    }
                    Role targetRole = island.getRole(target.getUniqueId());
                    if (targetRole == Role.Unknown) {
                        player.sendMessage(IslandMessages.PLAYER_IS_NOT_A_MEMBER.getMessage());
                        return true;
                    }
                    if (targetRole.isHigherOrEqualThan(targetRole)) {
                        player.sendMessage(IslandMessages.KICK_FAILED.getMessage());
                        return true;
                    }
                    island.getRoles().replace(target.getUniqueId(), island.getRole(target.getUniqueId()), Role.values()[island.getRole(target.getUniqueId()).ordinal() + 1]);
                    island.update();
                    player.sendMessage(IslandMessages.PLAYER_PROMOTED.getMessage());
                } else if (args[0].equalsIgnoreCase("members")) {
                    Island island = controller.getIsland(player);
                    if (island == null) {
                        player.sendMessage(IslandMessages.ISLAND_DOES_NOT_EXIST.getMessage());
                        return true;
                    }
                    player.sendMessage(new String[]{"", "   §e§lISLAND §7- Members:"});
                    island.getRoles().forEach((uuid, role) -> {
                        OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
                        player.sendMessage(" §7• §f" + target.getName() + "§8 - §6" + role.getName());
                    });
                } else if (args[0].equalsIgnoreCase("chat")) {
                    if (args.length == 1) {
                        player.sendMessage("§cUse: §f/island chat §7<message>");
                        return true;
                    }
                    Island island = controller.getIsland(player);
                    if (island == null) {
                        player.sendMessage(IslandMessages.ISLAND_DOES_NOT_EXIST.getMessage());
                        return true;
                    }

                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < args.length; i++)
                        sb.append(args[i]).append(" ");

                    island.sendMessage(player, sb.toString());
                } else if (args[0].equalsIgnoreCase("biome")) {
                    if (args.length == 1) {
                        player.sendMessage("§cUse: §f/island biome §7<biomeType>");
                        return true;
                    }
                    Island island = controller.getIsland(player);
                    if (island == null) {
                        player.sendMessage(IslandMessages.ISLAND_DOES_NOT_EXIST.getMessage());
                        return true;
                    }
                    BiomeType biome = BiomeTypes.get(args[1].toLowerCase());
                    if (biome == null) {
                        player.sendMessage(IslandMessages.BIOME_NULL.getMessage());
                        return true;
                    }
                    Role role = island.getRole(player.getUniqueId());
                    if (!role.isHigherOrEqualThan(Role.Assistant)) {
                        player.sendMessage(IslandMessages.NOT_PERMISSION.getMessage());
                        return true;
                    }

                    island.setBiome(biome);
                    player.sendMessage("§eBiome defined for §f" + biome.getId().replace("minecraft:", "") + "§e.");
                } else if (args[0].equalsIgnoreCase("create")) {
                    controller.create(player);
                } else if (args[0].equalsIgnoreCase("delete")) {
                    Island island = controller.getIsland(player);
                    if (island == null) {
                        player.sendMessage(IslandMessages.ISLAND_DOES_NOT_EXIST.getMessage());
                        return true;
                    }
                    controller.delete(player, island);
                } else if (args[0].equalsIgnoreCase("admin")) {
                    Island island = controller.getIsland(player);
                    if (island == null) {
                        player.sendMessage(IslandMessages.ISLAND_DOES_NOT_EXIST.getMessage());
                        return true;
                    }
                    controller.delete(player, island);
                }
            } else {
                if (args.length < 3){
                    sender.sendMessage("§cUse: §f/island admin setborder §7<player> <border>");
                    return true;
                }
                Player player = Bukkit.getPlayerExact(args[2]);
                if (player == null || !player.isOnline()){
                    sender.sendMessage("§cPlayer offline.");
                    return true;
                }
                int border = Integer.parseInt(args[3]);
                if (border > 1000){
                    sender.sendMessage("§cMax 1000.");
                    return true;
                }
                Island island = IslandController.getInstance().getOwnerIsland(player);
                if (island == null){
                    sender.sendMessage(IslandMessages.ISLAND_DOES_NOT_EXIST.getMessage());
                    return true;
                }
                island.setBorder(border);
                island.update();
                sender.sendMessage("§eBorder defined to §f" + border + "§e.");
            }
        }
        return false;
    }
}
