import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

//Вариант с оценкой позиции на основе эвристических коэффициентов клеток
public class ChessBoard2 extends ChessBoard{

    // важное замечание!
    // возможно таблица коэффициентов должна меняться в зависимости от стадии игры (сколько осталось свободных клеток!)

    private final int [][] bCoef1 = {
            {  19,  4, 15, 15, 15, 15,  4, 19 },
            {   4,  2,  5,  5,  5,  5,  2,  4 },
            {  15,  5, 14, 10, 10, 14,  5, 15 },
            {  15,  5, 10, 10, 10, 10,  5, 15 },
            {  15,  5, 10, 10, 10, 10,  5, 15 },
            {  15,  5, 14, 10, 10, 14,  5, 15 },
            {   4,  2,  5,  5,  5,  5,  2,  4 },
            {  19,  4, 15, 15, 15, 15,  4, 19 }
    };
/*
    private final int [][] bCoef1 = {
            {  1,  1, 1, 1, 1, 1,  1, 1 },
            {  1,  1, 1, 1, 1, 1,  1, 1 },
            {  1,  1, 1, 1, 1, 1,  1, 1 },
            {  1,  1, 1, 1, 1, 1,  1, 1 },
            {  1,  1, 1, 1, 1, 1,  1, 1 },
            {  1,  1, 1, 1, 1, 1,  1, 1 },
            {  1,  1, 1, 1, 1, 1,  1, 1 },
            {  1,  1, 1, 1, 1, 1,  1, 1 },
    };
*/

    private final int [][] bCoef2 = {
            { 120, -20, 20, 5, 5, 20,  -20, 120 },
            { -20, -40, -5, -5, -5, -5, -40,-20 },
            {  20, - 5, 15,  3,  3, 15,  -5, 20 },
            {   5,  -5,  3,  3,  3,  3,  -5,  5 },
            {   5,  -5,  3,  3,  3,  3,  -5,  5 },
            {  20, - 5, 15,  3,  3, 15,  -5, 20 },
            { -20, -40, -5, -5, -5, -5, -40,-20 },
            {  120, -20, 20, 5,  5, 20, -20, 120}
    };
    private int [][] bCoef = bCoef1;
    public void setbCoef (int mode) {
        switch (mode) {
            case 1 :  bCoef = bCoef1;
                      break;
            case 2 :  bCoef = bCoef2;
                      break;
            default:
                break;
        }
    }

    // возвращает оценку позиции игрока player на доске (board);
    // с учетом эвристических коэфициентов занятых игроками клеток
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


    /* одинаковое оценки встречаются крайне редко, нет смысла их искать...
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

        // Ищем все возможные ходы
        ArrayList<CPair> moves = findPossibleMoves(player, board);

        //
        int [] resultsForIndeterminate = new int[moves.size()];
        //

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
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // Обработка исключения
                e.printStackTrace();
            }
        }
        // Получаем результаты выполнения задач
        for (int i = 0; i < moves.size(); i++) {
            Future<Integer> result = results.get(i);
            try {
                resultsForIndeterminate[i] = result.get();
                // обработка результата
            } catch (InterruptedException e) {
                // обработка InterruptedException
                e.printStackTrace();
            } catch (ExecutionException e) {
                // обработка ExecutionException
                e.printStackTrace();
            }
        }
        executor.shutdown();

        //Ищем лучший ход
        CPair bestMove = new CPair(-1, -1);
        int bestScore = Integer.MIN_VALUE;
        //выбираем лучший результат
        for (int i = 0; i < moves.size(); i++){
            if (resultsForIndeterminate[i] > bestScore) {
                bestScore = resultsForIndeterminate[i];
                bestMove.x = moves.get(i).x;
                bestMove.y = moves.get(i).y;
            }
        }
        // если одинаково хороших ходов несколько, выбираем один случайным образом
        ArrayList<CPair> bestMoves = new ArrayList<CPair>();
        int numBestMoves = 0;
        for (int i = 0; i < moves.size(); i++){
            if (resultsForIndeterminate[i] == bestScore) {
                numBestMoves++;
                bestMoves.add(moves.get(i));
            }
        }
        bestMove = bestMoves.get((int) Math.random()*numBestMoves);
        return bestMove;
    }
*/
}
