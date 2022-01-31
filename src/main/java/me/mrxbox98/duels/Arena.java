package me.mrxbox98.duels;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Arena {

    @SerializedName("world")
    String world;

    @SerializedName("x")
    float x;

    @SerializedName("y")
    float y;

    @SerializedName("z")
    float z;

    public static Arena getArena() {

        File folder = DuelsPlugin.getInstance().getDataFolder();

        File file = null;

        for(File file1 : folder.listFiles()) {
            if(file1.getName().equals("arena.json")) {
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

        return new Gson().fromJson(json.toString(), Arena.class);
    }
}
