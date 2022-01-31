package me.mrxbox98.duels;

import java.sql.*;

public class Data {

    public static Connection database;

    public static void setupDatabase() throws SQLException {
        database = DriverManager.getConnection("jdbc:mysql://"
                + Config.getConfig().getString("mysql.host")
                + ":" + Config.getConfig().getString("mysql.port")
                + "/" + Config.getConfig().getString("mysql.database"),
                Config.getConfig().getString("mysql.username"),
                Config.getConfig().getString("mysql.password"));

        if(database.prepareStatement("SELECT * FROM duel_data").executeQuery().next()) {
            return;
        }

        database.prepareStatement("CREATE TABLE duel_data (uuid VARCHAR(36), wins INT, losses INT, kills INT, deaths INT);").execute();
        DuelsPlugin.instance.getLogger().info("Database Created!");
    }

    public static void duelWon(String winner, String looser) throws SQLException {
        //Insert players into database if they are not there already
        //Insert winner into database
        if(!database.prepareStatement("SELECT * FROM duel_data WHERE uuid = '" + winner + "'").executeQuery().next()) {
            database.prepareStatement("INSERT INTO duel_data (uuid, wins, losses, kills, deaths) VALUES ('" + winner + "', 0, 0, 0, 0);").execute();
        }

        //Insert looser into database
        if(!database.prepareStatement("SELECT * FROM duel_data WHERE uuid = '" + looser + "'").executeQuery().next()) {
            database.prepareStatement("INSERT INTO duel_data (uuid, wins, losses, kills, deaths) VALUES ('" + looser + "', 0, 0, 0, 0);").execute();
        }

        //Update their values
        database.prepareStatement("UPDATE duel_data SET wins = wins + 1 WHERE uuid = '" + winner + "';").execute();
        database.prepareStatement("UPDATE duel_data SET losses = losses + 1 WHERE uuid = '" + looser + "';").execute();
        database.prepareStatement("UPDATE duel_data SET kills = kills + 1 WHERE uuid = '" + winner + "';").execute();
        database.prepareStatement("UPDATE duel_data SET deaths = deaths + 1 WHERE uuid = '" + looser + "';").execute();
    }

    public static String stats(String uuid) throws SQLException {
        PreparedStatement preparedStatement = database.prepareStatement("SELECT * FROM duel_data WHERE uuid = ?");
        preparedStatement.setString(1, uuid);
        ResultSet resultSet = preparedStatement.executeQuery();

        if(resultSet.next())
        {
            //Get wins

            return "Wins: " +
                    resultSet.getInt(2) +
                    "\n" +

                    //Get losses
                    "Losses: " +
                    resultSet.getInt(3) +
                    "\n" +

                    //Get kills
                    "Kills: " +
                    resultSet.getInt(4) +
                    "\n" +

                    //Get deaths
                    "Deaths: " +
                    resultSet.getInt(5);
        }
        else
        {
            return "This user has no entered in any duels!";
        }
    }

}
