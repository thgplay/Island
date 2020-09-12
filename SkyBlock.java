package com.tke.island;

import com.tke.island.api.LuckPermsAPI;
import com.tke.island.api.WorldEditAPI;
import com.tke.island.command.IslandCommand;
import com.tke.island.controller.IslandController;
import com.tke.island.controller.IslandNPCTypeController;
import com.tke.island.data.Island;
import com.tke.island.listener.BlockListener;
import com.tke.island.listener.IslandListener;
import com.tke.island.listener.PlayerListener;
import com.tke.island.sql.Database;
import com.tke.island.util.IslandMessages;
import com.tke.island.util.IslandUtil;
import com.tke.island.util.WorldGenerator;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.stream.Stream;

public final class SkyBlock extends JavaPlugin {

    @Getter
    private static SkyBlock instance;

    @Getter
    private File schematic;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        schematic  = new File(SkyBlock.getInstance().getDataFolder().getAbsolutePath() + "/island.schem");
        init();
    }


    private void init(){
//        IslandUtil.createWorld();
        initDependencies();
        initControllers();
        new Database();
        initCommands();
        initListeners();
    }

    private void initDependencies(){
        new WorldEditAPI();
        new LuckPermsAPI();
    }

    private void initControllers(){
        createWorld();
        new IslandNPCTypeController();
        IslandMessages.load();
        IslandUtil.load();
        new IslandController();
    }

    private void initCommands(){
        getCommand("island").setExecutor(new IslandCommand());
    }

    private void initListeners(){
        Stream.of(
                new BlockListener(),
                new PlayerListener(),
                new IslandListener()
        ).forEach($ -> Bukkit.getPluginManager().registerEvents($, this));
    }

    public static void runAsynchronously(Runnable r){
        Bukkit.getScheduler().runTaskAsynchronously(SkyBlock.getInstance(), r);
    }

    private void createWorld(){
        if (Bukkit.getWorld("island") != null) return;
        WorldCreator wc = new WorldCreator("island");
        wc.generator(new WorldGenerator()); //The chunk generator from step 1
        wc.createWorld();
    }

    @Override
    public void onDisable() {
        IslandController.getInstance().unloadAll();
    }
}
