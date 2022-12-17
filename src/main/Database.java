package main;

import java.util.ArrayList;
import java.util.Arrays;

public final class Database {
    private static Database database = null;

    public static final ArrayList<String> ENVIRONMENT_CARDS = new ArrayList<String>() {
        {
            add("Winterfell");
            add("Heart Hound");
            add("Firestorm");
        }
    };

    public static final ArrayList<String> MINION_CARDS = new ArrayList<String>() {
        {
            add("Sentinel");
            add("Goliath");
            add("Berserker");
            add("Warden");
        }
    };

    public static final ArrayList<String> SPECIAL_MINION_CARDS = new ArrayList<String>() {
        {
            add("Miraj");
            add("The Ripper");
            add("Disciple");
            add("The Cursed One");
        }
    };
    public static final ArrayList<String> FRONT_ROW_CARDS = new ArrayList<String>() {
        {
            addAll(Arrays.asList("The Ripper", "Miraj", "Goliath", "Warden"));
        }
    };
    public static final ArrayList<String> BACK_ROW_CARDS = new ArrayList<String>() {
        {
            addAll(Arrays.asList("Sentinel", "Berserker", "The Cursed One", "Disciple"));
        }
    };

    public static final int FRONTROW1 = 2;

    public static final int FRONTROW2 = 1;

    public static final int BACKROW1 = 3;

    public static final int BACKROW2 = 0;

    public static final int MAXMANA = 10;
    private ArrayList<ArrayList<Card>> decksPlayer1 = new ArrayList<>();
    private ArrayList<ArrayList<Card>> decksPlayer2 = new ArrayList<>();

    private GameInfo game;

    public GameInfo getGame() {
        return game;
    }

    public void setGame(final GameInfo game) {
        this.game = game;
    }

    private Database() {
    }

    /**
     * @return the Database for the game
     */
    public static Database getDatabase() {
        if (database == null) {
            database = new Database();
        }
        return database;
    }

    public ArrayList<ArrayList<Card>> getDecksPlayer1() {
        return decksPlayer1;
    }

    public void setDecksPlayer1(final ArrayList<ArrayList<Card>> decksPlayer1) {
        //grija ca nu e deep copy
        this.decksPlayer1 = decksPlayer1;
    }

    public ArrayList<ArrayList<Card>> getDecksPlayer2() {
        return decksPlayer2;
    }

    public void setDecksPlayer2(final ArrayList<ArrayList<Card>> decksPlayer2) {
        this.decksPlayer2 = decksPlayer2;
    }

}
