package com.tke.island.api;

import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;

public class LuckPermsAPI {

    @Getter
    private LuckPerms API;

    @Getter
    private static LuckPermsAPI instance;


    public LuckPermsAPI(){
        instance = this;
        init();
    }

    public void init(){
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null)
            API = provider.getProvider();
    }

    public String getPrefix(UUID uuid){
        User user = getAPI().getUserManager().getUser(uuid);
        if (user == null)
            return "";
        Group group = getAPI().getGroupManager().getGroup(user.getPrimaryGroup());
        if (group != null && group.getDisplayName() != null)
            return ChatColor.translateAlternateColorCodes('&', group.getDisplayName());
        return "";
    }

}
