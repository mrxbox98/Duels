package me.mrxbox98.duels;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class CommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if(command.getName().equalsIgnoreCase("duels"))
        {
            if(strings.length==0)
            {
                commandSender.sendMessage("---- Duels Help ----");
                commandSender.sendMessage("/help - Prints this message");
                commandSender.sendMessage("/duel <name> [kit] - Duel another player");
                commandSender.sendMessage("/stats [name] - Gets the statistics of you or another player");
                commandSender.sendMessage("/accept <name> - Accept a duel request");
                return true;
            }
            switch(strings[0].toLowerCase())
            {
                case "stats":
                {
                    if(strings.length==1)
                    {
                        if(commandSender instanceof ConsoleCommandSender)
                        {
                            commandSender.sendMessage("This command needs a player name if executed through the console!");
                            return true;
                        }
                        else
                        {
                            Player player = (Player) commandSender;

                            try {
                                player.sendMessage(Data.stats(player.getUniqueId().toString()));
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                            return true;
                        }
                    }
                    else
                    {
                        String uuid = getUUID(strings[1]);
                        if(uuid==null)
                        {
                            commandSender.sendMessage("This player is not online!");
                            return true;
                        }
                        Player player = (Player) commandSender;

                        try {
                            player.sendMessage(Data.stats(player.getUniqueId().toString()));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        return true;
                    }
                }
            }
        }
        return true;
    }

    public static String getUUID(String playerName)
    {
        for(Player player: DuelsPlugin.instance.getServer().getOnlinePlayers())
        {
            if(player.getName().equalsIgnoreCase(playerName))
            {
                return player.getUniqueId().toString();
            }
        }
        return null;
    }
}
