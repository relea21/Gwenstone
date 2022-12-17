package main;

public class Statistics {

    private int games = 0;

    private int gamesWinPlayer1 = 0;

    private int gamesWinPlayer2 = 0;

    /**
     * @return get number of games played
     */
    public int getGames() {
        return games;
    }

    /**
     * @return get number of games win by player1
     */
    public int getGamesWinPlayer1() {
        return gamesWinPlayer1;
    }

    /**
     * @return get number of games win by player2
     */
    public int getGamesWinPlayer2() {
        return gamesWinPlayer2;
    }


    /**
     * increment number of games
     */
    public void incrementGames() {
        games++;
    }

    /**
     * increment number of games win by player1
     */
    public void incrementGamesWinPlayer1() {
        gamesWinPlayer1++;
    }

    /**
     * increment number of games win by player2
     */
    public void incrementGamesWinPlayer2() {
        gamesWinPlayer2++;
    }

}
