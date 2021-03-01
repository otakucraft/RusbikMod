package rusbik.helpers;

public class BackPos {
    // Clase para instanciar la deathPos.
    public double X;
    public double Y;
    public double Z;
    public String dim;

    public BackPos(double X, double Y, double Z, String dim){
        this.X = X;
        this.Y = Y;
        this.Z = Z;
        this.dim = dim;
    }

    public BackPos getBackPos() {
        return this;
    }

    public void setBackPos(double X, double Y, double Z, String dim) {
        this.X = X;
        this.Y = Y;
        this.Z = Z;
        this.dim = dim;
    }

    public boolean isValid() {
        return !this.dim.equals("");
    }
}
