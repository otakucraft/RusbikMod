package com.kahzerx.rubik.database;

public class RusbikBlockActionPerformLog {
    /**
     * Clase para instanciar, loggers.
     */
    private final String init;
    private final String block;
    private final int x;
    private final int y;
    private final int z;
    private final String dim;
    private final int actionType;
    private final String date;

    public RusbikBlockActionPerformLog(
            String player,
            String block,
            int x,
            int y,
            int z,
            String dim,
            int actionType,
            String date) {
        this.init = player;
        this.block = block;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;
        this.actionType = actionType;
        this.date = date;
    }

    public String getInit() {
        return init;
    }

    public String getBlock() {
        return block;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getDim() {
        return dim;
    }

    public int getActionType() {
        return actionType;
    }

    public String getDate() {
        return date;
    }
}
