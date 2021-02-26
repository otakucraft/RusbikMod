package rusbik.database;

public class RusbikBlockActionPerformLog {
    private final String init;
    private final String block;
    private final int X;
    private final int Y;
    private final int Z;
    private final String dim;
    private final int actionType;
    private final String date;

    public RusbikBlockActionPerformLog(String player, String block, int X, int Y, int Z, String dim, int actionType, String date) {
        this.init = player;
        this.block = block;
        this.X = X;
        this.Y = Y;
        this.Z = Z;
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
        return X;
    }

    public int getY() {
        return Y;
    }

    public int getZ() {
        return Z;
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
