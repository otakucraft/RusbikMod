package com.kahzerx.rubik.helpers;

public class HomePos {
    // Clase para instanciar la homePos.
    public double x;
    public double y;
    public double z;
    public String dim;

    public HomePos(final double x, final double y, final double z, final String dim) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;
    }

    public HomePos getHomePos() {
        return this;
    }

    public void setHomePos(final double x, final double y, final double z, final String dim) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;
    }

    public boolean isValid() {
        return this.dim != null && !this.dim.equals("");
    }
}
