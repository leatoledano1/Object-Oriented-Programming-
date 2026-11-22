import java.util.Scanner;

public class HumanPlayer implements Player{
    //constructor for human player
    public HumanPlayer(){}

    //human player, the user plays, no need for strategy
    @Override
    public void playTurn(Board board, Mark mark){
        //asking the user where he wants to put his mark
        System.out.printf("Player %s, type coordinates: ", mark);
        boolean Worked = false;

        //translating the input to coordinates
        while(!Worked){
            int coord = KeyboardInput.readInt();
            int row = (coord - coord%10)/10;
            int col = coord%10;

            //boundaries check
            if(row>= board.getSize() || col>= board.getSize() || row<0 || col<0){
                System.out.println("Invalid mark position. Please choose a valid position: ");
            }
            //using the put mark method
            else if(board.putMark(mark,row, col)){
                Worked = true;
            }
            else{
                System.out.println("Mark position is already occupied. Please choose a valid position: ");
            }
        }
    }
}