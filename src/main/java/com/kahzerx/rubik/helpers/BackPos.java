package com.kahzerx.rubik.helpers;

public class BackPos {
    // Clase para instanciar la deathPos.
    public double x;
    public double y;
    public double z;
    public String dim;

    public BackPos(final double x, final double y, final double z, final String dim) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;
    }

    public BackPos getBackPos() {
        return this;
    }

    public void setBackPos(final double x, final double y, final double z, final String dim) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;
    }

    public boolean isValid() {
        return this.dim != null && !this.dim.equals("");
    }
}
