public class NaivePlayer implements Player{
    //constructor for naive player
    public NaivePlayer(){}

    //a player that catches the first free spot, very low strategy
    @Override
    public void playTurn(Board board, Mark mark){
        int n = board.getSize();
        boolean found = false;

        //tries to put his mark at the first spot he can
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(board.putMark(mark, i, j)){
                    found = true;
                    break;
                }
            }
            if(found){
                break;
            }
        }
    }
}
