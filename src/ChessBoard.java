import java.util.ArrayList;

public class ChessBoard {

    // состояние клетки (chess square)
    public static final int CS_OUT = -1;  // вне пределов игрового поля;
    public static final int CS_EMPTY = 0; // пустая
    public static final int CS_WHITE = 1; // занята белой фишкой
    public static final int CS_BLACK= 2;  // занята черной фишкой


   // смещения вокруг текущей клетки начиная с "10" часов(левый верхний угол)  по часовой стрелке
    private final CPair[] offset = {new CPair(-1,-1), new CPair( 0,-1), new CPair( 1,-1), new CPair(-1, 0), new CPair( 1, 0), new CPair(-1, 1), new CPair( 0, 1), new CPair(1,  1)};

    // за один ход можно перевернуть не более 3 полных направлений за минусом ограничивающих фишек (2) на каждом
   private final ArrayList<CPair> flippedChips = new ArrayList<>((CBoard.CB_DIM - 2) * 3);


    // доска
    private CBoard cb;

    // Возвращает содержимое клетки доски
    public int GetSquare(CPair pair) {
        return cb.get(pair.x, pair.y);
    }
    public int GetSquare(int x, int y) {
        return cb.get(x,y);
    }

    // Устанавливает содержимое клетки
    public void SetSquare (int x, int y, int value) {
        cb.set(x, y, value);
    }
    public void SetSquare (CPair pair, int value) {
        cb.set(pair.x, pair.y, value);
    }

    public ChessBoard() {
      cb = new CBoard();
    }
    public ChessBoard(CBoard board) {
        //cb = new CBoard();
        try {
            cb = board.clone();
        } catch (Exception e) {
            System.out.println(":jgf");
            //cb = null;
        }

    }

    public void initBoard() {
        for (int j = 0; j < CBoard.CB_YHEIGHT; j++) {
                 for (int i = 0; i < CBoard.CB_XWIDTH; i++) {
                     if (i > 0 && i < CBoard.CB_XWIDTH - 1 && j > 0 && j < CBoard.CB_YHEIGHT - 1) SetSquare(i ,j, CS_EMPTY);
                     else SetSquare(i, j, CS_OUT);
            }
        }
        SetSquare(4, 4, CS_BLACK);
        SetSquare(5, 4, CS_WHITE);
        SetSquare(4, 5, CS_WHITE);
        SetSquare(5, 5, CS_BLACK);
    }

    // возвращает число фишек (chips) определенного цвета на доске;
    // также можно использовать для подсчета пустых клеток
    public int countChips(int value){
        int count = 0;
        for (int j = 1; j < CBoard.CB_YHEIGHT - 1; j++) {
            for (int i = 1; i < CBoard.CB_XWIDTH - 1; i++) {
                if (GetSquare(i, j) == value) count++;
            }
        }
        return count;
    }

    // проверяет возможность хода в указанную клетку (x,y) доски
    public boolean isLegalMove(int x, int y, int player) {
        // если клетка не пустая сразу завершаем;
        if (GetSquare(x,y) != CS_EMPTY) return false;
        // пока так
        return (findFlippedChips(x,y,player) > 0);
    }

    // формируем список фишек, которые будут перевернуты после хода
    // игрока player (CS_WHITE/CS_BLACK) в клетку (x,y) доски
    public int findFlippedChips(int x, int y, int player) {
        int player2 = (player == CS_WHITE) ? CS_BLACK : CS_WHITE;

        int ix, iy, ofX, ofY, count;

        flippedChips.clear();

        for (int i = 0; i < 8; i++) {

            count = 0;
            ofX = offset[i].x;
            ofY = offset[i].y;
            ix = x;
            iy = y;

            // проверяем текущее направление - считаем фишки врага
            do {
                ix += ofX;
                iy += ofY;
                count++;

            } while (GetSquare(ix, iy) == player2);
            // если последняя найденная фишка - наша, добавляем переворачивыемые фишки в список
            if ((GetSquare(ix, iy) == player) && (count > 1)) { //исключаем вариант, когда соседняя фишка - наша
                count--;
                for (int j = 0; j < count; j++){
                    // заполняем с хвоста;
                    ix -= ofX;
                    iy -= ofY;
                    flippedChips.add(new CPair(ix, iy));
                 }
            }
        }
        //
        return flippedChips.size();
    }

    // переворачивает найденные фишки на доске
    public void flipChips(int value) {
        for (CPair flippedChip : flippedChips) {
            SetSquare(flippedChip, value);
        }
    }

    //Анализирует доску на предмет окончания партии
    // - есть ли пустые клетки?
    // - если да, то можно ли в них сделать ход?
    //public int isFinished() {
    //}

    //

    public int Test(int player, CPair pair) {
        int possibleMoves = 0;
        int bestScore = Integer.MIN_VALUE;
        //CPair pair = new CPair(0,0);
        for (int j = 1; j <= CBoard.CB_DIM; j++){
            for (int i = 1; i<= CBoard.CB_DIM; i++) {
                if (GetSquare(i, j) == CS_EMPTY) {
                    int res = findFlippedChips(i, j, player);
                    if (res > 0) {
                        possibleMoves++;
                        if (bestScore < res) {
                            bestScore = res;
                            pair.x = i;
                            pair.y = j;
                        }
                    }
                }
            }
        }
        return possibleMoves;
    }

    // временный тестовый метод
    public void showBoard () {
        for (int j = 0; j < CBoard.CB_YHEIGHT; j++) {
            for (int i = 0; i < CBoard.CB_XWIDTH; i++) {
                switch (GetSquare(i,j)) {
                    case CS_EMPTY -> System.out.print("*");
                    case CS_BLACK -> System.out.print("B");
                    case CS_WHITE -> System.out.print("W");
                    case CS_OUT -> System.out.print(".");
                }
            }
            System.out.println();
        }

    }
}



