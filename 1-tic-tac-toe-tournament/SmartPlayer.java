public class SmartPlayer implements Player {
    //constructor for human player
    public SmartPlayer(){
    }

    @Override
    public void playTurn(Board board, Mark mark){
        int n = board.getSize();
        Mark otherMark;
        if (mark == Mark.X) {
            otherMark = Mark.O;
        } else {
            otherMark = Mark.X;
        }

        //we will compare our options then choose how to play depending on
        //the board now and where is the best potential to win the other player
        int bestRow = -1;
        int bestCol = -1;
        int maxScore = -1;

        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {

                if (board.getMark(r, c) == Mark.BLANK) {
                    // the goal is to choose each time our best options
                    //we give a different "grade" depending on the position
                    int currentScore = 0;

                    //checks all 8 directions around the cell
                    for (int dr = -1; dr <= 1; dr++) {
                        for (int dc = -1; dc <= 1; dc++) {
                            //10 points if next to own mark, 5 if next to other player so we can block him
                            if (dr != 0 || dc != 0) {
                                Mark neighborMark = board.getMark(r + dr, c + dc);
                                if (neighborMark == mark) {
                                    currentScore = currentScore + 10;
                                } else if (neighborMark == otherMark) {
                                    currentScore = currentScore + 6;
                                }
                            }
                        }
                    }
                    //bonus for center and corner
                    if (isCenter(r, c, n)) {
                        currentScore = currentScore + 3;
                    } else if (isCorner(r, c, n)) {
                        currentScore = currentScore + 2;
                    }
                    if (currentScore > maxScore) {
                        maxScore = currentScore;
                        bestRow = r;
                        bestCol = c;
                    }
                }
            }
        }
        //make the best move!!
        board.putMark(mark, bestRow, bestCol);
    }


    //I added helper functions but made them private so the API won't change

    //giving a small "bonus" if the cell is center or corner, these
    //positions are usually better than central cells in the board
    private boolean isCenter(int r, int c, int n) {
        int center = n / 2;

        boolean isCenterExactly = (r == center && c == center);
        int adjust = 1 - (n % 2);
        boolean isCenteradjust = (r == center - adjust && c == center);

        return isCenterExactly || isCenteradjust;
    }


    private boolean isCorner(int r, int c, int n) {
        int last = n - 1;

        boolean isTopLeft = (r == 0 && c == 0);
        boolean isTopRight = (r == 0 && c == last);
        boolean isBottomLeft = (r == last && c == 0);
        boolean isBottomRight = (r == last && c == last);

        return isTopLeft || isTopRight || isBottomLeft || isBottomRight;
    }

}