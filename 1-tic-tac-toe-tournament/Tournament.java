public class Tournament {
    int rounds;
    Renderer renderer;
    Player player1;
    Player player2;

    //constructor
    public Tournament(int rounds, Renderer renderer, Player player1, Player player2){
        this.rounds = rounds;
        this.renderer = renderer;
        this.player1 = player1;
        this.player2 = player2;
    }


    //plays several rounds and counts the results of each player
    public void playTournament(int size, int winStreak, String playerName1, String playerName2){
        //counter for each player wins and ties as well
        int p1Wins = 0;
        int p2Wins = 0;
        int ties = 0;

        for(int round = 0; round < this.rounds; round++){
            Player currentX;
            Player currentO;

            //switches signs after each round
            if (round % 2 == 0){
                currentX = this.player1;
                currentO = this.player2;
            }
            else {
                currentX = this.player2;
                currentO = this.player1;
            }
            //starts a new game for this round
            Game game = new Game(currentX, currentO, size, winStreak, this.renderer);
            Mark winner = game.run();
            //updates counters depending on the winner
            if (winner == Mark.X) {
                if (currentX == this.player1) {
                    p1Wins++;
                } else {
                    p2Wins++;
                }
            }
            else if (winner == Mark.O) {
                if (currentO == this.player1) {
                    p1Wins++;
                } else {
                    p2Wins++;
                }
            }
            else {
                ties++;
            }
        }
        //printing results according to the format
        System.out.println("########## Results ##########");
        System.out.println("Player 1, " + playerName1 + " won: " + p1Wins + " rounds");
        System.out.println("Player 2, " + playerName2 + " won: " + p2Wins + " rounds");
        System.out.print("Ties: " + ties);
    }

    //main function to start the tournament with command-line args
    public static void main(String[] args) {
        //creating the game according to the parameters
        int rounds = Integer.parseInt(args[0]);
        int size = Integer.parseInt(args[1]);
        int winStreak = Integer.parseInt(args[2]);
        String renderTypeName = args[3];
        String player1Name = args[4];
        String player2Name = args[5];

        //creates factories for players and renderer
        PlayerFactory playerFactory = new PlayerFactory();
        RendererFactory rendererFactory = new RendererFactory();


        //build renderer and players according to input names
        Renderer renderer = rendererFactory.buildRenderer(renderTypeName, size);
        Player player1 = playerFactory.buildPlayer(player1Name);
        Player player2 = playerFactory.buildPlayer(player2Name);

        //check for invalid renderer or players
        if (renderer == null) {
            System.err.println("Error: Unknown renderer type.");
            return;
        }
        if (player1 == null || player2 == null) {
            System.err.println("Error: Unknown player type.");
            return;
        }

        //creates and run the game!
        Tournament tournament = new Tournament(rounds, renderer, player1, player2);
        tournament.playTournament(size, winStreak, player1Name, player2Name);
    }
}
