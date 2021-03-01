/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rusbik.helpers;

import rusbik.database.RusbikDatabase;

public class RusbikPlayer {
    public HomePos home;
    public BackPos back;

    public RusbikPlayer(String player) {
        this.back = RusbikDatabase.getDeathPos(player);
        this.home = RusbikDatabase.getHomePos(player);
    }
}
