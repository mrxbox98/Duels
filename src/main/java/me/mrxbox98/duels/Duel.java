package me.mrxbox98.duels;

import me.mrxbox98.duels.kit.Item;
import me.mrxbox98.duels.kit.Kit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.events.Event;

import java.sql.SQLException;
import java.util.ArrayList;

public class Duel implements Listener {

    public static ArrayList<Duel> duels = new ArrayList<>();

    /**
     * 0 = Invited
     * 1 = Accepted
     * 2 = Starting
     * 3 = Ongoing
     * 4 = Finished
     */
    public int state;

    public Player inviter;

    public Player invited;

    public Location oldInviterLocation;

    public Location oldInvitedLocation;

    public Inventory oldInviterInventory;

    public Inventory oldInvitedInventory;

    public Kit kit;

    public Duel(Player inviter, Player invited)
    {
        this.inviter=inviter;
        this.invited=invited;
        this.oldInviterLocation=inviter.getLocation();
        this.oldInvitedLocation=invited.getLocation();
        this.oldInviterInventory=inviter.getInventory();
        this.oldInvitedInventory=invited.getInventory();
        state=0;
        kit=DuelsPlugin.kits.getDefaultKit();
        DuelsPlugin.instance.getServer().getPluginManager().registerEvents(this, (JavaPlugin)DuelsPlugin.getInstance());
        loadInventories();

        duels.add(this);
    }

    public void acceptDuel()
    {
        state=1;
        inviter.teleport(invited.getLocation());
        invited.teleport(inviter.getLocation());
        loadInventories();
        startDuel();
    }

    public void startDuel()
    {
        state=2;
        Bukkit.getScheduler().runTaskLater(DuelsPlugin.getInstance(), this::startFighting, 5000L);
    }

    public void startFighting()
    {
        state=3;
    }

    public void loadInventories()
    {
        inviter.getInventory().setContents(new ItemStack[]{});

        inviter.getInventory().setHelmet(kit.getHelmet());
        inviter.getInventory().setChestplate(kit.getChestplate());
        inviter.getInventory().setLeggings(kit.getLeggings());
        inviter.getInventory().setBoots(kit.getBoots());

        for(Item item : kit.inventoryContent)
        {
            ItemStack itemStack = new ItemStack(Material.getMaterial(item.material), item.amount);

            inviter.getInventory().setItem(item.slot, itemStack);
        }

        invited.getInventory().setContents(new ItemStack[]{});

        invited.getInventory().setHelmet(kit.getHelmet());
        invited.getInventory().setChestplate(kit.getChestplate());
        invited.getInventory().setLeggings(kit.getLeggings());
        invited.getInventory().setBoots(kit.getBoots());

        for(Item item : kit.inventoryContent)
        {
            ItemStack itemStack = new ItemStack(Material.getMaterial(item.material), item.amount);

            invited.getInventory().setItem(item.slot, itemStack);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        state=4;

        if(!(event.getPlayer().getUniqueId().equals(inviter.getUniqueId()) || event.getPlayer().getUniqueId().equals(invited.getUniqueId())))
        {
            return;
        }
        Player winner = event.getEntity().getKiller();
        Player loser = event.getEntity();

        //Winner should be not null in this case
        assert winner != null;

        try {
            Data.duelWon(winner.getUniqueId().toString(), loser.getUniqueId().toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        winner.sendMessage(ChatColor.GREEN + "You won the duel!");
        loser.sendMessage(ChatColor.RED + "You lost the duel!");

        resetPlayers();

        HandlerList.unregisterAll(this);

        duels.remove(this);
    }

    public void onEntityDamage(EntityDamageEvent event)
    {
        if(state!=2)
        {
            return;
        }
        if(event.getEntity() instanceof Player player)
        {
            if(player.getUniqueId().equals(inviter.getUniqueId()) || player.getUniqueId().equals(invited.getUniqueId()))
            {
                event.setCancelled(true);
            }
        }
    }

    public void resetPlayers()
    {
        inviter.teleport(oldInviterLocation);
        invited.teleport(oldInvitedLocation);
        inviter.getInventory().setContents(oldInviterInventory.getContents());
        invited.getInventory().setContents(oldInvitedInventory.getContents());
    }
}
