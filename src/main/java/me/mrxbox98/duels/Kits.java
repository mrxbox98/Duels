package me.mrxbox98.duels;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import me.mrxbox98.duels.kit.Kit;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Kits {

    @SerializedName("default_kit")
    public String defaultKit;

    @SerializedName("kits")
    public Kit[] kits;

    public Kit getDefaultKit() {
        for (Kit kit : kits)
        {
            if (kit.name.equals(defaultKit))
            {
                return kit;
            }
        }
        return null;
    }

    public static Kits getKits() {

        File folder = DuelsPlugin.getInstance().getDataFolder();

        File file = null;

        for(File file1 : folder.listFiles()) {
            if(file1.getName().equals("kit.json")) {
                file = file1;
            }
        }

        StringBuilder json = new StringBuilder();

        try {
            Scanner scanner = new Scanner(file);

            while(scanner.hasNextLine()) {
                json.append(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return new Gson().fromJson(json.toString(), Kits.class);
    }
}
