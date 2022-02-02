package me.mrxbox98.duels;

import me.mrxbox98.duels.kit.Item;
import me.mrxbox98.duels.kit.Kit;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

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

    public PlayerInventory oldInviterInventory;

    public PlayerInventory oldInvitedInventory;

    public ItemStack[] inviterItems = new ItemStack[36];

    public ItemStack[] invitedItems = new ItemStack[36];

    public ItemStack[] oldInviterArmor;

    public ItemStack[] oldInvitedArmor;

    public Kit kit;

    public World world;

    public Duel(Player inviter, Player invited)
    {
        this.inviter=inviter;
        this.invited=invited;
        this.oldInviterLocation=inviter.getLocation().clone();
        this.oldInvitedLocation=invited.getLocation().clone();

        oldInviterInventory=inviter.getInventory();
        oldInvitedInventory=invited.getInventory();

        oldInviterArmor = inviter.getInventory().getArmorContents().clone();
        oldInvitedArmor = invited.getInventory().getArmorContents().clone();

        for(int i=0;i<36;i++)
        {
            inviterItems[i]=inviter.getInventory().getItem(i);
        }

        for(int i=0;i<36;i++)
        {
            invitedItems[i]=invited.getInventory().getItem(i);
        }

        state=0;
        kit=DuelsPlugin.kits.getDefaultKit();
        DuelsPlugin.instance.getServer().getPluginManager().registerEvents(this, DuelsPlugin.getInstance());

        duels.add(this);

        inviter.sendMessage(ChatColor.GREEN+"You have invited "+invited.getName()+" to a duel!");
        invited.sendMessage(ChatColor.GREEN+"You have been invited to a duel by "+inviter.getName()+"!");

        Bukkit.getScheduler().runTaskLater(DuelsPlugin.getInstance(), this::timeout, 1200L);
    }

    public Duel(Player inviter, Player invited, Kit kit)
    {
        this.inviter=inviter;
        this.invited=invited;
        this.oldInviterLocation=inviter.getLocation().clone();
        this.oldInvitedLocation=invited.getLocation().clone();

        oldInviterInventory=inviter.getInventory();
        oldInvitedInventory=invited.getInventory();

        oldInviterArmor = inviter.getInventory().getArmorContents().clone();
        oldInvitedArmor = invited.getInventory().getArmorContents().clone();

        for(int i=0;i<36;i++)
        {
            inviterItems[i]=inviter.getInventory().getItem(i);
        }

        for(int i=0;i<36;i++)
        {
            invitedItems[i]=invited.getInventory().getItem(i);
        }

        state=0;
        this.kit=kit;
        DuelsPlugin.instance.getServer().getPluginManager().registerEvents(this, DuelsPlugin.getInstance());

        duels.add(this);

        inviter.sendMessage(ChatColor.GREEN+"You have invited "+invited.getName()+" to a duel!");
        invited.sendMessage(ChatColor.GREEN+"You have been invited to a duel by "+inviter.getName()+"!");

        Bukkit.getScheduler().runTaskLater(DuelsPlugin.getInstance(), this::timeout, 1200L);
    }

    public void timeout()
    {
        if(!duels.contains(this) || state==0)
        {
            return;
        }

        DuelsPlugin.getInstance().getLogger().info(state+"");

        inviter.sendMessage(ChatColor.RED+"The duel has timed out!");

        HandlerList.unregisterAll(this);

        duels.remove(this);
    }

    public static World cloneWorld(World world)
    {
        File dir = new File(Bukkit.getServer().getWorldContainer(), world.getName());

        String fileName = world.getName()+UUID.randomUUID();

        try {
            FileUtils.copyDirectory(dir, new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (File file : new File(fileName).listFiles())
        {
            if (file.isFile())
            {
                if (file.getName().equalsIgnoreCase("uid.dat"))
                {
                    file.delete();
                }
            }
        }

        return Bukkit.getServer().createWorld(new WorldCreator(fileName));
    }

    public void acceptDuel()
    {
        state=1;

        inviter.getInventory().clear();
        invited.getInventory().clear();

        world = cloneWorld(Bukkit.getWorld(DuelsPlugin.arena.world));

        Location loc = new Location(world, DuelsPlugin.arena.x, DuelsPlugin.arena.y, DuelsPlugin.arena.z);

        inviter.teleport(loc);
        invited.teleport(loc);

        loadInventories();

        startDuel();


    }

    public void startDuel()
    {
        state=2;

        invited.sendMessage(ChatColor.GREEN+"5 seconds until the duel starts!");
        inviter.sendMessage(ChatColor.GREEN+"5 seconds until the duel starts!");

        Bukkit.getScheduler().runTaskLater(DuelsPlugin.getInstance(), () -> secondsLeft(4), 20L);
        Bukkit.getScheduler().runTaskLater(DuelsPlugin.getInstance(), () -> secondsLeft(3), 40L);
        Bukkit.getScheduler().runTaskLater(DuelsPlugin.getInstance(), () -> secondsLeft(2), 60L);
        Bukkit.getScheduler().runTaskLater(DuelsPlugin.getInstance(), () -> secondsLeft(1), 80L);

        Bukkit.getScheduler().runTaskLater(DuelsPlugin.getInstance(), this::startFighting, 100L);
    }

    public void secondsLeft(int seconds)
    {
        invited.sendMessage(ChatColor.GREEN+""+seconds+" seconds until the duel starts!");
        inviter.sendMessage(ChatColor.GREEN+""+seconds+" seconds until the duel starts!");
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

        event.setCancelled(true);

        if(!(event.getPlayer().getUniqueId().equals(inviter.getUniqueId()) || event.getPlayer().getUniqueId().equals(invited.getUniqueId())))
        {
            return;
        }
        Player winner = event.getEntity().getKiller();
        Player loser = event.getEntity();

        if(winner==null)
        {
            winner = invited.getUniqueId().equals(loser.getUniqueId()) ? inviter : invited;

            try {
                Data.duelWonWithoutKill(winner.getUniqueId().toString(), loser.getUniqueId().toString());
            } catch (SQLException e) {
                e.printStackTrace();
            }

            winner.sendMessage(ChatColor.GREEN + "You won the duel!");
            loser.sendMessage(ChatColor.RED + "You lost the duel!");

            event.getPlayer().spigot().respawn();

            resetPlayers();

            HandlerList.unregisterAll(this);

            duels.remove(this);

            return;
        }

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

    @EventHandler
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

    public void setArmorMaybe(Player player, ItemStack[] armor, int index)
    {
        switch (index) {
            case 0 -> {
                if (armor[0] != null) {
                    player.getInventory().setBoots(armor[0]);
                }
            }
            case 1 -> {
                if (armor[1] != null) {
                    player.getInventory().setLeggings(armor[1]);
                }
            }
            case 2 -> {
                if (armor[2] != null) {
                    player.getInventory().setChestplate(armor[2]);
                }
            }
            case 3 -> {
                if (armor[3] != null) {
                    player.getInventory().setHelmet(armor[3]);
                }
            }
        }
    }

    public void resetPlayers()
    {
        inviter.getInventory().clear();
        invited.getInventory().clear();

        inviter.teleport(oldInviterLocation);
        invited.teleport(oldInvitedLocation);

        inviter.setHealth(inviter.getMaxHealth());
        invited.setHealth(invited.getMaxHealth());

        setArmorMaybe(inviter, oldInviterArmor, 0);
        setArmorMaybe(inviter, oldInviterArmor, 1);
        setArmorMaybe(inviter, oldInviterArmor, 2);
        setArmorMaybe(inviter, oldInviterArmor, 3);

        setArmorMaybe(invited, oldInvitedArmor, 0);
        setArmorMaybe(invited, oldInvitedArmor, 1);
        setArmorMaybe(invited, oldInvitedArmor, 2);
        setArmorMaybe(invited, oldInvitedArmor, 3);

        for(int i=0;i<36;i++)
        {
            inviter.getInventory().setItem(i, inviterItems[i]);
            DuelsPlugin.instance.getLogger().info(inviterItems[i]==null ? "" : inviterItems[i].toString());
        }

        for(int i=0;i<36;i++)
        {
            invited.getInventory().setItem(i, invitedItems[i]);
            DuelsPlugin.instance.getLogger().info(invitedItems[i]==null ? "" : invitedItems[i].toString());
        }



        File dir = new File(Bukkit.getServer().getWorldContainer(), world.getName());
        //delete world
        Bukkit.getServer().unloadWorld(world, false);

        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void accept(Player player, Player player2)
    {
        for(Duel duel: duels)
        {
            if(duel.inviter.getUniqueId().equals(player2.getUniqueId()) && duel.invited.getUniqueId().equals(player.getUniqueId()))
            {
                duel.acceptDuel();
            }
        }
    }

    public static boolean hasDuel(Player player, Player player2)
    {
        for(Duel duel: duels)
        {
            if(duel.invited.getUniqueId().equals(player2.getUniqueId()) && duel.inviter.getUniqueId().equals(player.getUniqueId()))
            {
                return true;
            }
        }
        return false;
    }
}
