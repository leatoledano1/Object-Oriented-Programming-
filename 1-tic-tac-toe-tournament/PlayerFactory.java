public class PlayerFactory {
    //a constructor for a player factory
    PlayerFactory(){}

    //a player factory who create the player according to the user input
    public Player buildPlayer(String type){
        String lowType = type.toLowerCase();

        switch (lowType){
            case "naive":
                return new NaivePlayer();
            case "smart":
                return new SmartPlayer();
            case "whatever":
                return new WhateverPlayer();
            case "human":
                return new HumanPlayer();
            default:
                return null;
        }
    }
}
