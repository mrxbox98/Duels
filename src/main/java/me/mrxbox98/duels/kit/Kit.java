package me.mrxbox98.duels.kit;

import com.google.gson.annotations.SerializedName;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Kit {

    @SerializedName("name")
    public String name;

    @SerializedName("armor_content")
    public HashMap<String, Armor> armorContent;

    @SerializedName("inventory_content")
    public Item[] inventoryContent;

    public ItemStack getHelmet()
    {
        if(!armorContent.containsKey("helmet") || armorContent.get("helmet")==null)
        {
            return null;
        }
        String material = armorContent.get("helmet").material;
        int amount = armorContent.get("helmet").amount;

        return new ItemStack(Material.getMaterial(material), amount);
    }

    public ItemStack getChestplate()
    {
        if(!armorContent.containsKey("chestplate") || armorContent.get("chestplate")==null)
        {
            return null;
        }
        String material = armorContent.get("chestplate").material;
        int amount = armorContent.get("chestplate").amount;

        return new ItemStack(Material.getMaterial(material), amount);
    }

    public ItemStack getLeggings()
    {
        if(!armorContent.containsKey("leggings") || armorContent.get("leggings")==null)
        {
            return null;
        }
        String material = armorContent.get("leggings").material;
        int amount = armorContent.get("leggings").amount;

        return new ItemStack(Material.getMaterial(material), amount);
    }

    public ItemStack getBoots()
    {
        if(!armorContent.containsKey("boots") || armorContent.get("boots")==null)
        {
            return null;
        }
        String material = armorContent.get("boots").material;
        int amount = armorContent.get("boots").amount;

        return new ItemStack(Material.getMaterial(material), amount);
    }

}