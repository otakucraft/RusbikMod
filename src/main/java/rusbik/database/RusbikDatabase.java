package rusbik.database;

import net.minecraft.server.network.ServerPlayerEntity;
import rusbik.helpers.BackPos;
import rusbik.helpers.HomePos;
import rusbik.utils.KrusbibUtils;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Toda la gestiÃ³n de bases de datos.
public class RusbikDatabase {

    public static Connection c = null;

    public static void initializeDB(){
        try {
            // Creo la conexiÃ³n.
            Class.forName("org.sqlite.JDBC");
            boolean createDir = new File("information").mkdirs();
            if (createDir) System.out.println("information dir created");
            c = DriverManager.getConnection("jdbc:sqlite:information/server.db");
            c.setAutoCommit(false);
            Statement stmt = c.createStatement();

            // Creo las tablas.
            String createPlayerDB = "CREATE TABLE IF NOT EXISTS `player` (" +
                    "`name` VARCHAR(20) PRIMARY KEY NOT NULL," +
                    "`discordId` NUMERIC DEFAULT NULL," +
                    "`timesJoined` NUMERIC DEFAULT 0," +
                    "`isBanned` NUMERIC DEFAULT 0," +
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

    // Se ejecuta al aÃ±adir al jugador a la whitelist desde el comando !add de discord.
    public static void addPlayerInformation(String name, long discordId) throws SQLException {
        if (c != null){
            // Intentar dedicar una row en la tabla player vincular el id de discord
            String queryAddPlayer = "INSERT OR IGNORE INTO player (name) VALUES (?)";
            PreparedStatement psAddPlayer = c.prepareStatement(queryAddPlayer);
            psAddPlayer.setString(1, name);
            int rsAddPlayer = psAddPlayer.executeUpdate();
            psAddPlayer.close();
            
            // Intentar dedicar una row en la tabla pos para home y death
            String queryPlayerPos = "INSERT OR IGNORE INTO pos (name) VALUES (?)";
            PreparedStatement psPlayerPos = c.prepareStatement(queryPlayerPos);
            psPlayerPos.setString(1, name);
            int rsPlayerPos = psPlayerPos.executeUpdate();
            psPlayerPos.close();
            
            //Añadir el id de discord
            /* No se hace junto con el primer insert porque el jugador puede estar registrado pero tener null en esta columna al sacar de la whitelist por ejemplo
               no se elimina so row por completo, simplemente el discordId, para dar facilidad ante posibles cambios de cuenta de discord.*/
            String queryDiscId = "UPDATE player SET discordId = ? WHERE name LIKE ?";
            PreparedStatement psDiscId = c.prepareStatement(queryDiscId);
            psDiscId.setLong(1, discordId);
            psDiscId.setString(2, name);
            int rsDiscId = psDiscId.executeUpdate();
            psDiscId.close();
            
            c.commit();
        }
    }

    // Actualiza la muerte del jugador
    public static void updatePlayerInformation(String name, double X, double Y, double Z, String Dim) throws SQLException {
        if (c != null){
            String query = "UPDATE pos SET deathX = ?, deathY = ?, deathZ = ?, deathDim = ? WHERE name LIKE ?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setDouble(1, X);
            ps.setDouble(2, Y);
            ps.setDouble(3, Z);
            ps.setString(4, Dim);
            ps.setString(5, name);
            int rs = ps.executeUpdate();
            ps.close();
            
            c.commit();
        }
    }

    // Actualiza la home del jugador
    public static void updatePlayerInformation(ServerPlayerEntity player, double X, double Y, double Z, String Dim) throws SQLException {
        if (c != null){
            String query = "UPDATE pos SET homeX = ?, homeY = ?, homeZ = ?, homeDim = ? WHERE name LIKE ?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setDouble(1, X);
            ps.setDouble(2, Y);
            ps.setDouble(3, Z);
            ps.setString(4, Dim);
            ps.setString(5, player.getName().getString());
            int rs = ps.executeUpdate();
            ps.close();

            c.commit();
        }
    }

    // Conseguir los permisos de cada jugador.
    public static int getPlayerPerms(String playerName) throws SQLException {
        String query = "SELECT perms FROM player WHERE name LIKE ?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setString(1, playerName);
        ResultSet rs = ps.executeQuery();
        int playerPerm = rs.getInt("perms");
        rs.close();
        ps.close();
        
        return playerPerm;
    }

    // Conseguir la posiciÃ³n de la Ãºltima muerte.
    public static BackPos getDeathPos(String playerName) throws SQLException {
        String query = "SELECT deathX , deathY , deathZ , deathDim FROM pos WHERE name LIKE ?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setString(1, playerName);
        ResultSet rs = ps.executeQuery();
        double X = rs.getDouble("deathX");
        double Y = rs.getDouble("deathY");
        double Z = rs.getDouble("deathZ");
        String dim = rs.getString("deathDim");
        rs.close();
        ps.close();

        return new BackPos(X, Y, Z, dim);
    }

    // Conseguir la posiciÃ³n de "home".
    public static HomePos getHomePos(String playerName) throws SQLException {
        String query = "SELECT homeX , homeY , homeZ , homeDim FROM pos WHERE name LIKE ?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setString(1, playerName);
        ResultSet rs = ps.executeQuery();
        double X = rs.getDouble("homeX");
        double Y = rs.getDouble("homeY");
        double Z = rs.getDouble("homeZ");
        String dim = rs.getString("homeDim");
        rs.close();
        ps.close();

        return new HomePos(X, Y, Z, dim);
    }

    // Actualizar los permisos para un jugador.
    public static void updatePerms(String playerName, int value) throws SQLException {
        if (c != null) {
            String query = "UPDATE player SET perms = ? WHERE name LIKE ?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setInt(1, value);
            ps.setString(2, playerName);
            int rs = ps.executeUpdate();
            ps.close();
            c.commit();
        }
    }

    // Check de si es la primera vez que este jugador se conecta.
    public static boolean playerExists(String playerName) throws SQLException {
        String query = "SELECT timesJoined FROM player WHERE name LIKE ?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setString(1, playerName);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            long times = rs.getLong("timesJoined");
            rs.close();
            ps.close();
            return times != 0;
        }
        rs.close();
        ps.close();
        return true;
    }

    // Check de si este jugador estÃ¡ registrado en la base de datos con nombre y discord ID.
    public static boolean userExists(String playerName) throws SQLException {
        String query = "SELECT name FROM player WHERE name LIKE ?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setString(1, playerName);
        ResultSet rs = ps.executeQuery();
        boolean exists = false;
        if (rs.next()) {
            String query2 = "SELECT discordId FROM player WHERE name LIKE ?";
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

    // Banear usuario para que no pueda meter a mÃ¡s gente.
    public static void banUser(long userID) throws SQLException {
        String query = "UPDATE player SET isBanned = 1 WHERE discordId = ?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setLong(1, userID);
        int rs = ps.executeUpdate();
        ps.close();
    }

    // Retirar el ban.
    public static void pardonUser(long userID) throws SQLException {
        String query = "UPDATE player SET isBanned = 0 WHERE discordId = ?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setLong(1, userID);
        int rs = ps.executeUpdate();
        ps.close();
    }

    public static boolean isBanned(long userID) throws SQLException {
        String query = "SELECT name FROM player WHERE discordId = ? AND isBanned = 1";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setLong(1, userID);
        ResultSet rs = ps.executeQuery();
        boolean exists = rs.next();
        rs.close();
        ps.close();
        return exists;
    }

    public static long getID(String playerName) throws SQLException {
        String query = "SELECT discordId FROM player WHERE name LIKE ?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setString(1, playerName);
        ResultSet rs = ps.executeQuery();
        long id = rs.getLong("discordId");
        rs.close();
        ps.close();
        return id;
    }

//    // Si tiene permitido actualizar (por ejemplo su nombre de mc?) WIP.
//    public static boolean allowedToUpdate(long discId, String playerName) throws SQLException {
//        Statement stmt = c.createStatement();
//        ResultSet rs = stmt.executeQuery(String.format("SELECT discordId FROM player WHERE name LIKE '%s';", playerName));
//        boolean isAllowed = rs.getLong("discordId") == discId;
//        rs.close();
//        stmt.close();
//        return isAllowed;
//    }

    // Si tiene permitido eliminar. Para que desde el comando !remove nadie elimine que no sea a sÃ­ mismo.
    public static boolean allowedToRemove(long discId, String playerName) throws SQLException {
        boolean isAllowed = true;
        if (userExists(playerName)) {
            String query = "SELECT discordId FROM player WHERE name LIKE ?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, playerName);
            ResultSet rs = ps.executeQuery();
            isAllowed = rs.getLong("discordId") == discId;
            rs.close();
            ps.close();
        }
        return isAllowed;
    }

    // AcciÃ³n al eliminarte de la whitelist.
    public static void removeData(String playerName) throws SQLException {
        if (c != null) {
            String query = "UPDATE player SET discordId = NULL WHERE name LIKE ?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, playerName);
            int rs = ps.executeUpdate();
            ps.close();
            
            String query2 = "UPDATE pos SET homeX = NULL, homeY = NULL, homeZ = NULL, homeDim = NULL, " +
                    "deathX = NULL, deathY = NULL, deathZ = NULL, deathDim = NULL WHERE name LIKE ?";
            PreparedStatement ps2 = c.prepareStatement(query2);
            ps2.setString(1, playerName);
            int rs2 = ps2.executeUpdate();
            ps2.close();

            c.commit();
        }
    }

    public static String getPlayerName(long discordID) throws SQLException {
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

    // Check de si el jugador ya ha registrado algÃºn usuario, solo puedes registrar una cuenta.
    public static boolean hasPlayer(long discId) throws SQLException {
        String query = "SELECT name FROM player WHERE discordId = ?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setLong(1, discId);
        ResultSet rs = ps.executeQuery();
        boolean hasPlayer = rs.next();
        rs.close();
        ps.close();
        return hasPlayer;
    }

    // Actualizar nÃºmero cada vez que te conectas, solo se usa para comprobar si es la primera vez que te unes.
    public static void updateCount(String playerName) throws SQLException {
        if (c != null) {
            String query = "SELECT timesJoined FROM player WHERE name LIKE ?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, playerName);
            ResultSet rs = ps.executeQuery();
            long times = rs.getLong("timesJoined");
            rs.close();
            ps.close();
            
            String query2 = "UPDATE player SET timesJoined = ? WHERE name LIKE ?";
            PreparedStatement ps2 = c.prepareStatement(query2);
            ps2.setLong(1, times + 1);
            ps2.setString(2, playerName);
            int rs2 = ps2.executeUpdate();
            ps2.close();
            
            c.commit();
        }
    }

    // Registrar acciones de bloques en la base de datos.
    public static void blockLogging(String init, String block, int X, int Y, int Z, String dim, int actionType, String date) throws SQLException {
        if (c != null){
            String query = "INSERT INTO logger (name,block,posX,posY,posZ,dim,action,date) " +
                    "VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, init);
            ps.setString(2, block);
            ps.setInt(3, X);
            ps.setInt(4, Y);
            ps.setInt(5, Z);
            ps.setString(6, dim);
            ps.setInt(7, actionType);            
            ps.setString(8, date);
            int rs = ps.executeUpdate();
            ps.close();
            c.commit();
        }
    }

    // Extraer la informaciÃ³n de un bloque especificado por coordenadas, dimensiÃ³n, y la pÃ¡gina.
    public static List<String> getInfo(int X, int Y, int Z, String dim, int page) throws SQLException {
        String query = "SELECT action , date , name , block  FROM logger " +
                "WHERE posX = ? AND posY = ? AND posZ = ? AND dim LIKE ? ORDER BY id DESC LIMIT 10 OFFSET ?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setInt(1, X);
        ps.setInt(2, Y);
        ps.setInt(3, Z);
        ps.setString(4, dim);
        ps.setInt(5, (page - 1) * 10);
        ResultSet rs = ps.executeQuery();

        List<String> msg = new ArrayList<>();
        int i = 0;
        while (rs.next() && i <= 10){
            String line = KrusbibUtils.buildLine(rs);
            msg.add(line);
            i++;
        }
        if (msg.isEmpty()){
            msg.add("Este bloque nunca ha sido modificado");
        }

        rs.close();
        ps.close();

        return msg;
    }

    // NÃºmero de pÃ¡ginas que puede tener de historial el bloque.
    public static int getLines(int X, int Y, int Z, String dim) throws SQLException {
        String query = "SELECT (COUNT(id) / 10) + 1 AS line FROM logger WHERE posX = ? AND posY = ? AND posZ = ? AND dim LIKE ?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setInt(1, X);
        ps.setInt(2, Y);
        ps.setInt(3, Z);
        ps.setString(4, dim);
        ResultSet rs = ps.executeQuery();
            
        int lines = rs.getInt("line");
        rs.close();
        ps.close();
        return lines;
    }

    // Extraer todos los IDs de gente no baneada o aÃ±adida por excepciÃ³n.
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

    // Extraer todos los nicknames.
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
    
    // Elimina registros del logger si se supera un número de registros
    public static void clearLogger() throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT id FROM logger ORDER BY id DESC LIMIT 1 OFFSET POWER(10,6)");
        
        if(rs.next()){
            int rs2 = stmt.executeUpdate("DELETE FROM logger WHERE id IN (SELECT id FROM logger LIMIT POWER(10,5))");
        }
        rs.close();
        stmt.close();
        
    }
}
