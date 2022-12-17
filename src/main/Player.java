package main;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

public class Player {
    private Hero hero;
    private ArrayList<Card> deckUsed = new ArrayList<>();
    private ArrayList<Card> cardsInHand = new ArrayList<>();
    private int mana;

    private int frontRow;
    private int backRow;

    private int enemyFrontRow;

    private int enemyBackRow;
    public Player() {

    }

    public Player(final Hero hero, final ArrayList<Card> deckUsed, final int frontRow,
                  final int backRow, final int enemyFrontRow, final int enemyBackRow) {
        switch (hero.getName()) {
            case "Lord Royce":
                this.hero = new LordRoyce(hero);
                break;
            case "Empress Thorina":
                this.hero =  new EmpressThorina(hero);
                break;
            case "King Mudface":
                this.hero = new KingMudface(hero);
                break;
            case "General Kocioraw":
                this.hero = new GeneralKocioraw(hero);
                break;
        }
        this.deckUsed.addAll(deckUsed);
        mana = 0;
        this.frontRow = frontRow;
        this.backRow = backRow;
        this.enemyFrontRow = enemyFrontRow;
        this.enemyBackRow = enemyBackRow;
    }

    public Player(Player player) {
        //this.hero = new Hero(hero.name, hero.mana, hero.description, hero.colors);
        switch (player.hero.getName()) {
            case "Lord Royce":
                this.hero = new LordRoyce(player.hero);
                break;
            case "Empress Thorina":
                this.hero =  new EmpressThorina(player.hero);
                break;
            case "King Mudface":
                this.hero = new KingMudface(player.hero);
                break;
            case "General Kocioraw":
                this.hero = new GeneralKocioraw(player.hero);
                break;
            default:
                this.hero = null;
        }
        this.deckUsed.addAll(player.deckUsed);
        mana = 0;
        this.cardsInHand = new ArrayList<>();
        this.backRow = player.backRow;
        this.frontRow = player.frontRow;
        this.enemyBackRow = player.enemyBackRow;
        this.enemyFrontRow = player.enemyFrontRow;
    }

    /**
     * @return get Hero of the player
     */
    public Hero getHero() {
        return hero;
    }

    /**
     * @param hero the player use
     */
    public void setHero(final Hero hero) {
        this.hero = hero;
    }

    /**
     * @return get deck used by the player
     */
    public ArrayList<Card> getDeckUsed() {
        return deckUsed;
    }

    /**
     * @param deckUsed set the deck used by the player
     */
    public void setDeckUsed(final ArrayList<Card> deckUsed) {
        this.deckUsed.addAll(deckUsed);
    }

    /**
     * @return get the cards in player hand
     */
    public ArrayList<Card> getCardsInHand() {
        return cardsInHand;
    }

    /**
     * @param cardsInHand set the cards in player hand
     */
    public void setCardsInHand(final ArrayList<Card> cardsInHand) {
        this.cardsInHand = cardsInHand;
    }

    /**
     * @return get mana the player is having
     */
    public int getMana() {
        return mana;
    }

    /**
     * @param mana set mana the player is having
     */
    public void setMana(final int mana) {
        this.mana = mana;
    }

    /**
     * @return get what the front row of player is
     */
    public int getFrontRow() {
        return frontRow;
    }

    /**
     * @param frontRow set the front row for a player
     */
    @JsonIgnore
    public void setFrontRow(final int frontRow) {
        this.frontRow = frontRow;
    }
    /**
     * @return get what the back row of player is
     */
    public int getBackRow() {
        return backRow;
    }

    /**
     * @param backRow set the back row for a player
     */
    @JsonIgnore
    public void setBackRow(final int backRow) {
        this.backRow = backRow;
    }

    /**
     * @return get what the front row of enemy player is
     */
    public int getEnemyFrontRow() {
        return enemyFrontRow;
    }


    /**
     * @param enemyFrontRow set enemy front row
     */
    @JsonIgnore
    public void setEnemyFrontRow(final int enemyFrontRow) {
        this.enemyFrontRow = enemyFrontRow;
    }

    /**
     * @return get what the back row of enemy player is
     */
    public int getEnemyBackRow() {
        return enemyBackRow;
    }

    /**
     * @param enemyBackRow set enemy back row
     */
    @JsonIgnore
    public void setEnemyBackRow(final int enemyBackRow) {
        this.enemyBackRow = enemyBackRow;
    }

    public void incrementMana(final int mana) {
        this.mana += mana;
    }

    public void useMana(final int mana) {
        this.mana -= mana;
    }

}


