public class CBoard implements Cloneable{
    // размерности доски (CB-chess board)
    public static final int CB_DIM = 8; // базовая размерность
    public static final int CB_XWIDTH = CB_DIM + 2; //размерность с учетом "клеток все поля"
    public static final int CB_YHEIGHT = CB_DIM + 2;

    private final int[][] cb;


    @Override
    protected CBoard clone() throws CloneNotSupportedException
    {
        return (CBoard)super.clone();
    }

    CBoard () {
        this.cb = new int[CB_XWIDTH][CB_YHEIGHT];
    }
    public int get(int x, int y) {
        return cb[x][y];
    }

    public void set(int x, int y, int value) {
        cb[x][y] = value;
    }
}
