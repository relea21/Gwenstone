package main;

import fileio.CardInput;
import fileio.DecksInput;
import fileio.StartGameInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Builder {
    /**
     * @param deckIndex
     * @param hero
     * @param decks
     * @param frontRow
     * @param backRow
     * @param enemyFrontRow
     * @param enemyBackRow
     * @return initialize player
     */
    public static Player initializePlayer(final int deckIndex, CardInput hero,
                                          final ArrayList<ArrayList<Card>> decks,
                                          final int frontRow, final int backRow,
                                          final int enemyFrontRow, final int enemyBackRow) {
        int mana;
        String name, description;
        ArrayList<String> colors;
        mana = hero.getMana();
        name = hero.getName();
        description = hero.getDescription();
        colors = hero.getColors();
        Hero heroPlayer;
        switch (name) {
            case "Lord Royce":
                heroPlayer = new LordRoyce(name, mana, description, colors);
                break;
            case "Empress Thorina":
                heroPlayer =  new EmpressThorina(name, mana, description, colors);
                break;
            case "King Mudface":
                heroPlayer = new KingMudface(name, mana, description, colors);
                break;
            case "General Kocioraw":
                heroPlayer = new GeneralKocioraw(name, mana, description, colors);
                break;
            default:
                heroPlayer = null;

        }
        return new Player(heroPlayer, decks.get(deckIndex),
                frontRow, backRow, enemyFrontRow, enemyBackRow);
    }

    /**
     * @param newGame input for a new game
     */
    public static void initializeNewGame(final StartGameInput newGame) {

        ArrayList<ArrayList<Card>> decksPlayer1 = Database.getDatabase().getDecksPlayer1();
        ArrayList<ArrayList<Card>> decksPlayer2 = Database.getDatabase().getDecksPlayer2();

        Player player1 = initializePlayer(newGame.getPlayerOneDeckIdx(),
                newGame.getPlayerOneHero(),
                decksPlayer1, Database.FRONTROW1, Database.BACKROW1,
                Database.FRONTROW2, Database.BACKROW2);
        Player player2 = initializePlayer(newGame.getPlayerTwoDeckIdx(),
                newGame.getPlayerTwoHero(),
                decksPlayer2,  Database.FRONTROW2, Database.BACKROW2,
                Database.FRONTROW1, Database.BACKROW1);
        int shuffleSeed = newGame.getShuffleSeed();
        int startingPlayer = newGame.getStartingPlayer();

        Random generator = new Random(shuffleSeed);
        Collections.shuffle(player1.getDeckUsed(), generator);
        generator = new Random(shuffleSeed);
        Collections.shuffle(player2.getDeckUsed(), generator);


        GameInfo game = new GameInfo(player1, player2, shuffleSeed, startingPlayer);

        Helper.addCardInPlayerHand(game.getPlayer1());
        Helper.addCardInPlayerHand(game.getPlayer2());

        game.getPlayer1().incrementMana(1);
        game.getPlayer2().incrementMana(1);

        ArrayList<ArrayList<Minion>> table = game.initializeTable();
        game.setTable(table);
        Database.getDatabase().setGame(game);
        Database.getDatabase().getGame().setPlayerTurn(startingPlayer);
        Database.getDatabase().getGame().setRound(1);
    }

    /**
     * @param player1Deck input for player1 decks
     */
    public static void buildDecksPlayer1(final DecksInput player1Deck) {
        int numberofDecks1 = player1Deck.getNrDecks();
        int nrcardsinDeck1 = player1Deck.getNrCardsInDeck();
        ArrayList<ArrayList<Card>> decksPlayer1 = new ArrayList<>();
        buildPlayerDecks(player1Deck, numberofDecks1, nrcardsinDeck1, decksPlayer1);
        Database.getDatabase().setDecksPlayer1(decksPlayer1);
    }

    /**
     * @param player2Deck input for player2 decks
     */
    public static void buildDecksPlayer2(final DecksInput player2Deck) {
        int numberofDecks2 = player2Deck.getNrDecks();
        int nrcardsinDeck2 = player2Deck.getNrCardsInDeck();
        ArrayList<ArrayList<Card>> decksPlayer2 = new ArrayList<>();
        buildPlayerDecks(player2Deck, numberofDecks2, nrcardsinDeck2, decksPlayer2);
        Database.getDatabase().setDecksPlayer2(decksPlayer2);
    }

    /**
     * @param playerDeck
     * @param numberofDecks
     * @param nrcardsinDeck
     * @param decksPlayer
     */
    private static void buildPlayerDecks(final DecksInput playerDeck, final int numberofDecks,
                                         final int nrcardsinDeck,
                                         final ArrayList<ArrayList<Card>> decksPlayer) {
        for(int i = 0; i < numberofDecks; i++) {
            //ArrayList<ArrayList<Card>> deck = new ArrayList<>();
            ArrayList<Card> cards = new ArrayList<>();
            for (int j = 0; j < nrcardsinDeck; j++) {
                int mana;
                String name, description;
                ArrayList<String> colors;
                Card newCard;

                mana = playerDeck.getDecks().get(i).get(j).getMana();
                name = playerDeck.getDecks().get(i).get(j).getName();
                description = playerDeck.getDecks().get(i).get(j).getDescription();
                colors = playerDeck.getDecks().get(i).get(j).getColors();

                if (!(Database.ENVIRONMENT_CARDS.contains(name))) {
                    int attackDamage, health;
                    attackDamage = playerDeck.getDecks().get(i).get(j).getAttackDamage();
                    health = playerDeck.getDecks().get(i).get(j).getHealth();
                    switch (name) {
                        case "The Ripper":
                            newCard = new TheRipper(name, mana, description, colors,
                                    health, attackDamage);
                            break;
                        case "Miraj":
                            newCard = new Miraj(name, mana, description, colors,
                                    health, attackDamage);
                            break;
                        case "The Cursed One":
                            newCard = new TheCursedOne(name, mana, description, colors,
                                    health, attackDamage);
                            break;
                        case "Disciple":
                            newCard = new Disciple(name, mana, description, colors,
                                    health, attackDamage);
                            break;
                        case "Warden":
                        case "Goliath":
                            newCard = new Minion(name, mana, description, colors, health,
                                    attackDamage, true);
                            break;
                        default:
                            newCard = new Minion(name, mana, description, colors, health,
                                    attackDamage, false);
                            break;
                    }
                } else {
                    switch (name) {
                        case "Firestorm":
                            newCard = new Firestorm(name, mana, description, colors);
                            break;
                        case "Winterfell":
                            newCard = new Winterfell(name, mana, description, colors);
                            break;
                        case "Heart Hound":
                            newCard = new HeartHound(name, mana, description, colors);
                            break;
                        default:
                            newCard = null;
                    }
                }
                cards.add(newCard);
            }
            decksPlayer.add(cards);
        }
    }

    public Builder() {
    }
}
