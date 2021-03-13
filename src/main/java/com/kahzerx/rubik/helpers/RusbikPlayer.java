package com.kahzerx.rubik.helpers;

import com.kahzerx.rubik.database.RusbikDatabase;

public class RusbikPlayer {
    public HomePos home;
    public BackPos back;

    public RusbikPlayer(final String player) {
        this.back = RusbikDatabase.getDeathPos(player);
        this.home = RusbikDatabase.getHomePos(player);
    }
}
