package me.mrxbox98.duels.kit;

import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("slot")
    public int slot;

    @SerializedName("material")
    public String material;

    @SerializedName("amount")
    public int amount;

}
