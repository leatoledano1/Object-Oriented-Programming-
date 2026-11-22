import java.util.Random;

public class WhateverPlayer implements Player{
    //constructor for whatever player
    public WhateverPlayer(){}

    //a player that plays randomly, very low strategy
    @Override
    public void playTurn(Board board, Mark mark){
        boolean available = false;
        Random rand = new Random();
        int n = board.getSize();

        //picks a random number for each col and row respecting the board boundaries
        while(!available){
            int row =  rand.nextInt(n);
            int col = rand.nextInt(n);

            //checking each time he picks a random number if the spot is free
            if(board.putMark(mark, row,col)){
                available = true;
            }
        }
    }
}
