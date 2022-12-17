package main;

import java.util.ArrayList;

public class GameInfo {
    private Player player1;
    private Player player2;
    private int shuffleSeed;
    private int startingPlayer;
    //playerTurn who is playing
    private int playerTurn;

    private int round;
    //e urat rau ca array la table, poate incerc altadata cu Arraylist
    private ArrayList<ArrayList<Minion>> table;
    public GameInfo(final Player player1, final Player player2, final int shuffleSeed,
                            final int startingPlayer) {
        //grija aici ca nu e deepcopy
        this.player1 = new Player(player1);
        this.player2 = new Player(player2);
        this.shuffleSeed = shuffleSeed;
        this.startingPlayer = startingPlayer;
    }

    /**
     * @return get player1
     */
    public Player getPlayer1() {
        return player1;
    }

    /**
     * @param player1 setting first player
     */
    public void setPlayer1(final Player player1) {
        this.player1 = player1;
    }

    /**
     * @return get player2
     */
    public Player getPlayer2() {
        return player2;
    }

    /**
     * @param player2 setting second player
     */
    public void setPlayer2(final Player player2) {
        this.player2 = player2;
    }

    /**
     * @return get ShuffleSeed
     */
    public int getShuffleSeed() {
        return shuffleSeed;
    }

    /**
     * @param shuffleSeed setting shuffleSeed
     */
    public void setShuffleSeed(final int shuffleSeed) {
        this.shuffleSeed = shuffleSeed;
    }

    /**
     * @return get StartingPlayer
     */
    public int getStartingPlayer() {
        return startingPlayer;
    }


    /**
     * @param startingPlayer set the player who start the game
     */
    public void setStartingPlayer(final int startingPlayer) {
        this.startingPlayer = startingPlayer;
    }

    /**
     * @return get the player whose turn is
     */
    public int getPlayerTurn() {
        return playerTurn;
    }

    /**
     * @param playerTurn set the player whose turn is
     */
    public void setPlayerTurn(final int playerTurn) {
        this.playerTurn = playerTurn;
    }

    /**
     * @return get the round of the game
     */
    public int getRound() {
        return round;
    }

    /**
     * @param round set round of the game
     */
    public void setRound(final int round) {
        this.round = round;
    }

    /**
     * @return get the table
     */
    public ArrayList<ArrayList<Minion>> getTable() {
        return table;
    }

    /**
     * @param table set table of the game
     */
    public void setTable(final ArrayList<ArrayList<Minion>> table) {
        this.table = table;
    }

    /**
     * @return initialized table for the game
     */
    public ArrayList<ArrayList<Minion>> initializeTable() {
        table = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            table.add(new ArrayList<>(5));
        }
        return table;
    }


    /**
     * increment rounds of the game
     */
    public void incrementRound() {
        round++;
    }
}
