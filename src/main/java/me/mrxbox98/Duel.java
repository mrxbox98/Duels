package me.mrxbox98.duels;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.events.Event;

import java.sql.SQLException;

public class Duel implements Listener {

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

    public Duel(Player inviter, Player invited)
    {
        this.inviter=inviter;
        this.invited=invited;
        this.oldInviterLocation=inviter.getLocation();
        this.oldInvitedLocation=invited.getLocation();
        this.oldInviterInventory=inviter.getInventory();
        this.oldInvitedInventory=invited.getInventory();
        state=0;
        DuelsPlugin.instance.getServer().getPluginManager().registerEvents(this, (JavaPlugin)DuelsPlugin.getInstance());
    }

    public void onPlayerDeath(PlayerDeathEvent event) {
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

    }

    public void resetPlayers()
    {
        inviter.teleport(oldInviterLocation);
        invited.teleport(oldInvitedLocation);
        inviter.getInventory().setContents(oldInviterInventory.getContents());
        invited.getInventory().setContents(oldInvitedInventory.getContents());
    }
}
