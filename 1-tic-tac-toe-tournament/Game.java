public class Game {
    private Player playerX;
    private Player playerO;
    private Renderer renderer;
    private Board board;
    private int winStreak;

    //default game constructor (4x4 board, winstreak = 3)
    public Game(Player playerX, Player playerO, Renderer renderer){
        this.board = new Board();
        this.renderer = renderer;
        this.playerX = playerX;
        this.playerO = playerO;
        this.winStreak = 3;
    }

    //a constructor that create a single game according to the user demand
    public Game(Player playerX,Player playerO, int size, int winStreak,Renderer renderer){
        this.board = new Board(size);
        this.winStreak = winStreak;
        this.renderer = renderer;
        this.playerX = playerX;
        this.playerO = playerO;
    }

    //a getter for the winstreak
    public int getWinStreak(){
        return this.winStreak;
    }

    //a getter for the board size
    public int getBoardSize(){
        return this.board.getSize();
    }

    //private helper functions to check if the game is over
    private boolean isBoardFull() {
        for (int i = 0; i < this.getBoardSize(); i++) {
            for (int j = 0; j < this.getBoardSize(); j++) {
                if (this.board.getMark(i,j) == Mark.BLANK) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkRows(Mark mark){
        int winStreak = getWinStreak();

        for(int row = 0; row < this.board.getSize(); row++){
            int counter = 0;

            for(int col = 0; col < this.board.getSize(); col++){
                if(this.board.getMark(row,col) == mark){
                    counter++;
                    if (counter >= winStreak){
                        return true;
                    }
                }
                else {
                    counter = 0;
                }
            }
        }
        return false;
    }

    private boolean checkCols(Mark mark){
        int winStreak = this.getWinStreak();

        for(int col = 0; col < this.board.getSize(); col++){
            int counter = 0;

            for(int row = 0; row < this.board.getSize(); row++){
                if(this.board.getMark(row,col) == mark){
                    counter++;
                    if (counter >= winStreak){
                        return true;
                    }
                }
                else {
                    counter = 0;
                }
            }
        }
        return false;
    }

    private boolean checkDiagonals(Mark mark){
        int winStreak = this.getWinStreak();

        for (int rowStart = 0; rowStart <= this.board.getSize() - winStreak; rowStart++) {
            for (int colStart = 0; colStart <= this.board.getSize()  - winStreak; colStart++) {
                boolean win = true;
                for (int k = 0; k < winStreak; k++) {
                    if (this.board.getMark(rowStart + k, colStart + k) != mark) {
                        win = false;
                        break;
                    }
                }
                if (win) {
                    return true;
                }
            }
        }
        for (int rowStart = 0; rowStart <= this.board.getSize() - winStreak; rowStart++) {
            for (int colStart = winStreak - 1; colStart < this.board.getSize(); colStart++) {
                boolean win = true;
                for (int k = 0; k < winStreak; k++) {
                    if (this.board.getMark(rowStart + k, colStart - k) != mark) {
                        win = false;
                        break;
                    }
                }
                if (win) {
                    return true;
                }
            }
        }
        return false;
    }

    //if we got a winstreak long same mark for row||col||diag we've got a winner!
    private boolean checkWin(Mark mark) {
        if (checkRows(mark)) {
            return true;
        }
        if (checkCols(mark)) {
            return true;
        }
        if (checkDiagonals(mark)) {
            return true;
        }
        return false;
    }

    //runs one full game until someone wins or it's a tie
    public Mark run(){
        //We count moves to check whose turn it is
        int movesNum = 0;

        //A loop that won't stop before there is a winner/teqo
        while(true){
            Player currentPlayer;
            Mark currentMark;

            //X starts
            if(movesNum%2 == 0){
                currentPlayer = playerX;
                currentMark = Mark.X;
            }
            else{
                currentPlayer = playerO;
                currentMark = Mark.O;
            }

            //The player will play his turn, each player knows what implementations
            //he's supposed to use based on what player he is.
            currentPlayer.playTurn(this.board, currentMark);

            // Show the board after each move
            this.renderer.renderBoard(this.board);

            // use a helper function to check is someone won
            if (this.checkWin(currentMark)) {
                return currentMark;
            }

            //helper function to check if the bard is full
            if(this.isBoardFull()){
                return Mark.BLANK;
            }

            movesNum++;
        }
    }
}
