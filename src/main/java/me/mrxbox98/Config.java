package me.mrxbox98.duels;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

    public static FileConfiguration configuration = DuelsPlugin.getInstance().getConfig();

    public static FileConfiguration getConfig()
    {
        return configuration;
    }

}
