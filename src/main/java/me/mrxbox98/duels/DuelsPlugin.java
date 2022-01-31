package me.mrxbox98.duels;

import com.google.gson.Gson;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class DuelsPlugin extends JavaPlugin {

    public static DuelsPlugin instance;

    public static Kits kits;

    @Override
    public void onEnable()
    {
        instance = this;
        getLogger().info("Started Duels v1.0.0!");
        saveDefaultConfig();
        try {
            Data.setupDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        kits = Kits.getKits();
        getLogger().info(new Gson().toJson(kits));
    }

    @Override
    public void onDisable()
    {
        instance = null;
    }

    public static DuelsPlugin getInstance()
    {
        return instance;
    }

}
