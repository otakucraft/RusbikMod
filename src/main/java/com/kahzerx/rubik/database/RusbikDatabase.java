package com.kahzerx.rubik.database;

import com.kahzerx.rubik.helpers.BackPos;
import com.kahzerx.rubik.helpers.HomePos;
import com.kahzerx.rubik.helpers.RusbikPlayer;
import com.kahzerx.rubik.Rusbik;
import com.kahzerx.rubik.utils.KrusbibUtils;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * All database management.
 */
public class RusbikDatabase {

    public static Connection c = null;
    public static RusbikLogger logger = new RusbikLogger("RusbikLogger");

    public static void initializeDB(final String directoryName) {
        try {
            // Connection creation.
            Class.forName("org.sqlite.JDBC");
            boolean createDir = new File(String.format("%s/information", directoryName)).mkdirs();
            if (createDir) {
                System.out.println("information dir created");
            }
            c = DriverManager.getConnection(String.format("jdbc:sqlite:%s/information/server.db", directoryName));
            c.setAutoCommit(false);
            Statement stmt = c.createStatement();

            // Table creation.
            String createPlayerDB = "CREATE TABLE IF NOT EXISTS `player` ("
                    + "`name` VARCHAR(20) PRIMARY KEY NOT NULL,"
                    + "`discordId` NUMERIC DEFAULT NULL,"
                    + "`timesJoined` NUMERIC DEFAULT 0,"
                    + "`isBanned` NUMERIC DEFAULT 0,"
                    + "`perms` NUMERIC DEFAULT 1);";
            stmt.executeUpdate(createPlayerDB);

            String createPlayerInfo = "CREATE TABLE IF NOT EXISTS `pos` ("
                    + "`name` VARCHAR(20) PRIMARY KEY NOT NULL,"
                    + "`deathX` NUMERIC DEFAULT NULL,"
                    + "`deathY` NUMERIC DEFAULT NULL,"
                    + "`deathZ` NUMERIC DEFAULT NULL,"
                    + "`deathDim` CHAR(20) DEFAULT NULL,"
                    + "`homeX` NUMERIC DEFAULT NULL,"
                    + "`homeY` NUMERIC DEFAULT NULL,"
                    + "`homeZ` NUMERIC DEFAULT NULL,"
                    + "`homeDim` char(20) DEFAULT NULL,"
                    + "FOREIGN KEY(name) REFERENCES player(name));";
            stmt.executeUpdate(createPlayerInfo);

            String createLoggerDB = "CREATE TABLE IF NOT EXISTS `logger` ("
                    + "`id` INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "`name` VARCHAR(20) NOT NULL,"
                    + "`block` VARCHAR(30) NOT NULL,"
                    + "`posX` NUMERIC NOT NULL,"
                    + "`posY` NUMERIC NOT NULL,"
                    + "`posZ` NUMERIC NOT NULL,"
                    + "`dim` CHAR(20) NOT NULL,"
                    + "`action` NUMERIC(1) NOT NULL,"
                    + "`date` TEXT NOT NULL);";
            stmt.executeUpdate(createLoggerDB);
            stmt.close();
            c.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Add player to the whitelist.
     * It is executed by adding the player to the whitelist from the discord !add command.
     * @param name player's name
     * @param discordId discord ID related to player
     * @throws SQLException connection error
     */
    public static void addPlayerInformation(final String name, final long discordId) throws SQLException {
        if (c != null) {
            // Try to dedicate a row in the player table bind the discord id
            String queryAddPlayer = "INSERT OR IGNORE INTO player (name) VALUES (?)";
            PreparedStatement psAddPlayer = c.prepareStatement(queryAddPlayer);
            psAddPlayer.setString(1, name);
            psAddPlayer.executeUpdate();
            psAddPlayer.close();
            
            // Try to dedicate a row in the pos table for home and death
            String queryPlayerPos = "INSERT OR IGNORE INTO pos (name) VALUES (?)";
            PreparedStatement psPlayerPos = c.prepareStatement(queryPlayerPos);
            psPlayerPos.setString(1, name);
            psPlayerPos.executeUpdate();
            psPlayerPos.close();
            
            // Add discord id
            /* It is not done together with the first insert because the player can be registered but have null in this column when removing from the whitelist for example
            his row is not completely removed, just the discordId, to make it easier for possible discord account changes. */
            String queryDiscId = "UPDATE player SET discordId = ? WHERE name = ?";
            PreparedStatement psDiscId = c.prepareStatement(queryDiscId);
            psDiscId.setLong(1, discordId);
            psDiscId.setString(2, name);
            psDiscId.executeUpdate();
            psDiscId.close();
            
            c.commit();
        }
    }

    /**
     * Update the player's death.
     * @param playerName player's name
     * @param x position X
     * @param y position Y
     * @param z position Z
     * @param dim Dimension
     * @throws SQLException connection error
     */
    public static void updateDeathInformation(final String playerName, final double x, final double y, final double z, final String dim) throws SQLException {
        if (c != null) {
            String query = "UPDATE pos SET deathX = ?, deathY = ?, deathZ = ?, deathDim = ? WHERE name = ?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setDouble(1, x);
            ps.setDouble(2, y);
            ps.setDouble(3, z);
            ps.setString(4, dim);
            ps.setString(5, playerName);
            ps.executeUpdate();
            ps.close();

            c.commit();
        }
        if (Rusbik.players.containsKey(playerName)) {
            Rusbik.players.get(playerName).back.setBackPos(x, y, z, dim);
        }
    }

    /**
     * Update the player's home.
     * @param playerName player entity
     * @param x position X
     * @param y position Y
     * @param z position Z
     * @param dim Dimension
     * @throws SQLException connection error
     */
    public static void updateHomeInformation(final String playerName, final double x, final double y, final double z, final String dim) throws SQLException {
        if (c != null) {
            String query = "UPDATE pos SET homeX = ?, homeY = ?, homeZ = ?, homeDim = ? WHERE name = ?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setDouble(1, x);
            ps.setDouble(2, y);
            ps.setDouble(3, z);
            ps.setString(4, dim);
            ps.setString(5, playerName);
            ps.executeUpdate();
            ps.close();

            c.commit();
        }
        if (Rusbik.players.containsKey(playerName)) {
            Rusbik.players.get(playerName).home.setHomePos(x, y, z, dim);
        }
    }

    /**
     * Get the player's permissions.
     * @param playerName player's name
     * @return player's permissions
     * @throws SQLException connection error
     */
    public static int getPlayerPerms(final String playerName) throws SQLException {
        String query = "SELECT perms FROM player WHERE name = ?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setString(1, playerName);
        ResultSet rs = ps.executeQuery();
        int playerPerm = rs.getInt("perms");
        rs.close();
        ps.close();
        
        return playerPerm;
    }

    /**
     * Get the position of the last death.
     * @param playerName player's name
     * @return player's last death position
     */
    public static BackPos getDeathPos(final String playerName) {
        String query = "SELECT deathX , deathY , deathZ , deathDim FROM pos WHERE name = ?";
        PreparedStatement ps;
        try {
            ps = c.prepareStatement(query);
            ps.setString(1, playerName);
            ResultSet rs = ps.executeQuery();
            double x = rs.getDouble("deathX");
            double y = rs.getDouble("deathY");
            double z = rs.getDouble("deathZ");
            String dim = rs.getString("deathDim");
            rs.close();
            ps.close();

            return new BackPos(x, y, z, dim);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return new BackPos(0, 0, 0, "");
        }
    }

    /**
     * Get the home position.
     * @param playerName player's name
     * @return player's home position
     */
    public static HomePos getHomePos(final String playerName) {
        String query = "SELECT homeX , homeY , homeZ , homeDim FROM pos WHERE name = ?";
        PreparedStatement ps;
        try {
            ps = c.prepareStatement(query);
            ps.setString(1, playerName);
            ResultSet rs = ps.executeQuery();
            double x = rs.getDouble("homeX");
            double y = rs.getDouble("homeY");
            double z = rs.getDouble("homeZ");
            String dim = rs.getString("homeDim");
            rs.close();
            ps.close();

            return new HomePos(x, y, z, dim);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return new HomePos(0, 0, 0, "");
        }
    }

    /**
     * Update player's permissions.
     * @param playerName player's name
     * @param value permissions ID
     * @throws SQLException connection error
     */
    public static void updatePerms(final String playerName, final int value) throws SQLException {
        if (c != null) {
            String query = "UPDATE player SET perms = ? WHERE name = ?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setInt(1, value);
            ps.setString(2, playerName);
            ps.executeUpdate();
            ps.close();
            c.commit();
        }
    }

    /**
     * Check if the player is joining without adding (op player maybe?).
     * @param playerName player's name
     * @return true if it has not a previous login, false otherwise
     * @throws SQLException connection error
     */
    public static boolean hasRow(final String playerName) throws SQLException {
        String query = "SELECT timesJoined FROM player WHERE name = ?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setString(1, playerName);
        ResultSet rs = ps.executeQuery();
        boolean exists = false;
        if (rs.next()) {
            exists = true;
        }
        rs.close();
        ps.close();
        return exists;
    }

    /**
     * Check if this is the first time this player has logged in.
     * @param playerName player's name
     * @return true if it has not a previous login, false otherwise
     * @throws SQLException connection error
     */
    public static boolean playerFirstJoined(final String playerName) throws SQLException {
        String query = "SELECT timesJoined FROM player WHERE name = ?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setString(1, playerName);
        ResultSet rs = ps.executeQuery();
        boolean exists = false;
        if (rs.next()) {
            long times = rs.getLong("timesJoined");
            rs.close();
            ps.close();
            exists = times != 0;
        }
        rs.close();
        ps.close();
        return !exists;
    }

    /**
     * Check if this player is registered in the database with name and discord ID.
     * @param playerName player's name
     * @return true if it has a discord ID registered, false otherwise
     * @throws SQLException connection error
     */
    public static boolean userExists(final String playerName) throws SQLException {
        String query = "SELECT name FROM player WHERE name = ?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setString(1, playerName);
        ResultSet rs = ps.executeQuery();
        boolean exists = false;
        if (rs.next()) {
            String query2 = "SELECT discordId FROM player WHERE name = ?";
            PreparedStatement ps2 = c.prepareStatement(query2);
            ps2.setString(1, playerName);
            ResultSet rs2 = ps2.executeQuery();
            exists = rs2.getLong("discordId") != 0;
            rs2.close();
            ps2.close();
        }
        rs.close();
        ps.close();
        return exists;
    }

    /**
     * Ban user so they can't get more people.
     * @param userID discord ID
     * @throws SQLException connection error
     */
    public static void banUser(final long userID) throws SQLException {
        String query = "UPDATE player SET isBanned = 1 WHERE discordId = ?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setLong(1, userID);
        ps.executeUpdate();
        ps.close();
        c.commit();
    }

    /**
     * Retirar el ban.
     * @param userID discord ID
     * @throws SQLException connection error
     */
    public static void pardonUser(final long userID) throws SQLException {
        String query = "UPDATE player SET isBanned = 0 WHERE discordId = ?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setLong(1, userID);
        ps.executeUpdate();
        ps.close();
        c.commit();
    }

    /**
     * Check if the user is banned
     * @param userID discord ID
     * @return true if the user is banned, false otherwise.
     * @throws SQLException connection error
     */
    public static boolean isBanned(final long userID) throws SQLException {
        String query = "SELECT name FROM player WHERE discordId = ? AND isBanned = 1";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setLong(1, userID);
        ResultSet rs = ps.executeQuery();
        boolean exists = rs.next();
        rs.close();
        ps.close();
        return exists;
    }

    /**
     * Get the discord ID from a player's name.
     * @param playerName player's name
     * @return discord ID
     * @throws SQLException connection error
     */
    public static long getID(final String playerName) throws SQLException {
        String query = "SELECT discordId FROM player WHERE name = ?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setString(1, playerName);
        ResultSet rs = ps.executeQuery();
        long id = rs.getLong("discordId");
        rs.close();
        ps.close();
        return id;
    }

    /**
     * Check if the  discord user can remove the player
     * If you are allowed to delete. So that from the !Remove command nobody removes other than himself.
     * @param discId discord ID
     * @param playerName player's name
     * @return true if it can remove the player, false in other case
     * @throws SQLException connection error
     */
    public static boolean allowedToRemove(final long discId, final String playerName) throws SQLException {
        boolean isAllowed = true;
        if (userExists(playerName)) {
            String query = "SELECT discordId FROM player WHERE name = ?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, playerName);
            ResultSet rs = ps.executeQuery();
            isAllowed = rs.getLong("discordId") == discId;
            rs.close();
            ps.close();
        }
        return isAllowed;
    }

    /**
     * Delete player's data
     * Action by removing yourself from the whitelist.
     * @param playerName player's name
     * @throws SQLException connection error
     */
    public static void removeData(final String playerName) throws SQLException {
        if (c != null) {
            String query = "UPDATE player SET discordId = NULL WHERE name = ?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, playerName);
            ps.executeUpdate();
            ps.close();
            
            String query2 = "UPDATE pos SET homeX = NULL, homeY = NULL, homeZ = NULL, homeDim = NULL, "
                    + "deathX = NULL, deathY = NULL, deathZ = NULL, deathDim = NULL WHERE name = ?";
            PreparedStatement ps2 = c.prepareStatement(query2);
            ps2.setString(1, playerName);
            ps2.executeUpdate();
            ps2.close();

            c.commit();
        }
    }

    /**
     * Get the player's name
     * @param discordID discord ID
     * @return player's name
     * @throws SQLException connection error
     */
    public static String getPlayerName(final long discordID) throws SQLException {
        if (c != null) {
            String query = "SELECT name FROM player WHERE discordId = ?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setLong(1, discordID);
            ResultSet rs = ps.executeQuery();
            String playerName = rs.getString("name");
            rs.close();
            ps.close();
            return playerName;
        }
        return null;
    }

    /**
     * Check if the discord user has already registered some player.
     * Discord users can only register one account.
     * @param discId discord ID
     * @return true if it has register a player, false otherwise
     * @throws SQLException connection error
     */
    public static boolean hasPlayer(final long discId) throws SQLException {
        String query = "SELECT name FROM player WHERE discordId = ?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setLong(1, discId);
        ResultSet rs = ps.executeQuery();
        boolean hasPlayer = rs.next();
        rs.close();
        ps.close();
        return hasPlayer;
    }

    /**
     * Update user's connection number
     * It is only used to check if it is the first time you join.
     * @param playerName player's name
     * @throws SQLException connection error
     */
    public static void updateCount(final String playerName) throws SQLException {
        if (c != null) {
            String query = "SELECT timesJoined FROM player WHERE name = ?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, playerName);
            ResultSet rs = ps.executeQuery();
            long times = rs.getLong("timesJoined");
            rs.close();
            ps.close();
            
            String query2 = "UPDATE player SET timesJoined = ? WHERE name = ?";
            PreparedStatement ps2 = c.prepareStatement(query2);
            ps2.setLong(1, times + 1);
            ps2.setString(2, playerName);
            ps2.executeUpdate();
            ps2.close();
            
            c.commit();
        }
    }

    /**
     * Register block actions in the database.
     * @param log log with the information of the action carried out on the block
     * @throws java.sql.SQLException connection error
     */
    public static void blockLogging(final RusbikBlockActionPerformLog log) throws SQLException {
        if (c != null) {
            String query = "INSERT INTO logger (name,block,posX,posY,posZ,dim,action,date) "
                    + "VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, log.getInit());
            ps.setString(2, log.getBlock());
            ps.setInt(3, log.getX());
            ps.setInt(4, log.getY());
            ps.setInt(5, log.getZ());
            ps.setString(6, log.getDim());
            ps.setInt(7, log.getActionType());
            ps.setString(8, log.getDate());
            ps.executeUpdate();
            ps.close();
            c.commit();
        }
    }

    /**
     * Get information from a block
     * @param x block coordinate X
     * @param y block coordinate Y
     * @param z block coordinate Z
     * @param dim block dimension
     * @param page pagination with 10 logs per page
     * @return requested logs
     * @throws SQLException connection error
     */
    public static List<String> getInfo(final int x, final int y, final int z, final String dim, final int page) throws SQLException {
        String query = "SELECT action , date , name , block  FROM logger "
                + "WHERE posX = ? AND posY = ? AND posZ = ? AND dim = ? ORDER BY id DESC LIMIT 10 OFFSET ?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setInt(1, x);
        ps.setInt(2, y);
        ps.setInt(3, z);
        ps.setString(4, dim);
        ps.setInt(5, (page - 1) * 10);
        ResultSet rs = ps.executeQuery();

        List<String> msg = new ArrayList<>();
        int i = 0;
        while (rs.next() && i <= 10) {
            String line = KrusbibUtils.buildLine(rs);
            msg.add(line);
            i++;
        }
        if (msg.isEmpty()) {
            msg.add("Este bloque nunca ha sido modificado");
        }

        rs.close();
        ps.close();

        return msg;
    }

    /**
     * Number of pages that the block can have in history.
     * @param x block coordinate X
     * @param y block coordinate Y
     * @param z block coordinate Z
     * @param dim block dimension
     * @return number of pages with 10 log
     * @throws SQLException connection error
     */
    public static int getLines(final int x, final int y, final int z, final String dim) throws SQLException {
        String query = "SELECT (COUNT(id) / 10) + 1 AS line FROM logger WHERE posX = ? AND posY = ? AND posZ = ? AND dim = ?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setInt(1, x);
        ps.setInt(2, y);
        ps.setInt(3, z);
        ps.setString(4, dim);
        ResultSet rs = ps.executeQuery();
        int lines = rs.getInt("line");
        rs.close();
        ps.close();
        return lines;
    }

    /**
     * Extract all the IDs of people not banned or added by exception.
     * @return list of discord IDs
     * @throws SQLException connection error
     */
    public static List<Long> getIDs() throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT discordId FROM player WHERE isBanned = 0 AND discordId IS NOT NULL AND discordId IS NOT 999999;");
        List<Long> idList = new ArrayList<>();
        while (rs.next()) {
            idList.add(rs.getLong("discordId"));
        }
        rs.close();
        stmt.close();
        return idList;
    }

    /**
     * Extract all nicknames.
     * @return list with players' name
     * @throws SQLException connection error
     */
    public static List<String> getNames() throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT name FROM player WHERE isBanned = 0 AND discordId IS NOT NULL");
        List<String> nameList = new ArrayList<>();
        while (rs.next()) {
            nameList.add(rs.getString("name"));
        }
        rs.close();
        stmt.close();
        return nameList;
    }

    /**
     * Clear blocks logger records
     * Delete first 100k records if it exists more than 1M records
     * @throws SQLException connection error
     */
    public static void clearLogger() throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT id FROM logger ORDER BY id DESC LIMIT 1 OFFSET POWER(10,6)");
        if (rs.next()) {
            stmt.executeUpdate("DELETE FROM logger WHERE id IN (SELECT id FROM logger LIMIT POWER(10,5))");
        }
        rs.close();
        stmt.close();
        
    }

    /**
     * Add the player to the list of online players
     * @param player player to be added
     */
    public static void addPlayer(final String player) {
        if (Rusbik.players.containsKey(player)) {
            removePlayer(player);
        }
        Rusbik.players.put(player, new RusbikPlayer(player));
    }
    
    /**
     * Remove the player from the list od online players
     * @param player player to be removed
     */
    public static void removePlayer(final String player) {
        Rusbik.players.remove(player);
    }
}
