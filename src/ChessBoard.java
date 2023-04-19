import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ChessBoard {

    // состояние клетки (chess square)
    public static final int CS_OUT = -1;  // вне пределов игрового поля;
    public static final int CS_EMPTY = 0; // пустая
    public static final int CS_WHITE = 1; // занята белой фишкой
    public static final int CS_BLACK= 2;  // занята черной фишкой


   // смещения вокруг текущей клетки начиная с "10" часов(левый верхний угол)  по часовой стрелке
    private final CPair[] offset = {new CPair(-1,-1), new CPair( 0,-1), new CPair( 1,-1), new CPair(-1, 0), new CPair( 1, 0), new CPair(-1, 1), new CPair( 0, 1), new CPair(1,  1)};

    // важное замечание!
    // таблица коэффициентов должна меняться в зависимости от стадии игры (сколько осталось свободных клеток!)
 /* private final int [][] bCoef = {
            {  25, -10, 15, 15, 15, 15, -10, 25 },
            { -10, -10, -5, -5, -5, -5, -10,-10 },
            {  15, -10, 10, 10, 10, 10, -10, 15 },
            {  15, -10, 10, 10, 10, 10, -10, 15 },
            {  15, -10, 10, 10, 10, 10, -10, 15 },
            {  15, -10, 10, 10, 10, 10, -10, 15 },
            { -10, -10, -5, -5, -5, -5, -10,-10 },
            {  25, -10, 15, 15, 15, 15, -10, 25 }
    };
*/
/*
private final int [][] bCoef = {
        {  120, -20, 20, 5, 5, 20,  -20, 120 },
        { -20, -40, -5, -5, -5, -5, -40,-20 },
        {  20, - 5, 15,  3,  3, 15,  -5, 20 },
        {   5,  -5,  3,  3,  3,  3,  -5,  5 },
        {   5,  -5,  3,  3,  3,  3,  -5,  5 },
        {  20, - 5, 15,  3,  3, 15,  -5, 20 },
        { -20, -40, -5, -5, -5, -5, -40,-20 },
        {  120, -20, 20, 5,  5, 20, -20, 120}
};

 */

    // за один ход можно перевернуть не более 3 полных направлений за минусом ограничивающих фишек (2) на каждом
   private ArrayList<CPair> flippedChips = new ArrayList<>((CBoard.CB_DIM - 2) * 3);
    public void setFlippedChips(ArrayList<CPair> flippedChips) {
        this.flippedChips = flippedChips;
    }
    public ArrayList<CPair> getFlippedChips() {
        return flippedChips;
    }

    /*
    private int lastMoveColor = 0;
    public int getLastMoveColor() {
        return lastMoveColor;
    }
    public void setLastMoveColor(int lastMoveColor) {
        this.lastMoveColor = lastMoveColor;
    }
    */
    //
    private CPair lastMove = new CPair(-1, -1);
    public void setLastMove(CPair lastMove) {
        this.lastMove = lastMove;
    }

    public CPair getLastMove() {
        return lastMove;
    }

    // доска
    private CBoard cb;

    // тестовый счетчик
    public static int n;

    // Признак логирования
    //private static boolean IsLogYes = true;

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
        // Обнуляем переменные последнего хода и перевернутые фишки
        setLastMove(new CPair(-1, -1));
        getFlippedChips().clear();
        //setLastMoveColor(0);
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

    // Главная функция поиска лучшего хода (обертка для работы minimax)
    // для игрока player на доске board составляет список возможных ходов
    // и для каждого из них вызывает функцию minimax.
    // Возвращает количество возможных ходов и координаты лучшего в pair.
    public CPair mainMoveSearch (int player, int depth, CBoard board) {
        // подготавливаем список возможных ходов
        ArrayList<CPair> moves = findPossibleMoves(player, board);

        //
        Instant start = Instant.now();
        n = 0;
        //

        CPair bestMove = new CPair(-1, -1);
        int bestScore = Integer.MIN_VALUE;
        for (CPair t : moves) {
            //делаем ход на новой доске
            CBoard newBoard = makeMove(player, t, board);
            //int currentMove = miniMax(player, depth - 1, newBoard, false);
            int currentMove = miniMaxAlphaBetaPrunning(player, depth - 1, newBoard, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            //String a = "(" + String.valueOf(t.x) + "," + String.valueOf(t.y) + "):" + String.valueOf(currentMove);
            //System.out.println(a);

            if (currentMove > bestScore) { //<??
                bestScore = currentMove;
                bestMove.x = t.x;
                bestMove.y = t.y;
            }
        }
        // выводим время выполнения процедуры
        Instant finish = Instant.now();
        long elapsed = Duration.between(start, finish).toMillis();
        System.out.println("Общее время поиска, мс: " + elapsed);
        //
        return bestMove;
    }

    // То же. что и предыдущая функция поиска лучшего хода, но с использованием мультипоточности.
    public CPair mainMultiThreadMoveSearch (int player, int depth, CBoard board) {
        class mtMinimax implements Callable<Integer> {
            private int player;
            private int depth;
            private CBoard board;
            private boolean maximizedPlayer;
            int alpha;
            int beta;
            public mtMinimax(int player, int depth, CBoard board, boolean maximizedPlayer, int alpha, int beta) {
                this.player = player;
                this.depth = depth;
                this.board = board;
                this.maximizedPlayer = maximizedPlayer;
                this.alpha = alpha;
                this.beta = beta;
            }
            @Override
            public Integer call() throws Exception {
                return miniMaxAlphaBetaPrunning(player, depth, board, maximizedPlayer, alpha, beta);
                // return miniMax(player, depth, board, maximizedPlayer);
            }
        }

        // замеряем время
        Instant start = Instant.now();
        n = 0;
        //

        // Ищем все возможные ходы
        ArrayList<CPair> moves = findPossibleMoves(player, board);
        // Создаем потоки по числу возможных ходов - это не оптимально, рекомендуют не больше, чем ядер у процессора
        //int nThreads = Runtime.getRuntime().availableProcessors(); // определяем число доступных процессоров
        ExecutorService executor = Executors.newFixedThreadPool(moves.size());
        List<Callable<Integer>> tasks = new ArrayList<>();

        for (CPair t : moves) {
            //делаем ход на новой доске
            CBoard newBoard = makeMove(player, t, board);
            //Добавляем новый процесс
            //tasks.add(new mtMinimax(player, depth - 1, newBoard, false));
            tasks.add(new mtMinimax(player, depth - 1, newBoard, false, Integer.MIN_VALUE, Integer.MAX_VALUE));
        }
        // Запускаем коллекцию задач в пуле потоков
        List<Future<Integer>> results = null;
        try {
            results = executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Проверяем, завершились ли все задачи
        // results.stream() создает поток элементов списка results, и мы можем выполнять над этим потоком операции, такие как allMatch:
        // которая проверяет, удовлетворяет ли каждый объект типа Future в списке results методу isDone(), который возвращает true, если задача завершилась
        while (!results.stream().allMatch(Future::isDone)) {
            // Если не все процессы завершены, то ждем
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // Обработка исключения
                e.printStackTrace();
            }
        }
        // Получаем результаты выполнения задач
        // Только как теперь понять, какой результат какому ходу соответствует??? - вроде бы в той же очередности, как добавляли задачи
        CPair bestMove = new CPair(-1, -1);
        int bestScore = Integer.MIN_VALUE;
        for (int i = 0; i < moves.size(); i++) {
            Future<Integer> result = results.get(i);
            int currentMove = Integer.MIN_VALUE;
            try {
                currentMove = result.get();
                // обработка результата
            } catch (InterruptedException e) {
                // обработка InterruptedException
                e.printStackTrace();
            } catch (ExecutionException e) {
                // обработка ExecutionException
                e.printStackTrace();
            }
            //
            //String a = "(" + String.valueOf(moves.get(i).x) + "," + String.valueOf(moves.get(i).y) + "):" + String.valueOf(currentMove);
            //System.out.println(a);
            //
            if (currentMove > bestScore) {
                bestScore = currentMove;
                bestMove.x = moves.get(i).x;
                bestMove.y = moves.get(i).y;

            }
        }
        executor.shutdown();

        // выводим время выполнения процедуры
        Instant finish = Instant.now();
        long elapsed = Duration.between(start, finish).toMillis();
        System.out.println("Threads.Общее время поиска, мс: " + elapsed);
        //

        return bestMove;
    }

    // Метод minimax c альфа-бета отсечением.
    // поиск ВСЕГДА ведется для игрока player.
    // на ходе player ищется максимальное значение оценочной функции, на ходе соперника - минимальное для игрока player
    // отсечение:
    /*
    Параметры alpha и beta используются для обрезки дерева поиска. Альфа - это лучший результат, которого на данный момент достиг максимизирующий игрок,
    а бета - это лучший результат, которого на данный момент достиг минимизирующий игрок.
    Если в какой-либо момент во время поиска альфа-значение больше или равно бета-значению, поиск прекращается,
    потому что мы знаем, что текущий игрок может показать результат не лучше, чем лучший результат, найденный на данный момент.
     */
    public int miniMaxAlphaBetaPrunning(int player, int depth, CBoard board, boolean maximizedPlayer, int alpha, int beta ){
        // достигнута предельная глубина расчета
        // либо на доске нет больше ходов для игрока player

        n++;

        if (depth == 0 ) {
            return positionScore(player, board);
        }
        if (maximizedPlayer) { // поиск максимума
            // Ищем все доступные ходы для игрока player
            ArrayList<CPair> moves = findPossibleMoves(player, board);
            //
            if (moves.size() == 0) {return positionScore(player, board);}
            //
            int bestScore = Integer.MIN_VALUE;
            for (CPair t :  moves) {
                // делаем ход
                CBoard newBoard = makeMove(player, t, board);
                // рекурсивно вызываем miniMax для новой доски (после хода player), с уменьшенной глубиной и с минимизацией;
                int score = miniMaxAlphaBetaPrunning(player, depth - 1, newBoard, false, alpha, beta);
                bestScore = Math.max(bestScore, score);
                // проверка альфа-бета
                alpha = Math.max(alpha, score);
                if (beta <= alpha) {
                    break;
                }
            }
            return bestScore;
        } else {  //поиск минимума
            int player2 = (player == CS_WHITE) ? CS_BLACK : CS_WHITE;
            ArrayList<CPair> moves = findPossibleMoves(player2, board);
            //
            if (moves.size() == 0) {return positionScore(player, board);}
            //
            int bestScore = Integer.MAX_VALUE;
           for (CPair t :  moves) {
                // делаем ход (для противника player, т.к. ветка минимизации)
                CBoard newBoard = makeMove(player2, t, board);
                // рекурсивно вызываем miniMax для новой доски (после хода player2, но для player!), с уменьшенной глубиной и с максимизацией;
                int score = miniMaxAlphaBetaPrunning(player, depth - 1, newBoard, true, alpha, beta);
                bestScore = Math.min(bestScore, score);
                // проверка альфа-бета
                beta = Math.min(beta, score);
                if (beta <= alpha) {
                    break;
                }
            }
            return bestScore;
        }
    }


    // Метод minimax
    // поиск ВСЕГДА ведется для игрока player.
    // на ходе player ищется максимальное значение оценочной функции, на ходе соперника - минимальное для игрока player
    public int miniMax(int player, int depth, CBoard board, boolean maximizedPlayer){
        // достигнута предельная глубина расчета
        // либо на доске нет больше ходов для игрока player

        n++;

        // есть вопрос - проверяем отсутствие хода только для PLAYER
        // может нужно для текущего игрока или вообще на общее отсутсвие ходов?
        /*
        if ((depth == 0) || (notCanMakeMove(player, board))) {
            return positionScore(player, board);
        }
        */
        // Это вариант на общее отсутствие ходов - тоже неверно, т.к. если для текущего нет ходов, функция вернет +-maxint
        if (depth == 0) {
            return positionScore(player, board);
        }

        if (maximizedPlayer) { // поиск максимума
            // Ищем все доступные ходы для игрока player
            ArrayList<CPair> moves = findPossibleMoves(player, board);
            //
            if (moves.size() == 0) {return positionScore(player, board);}
            //
            int bestScore = Integer.MIN_VALUE;
            for (CPair t :  moves) {
                // делаем ход
                CBoard newBoard = makeMove(player, t, board);
                // рекурсивно вызываем miniMax для новой доски (после хода player), с уменьшенной глубиной и с минимизацией;
                int score = miniMax(player, depth - 1, newBoard, false);
                bestScore = Math.max(bestScore, score);
            }
            return bestScore;
        } else {  //поиск минимума
            int player2 = (player == CS_WHITE) ? CS_BLACK : CS_WHITE;
            ArrayList<CPair> moves = findPossibleMoves(player2, board);
            //
            if (moves.size() == 0) {return positionScore(player, board);}
            //
            int bestScore = Integer.MAX_VALUE;
            for (CPair t :  moves) {
                // делаем ход (для противника player, т.к. ветка минимизации)
                CBoard newBoard = makeMove(player2, t, board);
                // рекурсивно вызываем miniMax для новой доски (после хода player2, но для player!), с уменьшенной глубиной и с максимизацией;
                int score = miniMax(player, depth - 1, newBoard, true);
                bestScore = Math.min(bestScore, score);
            }
            return bestScore;
        }
    }


    // Метод делает случайных ход из возможных правильных
    // анализ никакой не проводится  Используется для сравнения
    CPair randomMove (int player, CBoard board) {
        ArrayList<CPair> moves = new ArrayList<CPair>();
        moves = findPossibleMoves(player, board);
        int nMoves = moves.size();
        if ( nMoves == 0 ) { return new CPair(-1, -1); }
        else {return moves.get((int) Math.random() * nMoves);}
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
    /*
    public int positionScore(int player, CBoard board) {
        int score = 0;
        int player2 = (player == CS_WHITE) ? CS_BLACK : CS_WHITE;
        for (int j = 1; j < CBoard.CB_YHEIGHT - 1; j++) {
            for (int i = 1; i < CBoard.CB_XWIDTH - 1; i++) {
                int square = board.get(i, j);
                if (square == player) {
                    score += bCoef[i - 1][j - 1];
                } else if (square == player2) {
                    score -= bCoef[i - 1][j - 1];
                }
            }
        }
        return score;
    }
*/

//  оценка позиции без коэффициентов, просто по подсчету (разнице) количества ч/б фишек

    public int positionScore(int player, CBoard board) {
        int score = 0;
        int player2 = (player == CS_WHITE) ? CS_BLACK : CS_WHITE;
        for (int j = 1; j < CBoard.CB_YHEIGHT - 1; j++) {
            for (int i = 1; i < CBoard.CB_XWIDTH - 1; i++) {
                int square = board.get(i, j);
                if (square == player) {
                    score++;
                } else if (square == player2) {
                    score--;
                }
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

    // возвращает true если нет ходов для ВСЕХ игроков
    public boolean isGameOver (CBoard board) {
        return (findPossibleMoves(ChessBoard.CS_WHITE, board).size() == 0) && (findPossibleMoves(ChessBoard.CS_BLACK, board).size() == 0);
    }

    //----------------------------------------------------------------------//
    // Новое поколение процедур

    // Поиск ВСЕХ доступных ходов для Игрока player для текущей позиции на доске board
    // возвращает _список_ ArrayList<CPair> ходов
    public ArrayList<CPair> findPossibleMoves(int player, CBoard board) {
        ArrayList<CPair> list = new ArrayList<CPair>();
        for (int j = 1; j <= CBoard.CB_DIM; j++){
            for (int i = 1; i <= CBoard.CB_DIM; i++) {
                if (isLegalMoveNew(i, j, player, board)) {
                        list.add(new CPair(i, j));
                    }
                }
            }
        return list;
    }
    // Выполняет ход игрока player(move) на вновь созданной доске
    // возвращает новую доску
    public CBoard makeMove (int player, CPair move, CBoard board){
        CBoard newBoard = board.clone();
        ArrayList<CPair> toFlip = new ArrayList<CPair>((CBoard.CB_DIM - 2) * 3);
        findFlippedChipsNew(move.x, move.y, player, toFlip, newBoard);
        flipChipsNew(move.x, move.y, player, toFlip, newBoard);
        return newBoard;
    }

/*
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
  */
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



