public interface Player{
    //player interface, each Player class that implements it
    //(every player) has to implement the playTurn function
    void playTurn(Board board, Mark mark);
}