/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rusbik.helpers;

import java.sql.SQLException;
import net.minecraft.server.network.ServerPlayerEntity;
import rusbik.database.RusbikDatabase;

/**
 *
 * @author Pablo Alcaraz
 */
public class RusbikPlayer {
    public final ServerPlayerEntity player;
    public HomePos home;
    public BackPos back;

    public RusbikPlayer(ServerPlayerEntity player, boolean load) throws SQLException{
        this.player = player;
        if (load) {
            this.back = RusbikDatabase.getDeathPos(player.getName().getString());
            this.home = RusbikDatabase.getHomePos(player.getName().getString());
        }
    }

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    public HomePos getHome() {
        return home;
    }

    public BackPos getBack() {
        return back;
    }
    
    public void setBack(double X, double Y, double Z, String dim) {
        this.back.X = X;
        this.back.Y = Y;
        this.back.Z = Z;
        this.back.dim = dim;
    }
    
    public void setHome(double X, double Y, double Z, String dim) {
        this.home.X = X;
        this.home.Y = Y;
        this.home.Z = Z;
        this.home.dim = dim;
    }
    
    
    @Override
    public boolean equals(Object o) { 
        if (o == this) { 
            return true; 
        } 
        if(o instanceof String) {
            return this.player.getName().getString().equals(((String) o)); 
        }
        if (o instanceof ServerPlayerEntity){
            return this.player.getName().getString().equals(
                ((ServerPlayerEntity) o).getName().getString()
            ); 
        }
        if (!(o instanceof RusbikPlayer)) { 
            return false; 
        } 
        return this.player.getName().getString().equals(
                ((RusbikPlayer) o).getPlayer().getName().getString()
        ); 
    }     
    
}
