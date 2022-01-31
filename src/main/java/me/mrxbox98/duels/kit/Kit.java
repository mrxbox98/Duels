package me.mrxbox98.duels.kit;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class Kit {

    @SerializedName("name")
    public String name;

    @SerializedName("armor_content")
    public HashMap<String, Armor> armorContent;

}