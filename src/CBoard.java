public class CBoard implements Cloneable{
    // размерности доски (CB-chess board)
    public static final int CB_DIM = 8; // базовая размерность
    public static final int CB_XWIDTH = CB_DIM + 2; //размерность с учетом "клеток вне поля"
    public static final int CB_YHEIGHT = CB_DIM + 2;

    private int[][] cb;


    @Override
    protected CBoard clone()  {
        CBoard clone = null;
        try {
            clone = (CBoard) super.clone();
            clone.cb = cb.clone();
            for (int i = 0; i < cb.length; i++) {
                clone.cb[i] = cb[i].clone();
            }
        } catch (CloneNotSupportedException e) {
                e.printStackTrace();
        }
         return clone;
    }

    public CBoard () {
        this.cb = new int[CB_XWIDTH][CB_YHEIGHT];
    }
    public int get(int x, int y) {
        return cb[x][y];
    }

    public void set(int x, int y, int value) {
        cb[x][y] = value;
    }
}


