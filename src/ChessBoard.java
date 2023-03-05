import java.util.ArrayList;

public class ChessBoard {

    // состояние клетки (chess square)
    public static final int CS_OUT = -1;  // вне пределов игрового поля;
    public static final int CS_EMPTY = 0; // пустая
    public static final int CS_WHITE = 1; // занята белой фишкой
    public static final int CS_BLACK= 2;  // занята черной фишкой


   // смещения вокруг текущей клетки начиная с "10" часов(левый верхний угол)  по часовой стрелке
    private final CPair[] offset = {new CPair(-1,-1), new CPair( 0,-1), new CPair( 1,-1), new CPair(-1, 0), new CPair( 1, 0), new CPair(-1, 1), new CPair( 0, 1), new CPair(1,  1)};

    private final int [][] bCoef = {
            {  25, -10, 15, 15, 15, 15, -10, 25 },
            { -10, -10, -5, -5, -5, -5, -10,-10 },
            {  15, -10, 10, 10, 10, 10, -10, 15 },
            {  15, -10, 10, 10, 10, 10, -10, 15 },
            {  15, -10, 10, 10, 10, 10, -10, 15 },
            {  15, -10, 10, 10, 10, 10, -10, 15 },
            { -10, -10, -5, -5, -5, -5, -10,-10 },
            {  25, -10, 15, 15, 15, 15, -10, 25 }
    };

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
         cb = board.clone();
    }

    public CBoard getCBoard () {
        return cb;
    }
    // Устанавливает на поле стандартную первоначальную позицию, инициализирует в т.ч. края поля.
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
        return countChipsNew(value, cb);
   }

    // проверяет возможность хода в указанную клетку (x,y) доски
    public boolean isLegalMove(int x, int y, int player) {
        return isLegalMoveNew(x, y, player, cb);
    }

    // формируем список фишек, которые будут перевернуты после хода
    // игрока player (CS_WHITE/CS_BLACK) в клетку (x,y) доски
    public int findFlippedChips(int x, int y, int player) {
        return findFlippedChipsNew(x, y, player, flippedChips, cb);
    }

    // переворачивает найденные фишки на доске
    public void flipChips(int value) {
        for (CPair flippedChip : flippedChips) {
            SetSquare(flippedChip, value);
        }
    }



    // тестовый метод: для Игрока player возвращает количество возможных (допустимых)
    // ходов при текущей позиции на доске cb, координаты (pair) лучшего хода для Игрока player
    // (на основе количества переворачиваемых после хода фишек)
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
    // Второй тестовый метод: для Игрока player возвращает _список_ возможных (допустимых)
    // ходов при текущей позиции на доске cb с о
    public void Test2(int player, ArrayList<CPair> listValues) {
        listValues.clear();
        for (int j = 1; j <= CBoard.CB_DIM; j++){
            for (int i = 1; i<= CBoard.CB_DIM; i++) {
                if (GetSquare(i, j) == CS_EMPTY) {
                    int res = findFlippedChips(i, j, player);
                    if (res > 0) {
                        listValues.add(new CPair(i, j));
                        String a = "(" + String.valueOf(i) + "," + String.valueOf(j) + "):" + String.valueOf(res);
                        System.out.println(a);
                    }
                }
            }
        }
    }

    // Третий тестовый метод : оценка позиции не по числу перевернутых фишек,
    // а по подсчету общего числа фишек на доске после хода.
    // возвращает оценку и соответствующий ей лучший ход (pair)(?).
    // четность/нечетность depth - важна?
    public int Test3(int player, int depth, CBoard board, CPair pair) {
        // достигнута предельная глубина расчета
        // либо на доске нет больше ходов для игрока player
        if ((depth == 0) || (notCanMakeMove(player, board))) {
            pair.x = 0;
            pair.y = 0;
            return positionScore(player, board);
        }
        ArrayList<CPair> toFlip = new ArrayList<CPair>((CBoard.CB_DIM - 2) * 3);
        ArrayList<VPair> treeMoves = new ArrayList<VPair>();
        for (int j = 1; j <= CBoard.CB_DIM; j++){
            for (int i = 1; i<= CBoard.CB_DIM; i++) {
                if (board.get(i, j) == CS_EMPTY) {
                    int res = findFlippedChipsNew(i, j, player, toFlip, board);
                    if (res > 0) {
                        //создаем новую доску
                        CBoard tmpBoard = board.clone();
                        // делаем ход на ней и применяем изменения (переворачиваем фишки)
                        flipChipsNew(i, j, player, toFlip, tmpBoard);
                        int oppositePlayer = (player == CS_WHITE) ? CS_BLACK : CS_WHITE;
                        CPair tmpPair = new CPair(0, 0);
                        // оцениваем ход противника (oppositePlayer) на вновь получившийся позиции
                        // и вычитаем его из оценки текущей позиции
                        int value = positionScore(player, board) - Test3(oppositePlayer, depth - 1, tmpBoard, tmpPair);
                        //int value = Test3(oppositePlayer, depth - 1, tmpBoard, tmpPair);
                        treeMoves.add(new VPair(i, j, value));
                        String a = "Depth =" + String.valueOf(depth) + ". (" + String.valueOf(i) + "," + String.valueOf(j) + "):" + String.valueOf(value);
                        System.out.println(a);
                    }
                }
            }
        }
        // все клетки просмотрены, составлен список возможных ходов с оценками
        // выбираем из них лучший ход
        int bestScore = Integer.MIN_VALUE;
        for (VPair t : treeMoves) {
            if (t.value > bestScore) {
                bestScore = t.value;
                pair.x = t.x;
                pair.y = t.y;
            }
        }
        return bestScore;
    }


    //Методы, которые работают с прямым указанием доски CBoard
    /*********************************************************/
    // возвращает число фишек (chips) определенного цвета на доске (board);
    // также можно использовать для подсчета пустых клеток
    public int countChipsNew(int value, CBoard board){
        int count = 0;
        for (int j = 1; j < CBoard.CB_YHEIGHT - 1; j++) {
            for (int i = 1; i < CBoard.CB_XWIDTH - 1; i++) {
                if (board.get(i, j) == value) count++;
            }
        }
        return count;
    }
    // возвращает оценку позиции игрока player на доске (board);
    // с учетом эвристических коэфицциентов занятых игроками клеток
    public int positionScore(int player, CBoard board) {
        int score = 0;
        int sign;
        int player2 = (player == CS_WHITE) ? CS_BLACK : CS_WHITE;
        for (int j = 1; j < CBoard.CB_YHEIGHT - 1; j++) {
            for (int i = 1; i < CBoard.CB_XWIDTH - 1; i++) {
                int square = board.get(i, j);
                if (square == player) {
                    sign = 1;
                }
                else if (square == player2) {
                    sign = -1;
                } else sign = 0;
                score += sign * bCoef[i+1][j+1];
            }
        }
        return score;
    }



    // Метод возвращает количество фишек, которые будут перевернуты после хода в клетку (x,y) доски board
    // фишки игрока player (CS_WHITE/CS_BLACK).
    // Формируется также список  (координаты) фишек (toFlip), которые будут перевернуты после хода
     public int findFlippedChipsNew(int x, int y, int player, ArrayList<CPair> toFlip, CBoard board) {
        int player2 = (player == CS_WHITE) ? CS_BLACK : CS_WHITE;
        int ix, iy, ofX, ofY, count;
        toFlip.clear();
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
            } while (board.get(ix, iy) == player2);
            // если последняя найденная фишка - наша, добавляем переворачивыемые фишки в список
            if ((board.get(ix, iy) == player) && (count > 1)) { //исключаем вариант, когда соседняя фишка - наша
                count--;
                for (int j = 0; j < count; j++){
                    // заполняем с хвоста;
                    ix -= ofX;
                    iy -= ofY;
                    toFlip.add(new CPair(ix, iy));
                }
            }
        }
        return toFlip.size();
    }

    // Применяет ход игрока player в клетку (x, y) на доску board
    // найденный методом findFlippedChipsNew (переворачивает фишки сз списка toFlip на доске)
    public void flipChipsNew(int x, int y, int player, ArrayList<CPair> toFlip, CBoard board) {
        board.set(x, y, player);
        for (CPair t : toFlip) {
            board.set(t.x, t.y, player);
        }
    }

    // Проверяет возможность хода в указанную клетку (x,y) доски board фишки игрока player (CS_WHITE/CS_BLACK)
    public boolean isLegalMoveNew(int x, int y, int player, CBoard board) {
        ArrayList<CPair> toFlip = new ArrayList<>();
        // если клетка не пустая сразу завершаем;
        if (board.get(x, y) != CS_EMPTY) return false;
        else return (findFlippedChipsNew(x, y, player, toFlip, board) > 0);
    }

    // Проверяет наличие возможности хода на доске board игрока player
    // заканчивает работу после нахождения первого же возможного варианта
    public boolean notCanMakeMove(int player, CBoard board) {
        for (int j = 1; j < CBoard.CB_YHEIGHT - 1; j++) {
            for (int i = 1; i < CBoard.CB_XWIDTH - 1; i++) {
                if (isLegalMoveNew(i, j, player, board)) return false;
            }
        }
        return true;
    }

    // временный тестовый метод
    public void showBoardNew (CBoard board) {
        for (int j = 0; j < CBoard.CB_YHEIGHT; j++) {
            for (int i = 0; i < CBoard.CB_XWIDTH; i++) {
                switch (board.get(i,j)) {
                    case CS_EMPTY -> System.out.print("*");
                    case CS_BLACK -> System.out.print("B");
                    case CS_WHITE -> System.out.print("W");
                    case CS_OUT -> System.out.print(".");
                    default -> System.out.print("?");
                }
            }
            System.out.println();
        }

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



