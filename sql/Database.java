package com.tke.island.sql;

import com.tke.island.SkyBlock;
import com.tke.island.controller.IslandController;
import com.tke.island.data.Island;
import com.tke.island.data.Role;
import com.tke.island.util.IslandUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.Map;
import java.util.UUID;

public class Database implements AutoCloseable {


    @Getter
    private static Database instance;

    @Getter
    private String database;
    @Getter
    private Connection connection;

    public Database(){
        instance = this;
        init();
    }

    private void init(){
        FileConfiguration config = SkyBlock.getInstance().getConfig();
        this.database = config.getString("SQL.database");
        String query = "jdbc:mysql://" + config.getString("SQL.host") + ":" + config.getInt("SQL.port") + "/" + database + "?autoReconnect=true";
        System.out.println(query);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(query, config.getString("SQL.user"), config.getString("SQL.password"));
            createTables();
            SkyBlock.runAsynchronously(this::load);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Could not connect to MySQL");
            Bukkit.getServer().shutdown();
        }
    }

    @SneakyThrows
    public void load(){
            try (PreparedStatement stm = connection.prepareStatement("SELECT * FROM `" + getDatabase() + "`.`island`")){
                try (ResultSet rs = stm.executeQuery()){
                    if (rs.next()){
                        int id = rs.getInt("id");
                        UUID owner = UUID.fromString(rs.getString("owner"));
                        long created = rs.getLong("created");
                        Location center = IslandUtil.unserializer(rs.getString("center"));
                        Location spawn = IslandUtil.unserializer(rs.getString("spawn"));
                        Map<UUID, Role> members = IslandUtil.unserializerRoles(rs.getString("members"));
                        int border = rs.getInt("border");
                        boolean visit = rs.getByte("is_visit") == 1;

                        Island island = new Island(id, owner, created, center, spawn, null, members, border, visit);
                        island.spawnNPCs();
                        IslandController.getInstance().getIndex().put(owner, island);
                    }
                }
            }
    }

    @SneakyThrows
    private void createTables(){
        try (PreparedStatement stm = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + getDatabase() + "`.`island` (" +
                "  `id` BIGINT NOT NULL AUTO_INCREMENT," +
                "  `owner` VARCHAR(64) NULL," +
                "  `created` BIGINT NULL," +
                "  `center` LONGTEXT NULL," +
                "  `spawn` LONGTEXT NULL," +
                "  `members` LONGTEXT NULL," +
                "  `border` INT NULL DEFAULT 500," +
                "  `is_visit` BIT(1) NULL DEFAULT 0," +
                "  PRIMARY KEY (`id`))" +
                "ENGINE = InnoDB " +
                "DEFAULT CHARACTER SET = utf8;")){
            stm.executeUpdate();
        }
    }

    @SneakyThrows
    public void create(Island island){
        try (PreparedStatement stm = getConnection().prepareStatement("INSERT INTO `" + getDatabase() + "`.`island` " +
                "(`owner`,`created`,`center`,`spawn`,`members`,`border`,`is_visit`) VALUES(?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)){

            stm.setString(1, island.getOwner().toString());
            stm.setLong(2, island.getCreated());
            stm.setString(3, IslandUtil.serializer(island.getCenter()));
            stm.setString(4, IslandUtil.serializer(island.getSpawn()));
            stm.setString(5, IslandUtil.serializerRoles(island.getRoles()));
            stm.setInt(6, island.getBorder());
            stm.setByte(7, (byte) (island.isVisit() ? 1 : 0));

            stm.executeUpdate();

            try (ResultSet s = stm.getGeneratedKeys()){
                if (s.next())
                    island.setId(s.getInt(1));
            }
        }
    }

    @SneakyThrows
    public void update(Island island){
        try (PreparedStatement stm = getConnection().prepareStatement("UPDATE `" + getDatabase() + "`.`island` SET" +
                "`spawn` = ?,`members` = ?,`border` = ?,`is_visit` = ? WHERE `id` = ?")){

            stm.setString(1, IslandUtil.serializer(island.getSpawn()));
            stm.setString(2, IslandUtil.serializerRoles(island.getRoles()));
            stm.setInt(3, island.getBorder());
            stm.setByte(4, (byte) (island.isVisit() ? 1 : 0));

            stm.setInt(5, island.getId());

            stm.executeUpdate();
        }
    }

    @SneakyThrows
    public void delete(Island island){
        try (PreparedStatement stm = getConnection().prepareStatement("DELETE FROM `" + getDatabase() + "`.`island` " +
                "WHERE `id` = ?")){
            stm.setInt(1, island.getId());
            stm.executeUpdate();
        }
    }


    @Override
    public void close() throws Exception {
        connection.close();
    }
}
