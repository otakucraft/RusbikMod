package rusbik.database;

import net.minecraft.server.network.ServerPlayerEntity;
import rusbik.back.BackPos;

import java.io.File;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class RusbikDatabase {

    public static Connection c = null;

    public static void initializeDB(){
        try {
            Class.forName("org.sqlite.JDBC");
            new File("information").mkdirs();
            c = DriverManager.getConnection("jdbc:sqlite:information/players.db");
            Statement stmt = c.createStatement();
            String createPlayerDB = "CREATE TABLE IF NOT EXISTS `player` (" +
                    "`name` varchar(20) PRIMARY KEY NOT NULL," +
                    "`perms` INT DEFAULT 1," +
                    "`deathX` NUMERIC DEFAULT NULL," +
                    "`deathY` NUMERIC DEFAULT NULL," +
                    "`deathZ` NUMERIC DEFAULT NULL," +
                    "`deathDim` char(20) DEFAULT NULL," +
                    "`homeX` NUMERIC DEFAULT NULL," +
                    "`homeY` NUMERIC DEFAULT NULL," +
                    "`homeZ` NUMERIC DEFAULT NULL," +
                    "`homeDim` char(20) DEFAULT NULL);";
            stmt.executeUpdate(createPlayerDB);
            stmt.close();
        }
        catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    public static void addPlayerInformation(String name) throws SQLException {
        if (c != null){
            Statement stmt = c.createStatement();
            String addPlayer = String.format("INSERT OR IGNORE INTO player (name,perms) " +
                    "VALUES ('%s',1);", name); // only on new player
            stmt.executeUpdate(addPlayer);
            stmt.close();
        }
    }

    public static void addPlayerInformation(String name, double X, double Y, double Z, String Dim) throws SQLException {
        if (c != null){
            Statement stmt = c.createStatement();
            String addPlayer = String.format("UPDATE player SET deathX = %f, deathY = %f, deathZ = %f, deathDim = '%s' " +
                    "WHERE name like '%s';", X, Y, Z, Dim, name); // on player death
            stmt.executeUpdate(addPlayer);
            stmt.close();
        }
    }

    public static void addPlayerInformation(ServerPlayerEntity player, double X, double Y, double Z, String Dim) throws SQLException {
        if (c != null){
            Statement stmt = c.createStatement();
            String addPlayer = String.format("UPDATE player SET homeX = %f, homeY = %f, homeZ = %f, homeDim = '%s' " +
                    "WHERE name like '%s';", X, Y, Z, Dim, player.getName().getString()); // on player /setHome
            stmt.executeUpdate(addPlayer);
            stmt.close();
        }
    }

    public static int getPlayerPerms(String playerName) throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM player WHERE name like '%s';", playerName));
        int playerPerm = rs.getInt("perms");
        rs.close();
        stmt.close();

        return playerPerm;
    }

    public static BackPos getDeathPos(String playerName) throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM player WHERE name like '%s';", playerName));
        double X = rs.getDouble("deathX");
        double Y = rs.getDouble("deathY");
        double Z = rs.getDouble("deathZ");
        String dim = rs.getString("deathDim");

        return new BackPos(X, Y, Z, dim);
    }
}
