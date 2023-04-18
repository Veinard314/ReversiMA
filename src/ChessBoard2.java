//Вариант с оценкой позиции на основе эвристических коэффициентов клеток
public class ChessBoard2 extends ChessBoard{

    private final int [][] bCoef = {
            {  19,  4, 15, 15, 15, 15,  4, 19 },
            {   4,  2,  5,  5,  5,  5,  2,  4 },
            {  15,  5, 14, 10, 10, 14,  5, 15 },
            {  15,  5, 10, 10, 10, 10,  5, 15 },
            {  15,  5, 10, 10, 10, 10,  5, 15 },
            {  15,  5, 14, 10, 10, 14,  5, 15 },
            {   4,  2,  5,  5,  5,  5,  2,  4 },
            {  19,  4, 15, 15, 15, 15,  4, 19 }
    };

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
}
