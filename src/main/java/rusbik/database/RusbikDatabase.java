package rusbik.database;

import net.minecraft.server.network.ServerPlayerEntity;
import rusbik.Rusbik;
import rusbik.helpers.BackPos;
import rusbik.helpers.HomePos;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RusbikDatabase {

    public static Connection c = null;

    public static void initializeDB(){
        try {
            Class.forName("org.sqlite.JDBC");
            boolean createDir = new File("information").mkdirs();
            if (createDir) System.out.println("information dir created");
            c = DriverManager.getConnection("jdbc:sqlite:information/server.db");
            c.setAutoCommit(false);
            Statement stmt = c.createStatement();

            String createPlayerDB = "CREATE TABLE IF NOT EXISTS `player` (" +
                    "`name` VARCHAR(20) PRIMARY KEY NOT NULL," +
                    "`discordId` NUMERIC DEFAULT NULL," +
                    "`timesJoined` NUMERIC DEFAULT 0," +
                    "`perms` NUMERIC DEFAULT 1);";
            stmt.executeUpdate(createPlayerDB);

            String createPlayerInfo = "CREATE TABLE IF NOT EXISTS `pos` (" +
                    "`name` VARCHAR(20) PRIMARY KEY NOT NULL," +
                    "`deathX` NUMERIC DEFAULT NULL," +
                    "`deathY` NUMERIC DEFAULT NULL," +
                    "`deathZ` NUMERIC DEFAULT NULL," +
                    "`deathDim` CHAR(20) DEFAULT NULL," +
                    "`homeX` NUMERIC DEFAULT NULL," +
                    "`homeY` NUMERIC DEFAULT NULL," +
                    "`homeZ` NUMERIC DEFAULT NULL," +
                    "`homeDim` char(20) DEFAULT NULL);";
            stmt.executeUpdate(createPlayerInfo);

            String createLoggerDB = "CREATE TABLE IF NOT EXISTS `logger` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "`name` VARCHAR(20) NOT NULL," +
                    "`block` VARCHAR(30) NOT NULL," +
                    "`posX` NUMERIC NOT NULL," +
                    "`posY` NUMERIC NOT NULL," +
                    "`posZ` NUMERIC NOT NULL," +
                    "`dim` CHAR(20) NOT NULL," +
                    "`action` NUMERIC(1) NOT NULL," +
                    "`date` TEXT NOT NULL);";
            stmt.executeUpdate(createLoggerDB);
            stmt.close();
            c.commit();
        }
        catch (Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public static void addPlayerInformation(String name, long discordId) throws SQLException {
        if (c != null){
            Statement stmt = c.createStatement();
            String addPlayer = String.format("INSERT OR IGNORE INTO player (name, discordId) " +
                    "VALUES ('%s',%d);", name, discordId);
            stmt.executeUpdate(addPlayer);
            String regPlayerPosTable = String.format("INSERT OR IGNORE INTO pos (name) VALUES ('%s');", name);
            stmt.executeUpdate(regPlayerPosTable);
            String addDiscId = String.format("UPDATE player SET discordId = %d WHERE name LIKE '%s'", discordId, name);
            stmt.executeUpdate(addDiscId);
            stmt.close();
            c.commit();
        }
    }

    public static void addPlayerInformation(String name, double X, double Y, double Z, String Dim) throws SQLException {
        if (c != null){
            Statement stmt = c.createStatement();
            String addPlayer = String.format("UPDATE pos SET deathX = %f, deathY = %f, deathZ = %f, deathDim = '%s' " +
                    "WHERE name LIKE '%s';", X, Y, Z, Dim, name); // on player death
            stmt.executeUpdate(addPlayer);
            stmt.close();
            c.commit();
        }
    }

    public static void addPlayerInformation(ServerPlayerEntity player, double X, double Y, double Z, String Dim) throws SQLException {
        if (c != null){
            Statement stmt = c.createStatement();
            String addPlayer = String.format("UPDATE pos SET homeX = %f, homeY = %f, homeZ = %f, homeDim = '%s' " +
                    "WHERE name LIKE '%s';", X, Y, Z, Dim, player.getName().getString()); // on player /setHome
            stmt.executeUpdate(addPlayer);
            stmt.close();
            c.commit();
        }
    }

    public static int getPlayerPerms(String playerName) throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM player WHERE name LIKE '%s';", playerName));
        int playerPerm = rs.getInt("perms");
        rs.close();
        stmt.close();

        return playerPerm;
    }

    public static BackPos getDeathPos(String playerName) throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM pos WHERE name LIKE '%s';", playerName));
        double X = rs.getDouble("deathX");
        double Y = rs.getDouble("deathY");
        double Z = rs.getDouble("deathZ");
        String dim = rs.getString("deathDim");
        rs.close();
        stmt.close();

        return new BackPos(X, Y, Z, dim);
    }

    public static HomePos getHomePos(String playerName) throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM pos WHERE name LIKE '%s';", playerName));
        double X = rs.getDouble("homeX");
        double Y = rs.getDouble("homeY");
        double Z = rs.getDouble("homeZ");
        String dim = rs.getString("homeDim");
        rs.close();
        stmt.close();

        return new HomePos(X, Y, Z, dim);
    }

    public static void updatePerms(String playerName, int value) throws SQLException {
        if (c != null) {
            Statement stmt = c.createStatement();
            String addPlayer = String.format("UPDATE player SET perms = %d WHERE name LIKE '%s';", value, playerName); // on perms update
            stmt.executeUpdate(addPlayer);
            stmt.close();
            c.commit();
        }
    }

    public static boolean playerExists(String playerName) throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(String.format("SELECT timesJoined FROM player WHERE name LIKE '%s';", playerName));
        if (rs.next()) {
            long times = rs.getLong("timesJoined");
            rs.close();
            stmt.close();
            return times != 0;
        }
        rs.close();
        stmt.close();
        return true;
    }

    public static boolean userExists(String playerName) throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(String.format("SELECT name FROM player WHERE name LIKE '%s';", playerName));
        boolean exists = false;
        if (rs.next()) {
            ResultSet rs2 = stmt.executeQuery(String.format("SELECT discordId FROM player WHERE name LIKE '%s';", playerName));
            exists = rs2.getLong("discordId") != 0;
        }
        rs.close();
        stmt.close();
        return exists;
    }

    public static boolean allowedToUpdate(long discId, String playerName) throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(String.format("SELECT discordId FROM player WHERE name LIKE '%s';", playerName));
        boolean isAllowed = rs.getLong("discordId") == discId;
        rs.close();
        stmt.close();
        return isAllowed;
    }

    public static boolean allowedToRemove(long discId, String playerName) throws SQLException {
        boolean isAllowed = true;
        if (userExists(playerName)) {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT discordId FROM player WHERE name LIKE '%s';", playerName));
            isAllowed = rs.getLong("discordId") == discId;
            rs.close();
            stmt.close();
        }
        return isAllowed;
    }

    public static void removeData(String playerName) throws SQLException {
        if (c != null){
            Statement stmt = c.createStatement();
            String deleteDiscId = String.format("UPDATE player SET discordId = NULL WHERE name LIKE '%s';", playerName);
            stmt.executeUpdate(deleteDiscId);
            String deletePos = String.format("UPDATE pos SET homeX = NULL, homeY = NULL, homeZ = NULL, homeDim = NULL, " +
                    "deathX = NULL, deathY = NULL, deathZ = NULL, deathDim = NULL WHERE name LIKE '%s';", playerName);
            stmt.executeUpdate(deletePos);
            stmt.close();
            c.commit();
        }
    }

    public static boolean hasPlayer(long discId) throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM player WHERE discordId LIKE '%d';", discId));
        boolean hasPlayer = rs.next();
        rs.close();
        stmt.close();
        return hasPlayer;
    }

    public static void updateCount(String playerName) throws SQLException {
        if (c != null) {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT timesJoined FROM player WHERE name LIKE '%s';", playerName));
            long times = rs.getLong("timesJoined");
            String playerJoined = String.format("UPDATE player SET timesJoined = %d WHERE name LIKE '%s';", times + 1, playerName);
            stmt.executeUpdate(playerJoined);
            stmt.close();
            c.commit();
        }
    }

    public static void blockLogging(String init, String block, int X, int Y, int Z, String dim, int actionType, String date) throws SQLException {
        if (c != null){
            Statement stmt = c.createStatement();
            String addBrokenBlock = String.format("INSERT INTO logger (name,block,posX,posY,posZ,dim,action,date) " +
                    "VALUES ('%s','%s',%d,%d,%d,'%s',%d,'%s');", init, block, X, Y, Z, dim, actionType, date);
            stmt.executeUpdate(addBrokenBlock);
            stmt.close();
            c.commit();
        }
    }

    public static List<String> getInfo(int X, int Y, int Z, String dim, int page) throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM logger WHERE posX = %d AND posY = %d AND posZ = %d AND dim LIKE '%s' ORDER BY id DESC LIMIT 10 OFFSET %d;", X, Y, Z, dim, (page - 1) * 10));
        List<String> msg = new ArrayList<>();
        int i = 0;
        while (rs.next() && i <= 10){
            String line = Rusbik.buildLine(rs);
            msg.add(line);
            i++;
        }
        if (msg.isEmpty()){
            msg.add("Este bloque nunca ha sido modificado");
        }

        return msg;
    }

    public static int getLines(int X, int Y, int Z, String dim) throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(String.format("SELECT (COUNT(*) / 10) + 1 AS line FROM logger WHERE posX = %d AND posY = %d AND posZ = %d AND dim LIKE '%s';", X, Y, Z, dim));
        int lines = rs.getInt("line");
        rs.close();
        stmt.close();
        return lines;
    }
}
