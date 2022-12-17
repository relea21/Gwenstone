package main;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

public abstract class Card {
    private String name;
    private int mana;
    private String description;
    private ArrayList<String> colors = new ArrayList<>();

    /**
     * @return get the name of a card
     */
    public String getName() {
        return name;
    }

    /**
     * @param name set name for card
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return get the description of a card
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description set description for card
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return get the mana of a card
     */
    public int getMana() {
        return mana;
    }

    /**
     * @param mana set mana for card
     */
    public void setMana(final int mana) {
        this.mana = mana;
    }

    /**
     * @return get the colors of a card
     */
    public ArrayList<String> getColors() {
        return colors;
    }

    /**
     * @param colors set colors for card
     */
    public void setColors(final ArrayList<String> colors) {
        this.colors = colors;
    }

    Card() {
        name = null;
        mana = 0;
        description = null;
    }
    Card(final String name, final int mana, final String description,
                                                final ArrayList<String> colors) {
        this.mana = mana;
        this.name = name;
        this.description = description;
        this.colors.addAll(colors);
    }

    Card(final Card card) {
        this.mana = card.mana;
        this.name = card.name;
        this.description = card.description;
        this.colors.addAll(card.colors);
    }

    abstract boolean isEnvironment();

}

class Minion extends Card {
    private int health, attackDamage;
    private boolean frozen;
    private boolean hasAttacked;

    private boolean isTank;

    public int getHealth() {
        return health;
    }

    public void setHealth(final int health) {
        this.health = health;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(final int attack) {
        this.attackDamage = attack;
    }

    @JsonIgnore
    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(final boolean frozen) {
        this.frozen = frozen;
    }
    @JsonIgnore
    public boolean isHasAttacked() {
        return hasAttacked;
    }

    public void setHasAttacked(final boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    @JsonIgnore
    public boolean isTank() {
        return isTank;
    }

    public void setTank(final boolean tank) {
        isTank = tank;
    }

    Minion() {
        super();
    }
    Minion(final String name, final int mana, final String description,
                                final ArrayList<String> colors,
                                final int health, final int attack, final boolean isTank) {
        super(name, mana, description, colors);
        this.health = health;
        this.attackDamage = attack;
        this.frozen = false;
        this.hasAttacked = false;
        this.isTank = isTank;
    }
    Minion(final Minion minion) {
        super(minion.getName(), minion.getMana(), minion.getDescription(), minion.getColors());
        this.health = minion.getHealth();
        this.attackDamage = minion.getAttackDamage();
        this.frozen = minion.frozen;
        this.hasAttacked = minion.hasAttacked;
        this.isTank = minion.isTank;
    }
    @JsonIgnore
    public boolean isEnvironment() {
        return false;
    }
    public void decrementHealth(final int attack) {
        this.health -= attack;
    }
    @JsonIgnore
    public boolean isSpecialMinion() {
        return false;
    }
}

abstract class SpecialMinion extends Minion  {

    SpecialMinion() {

    }
    SpecialMinion(final String name, final int mana, final String description,
                         final ArrayList<String> colors,
                         final int health, final int attack) {
        super(name, mana, description, colors, health, attack, false);
    }

    SpecialMinion(final SpecialMinion minion) {
        super(minion);
    }

    abstract void ability(Minion minion);

    @Override
    public boolean isSpecialMinion() {
        return true;
    }
}

class TheRipper extends SpecialMinion {
    TheRipper() {
    }
    TheRipper(final String name, final int mana, final String description,
                     final ArrayList<String> colors, final int health, final int attack) {
        super(name, mana, description, colors, health, attack);
    }

    TheRipper(final SpecialMinion minion) {
        super(minion);
    }

    @Override
    public void ability(final Minion minion) {
        int attackDamage = minion.getAttackDamage();
        if (attackDamage < 2) {
            minion.setAttackDamage(0);
        } else {
            minion.setAttackDamage(attackDamage - 2);
        }
    }
}

class Miraj extends SpecialMinion {
    Miraj() {
    }

    Miraj(final String name, final int mana, final String description,
                 final ArrayList<String> colors, final int health, final int attack) {
        super(name, mana, description, colors, health, attack);
    }

    Miraj(final SpecialMinion minion) {
        super(minion);
    }

    @Override
    public void ability(final Minion minion) {
        int health = minion.getHealth();
        minion.setHealth(this.getHealth());
        this.setHealth(health);
    }
}

class TheCursedOne extends SpecialMinion {
    TheCursedOne() {
    }

    TheCursedOne(final String name, final int mana, final String description,
                        final ArrayList<String> colors, final int health, final int attack) {
        super(name, mana, description, colors, health, attack);
    }

    TheCursedOne(final SpecialMinion minion) {
        super(minion);
    }

    @Override
    public void ability(final Minion minion) {
        int health = minion.getHealth();
        minion.setHealth(minion.getAttackDamage());
        minion.setAttackDamage(health);
    }
}

class Disciple extends SpecialMinion {

    Disciple() {
    }

    Disciple(final String name, final int mana, final String description,
                    final ArrayList<String> colors, final int health, final int attack) {
        super(name, mana, description, colors, health, attack);
    }

    Disciple(final SpecialMinion minion) {
        super(minion);
    }

    @Override
    public void ability(final Minion minion) {
        int health = minion.getHealth();
        minion.setHealth(health + 2);
    }
}


abstract class Environment extends Card {
    Environment() {

    }
    Environment(final String name, final int mana, final String description,
                                                final ArrayList<String> colors) {
        super(name, mana, description, colors);
    }
    @JsonIgnore
    public boolean isEnvironment() {
        return true;
    }

    abstract void ability(int row);
}

class Firestorm extends Environment {
    Firestorm() {
    }
    Firestorm(final String name, final int mana, final String description,
                     final ArrayList<String> colors) {
        super(name, mana, description, colors);
    }
    @Override
    public void ability(final int row) {
        ArrayList<ArrayList<Minion>> table = Database.getDatabase().getGame().getTable();
        for (Minion minion : table.get(row)) {
            minion.decrementHealth(1);
        }
        for (int i = 0; i < table.get(row).size(); i++) {
            if (table.get(row).get(i).getHealth() <= 0) {
                table.get(row).remove(i);
                i--;
            }
        }
    }

}

class Winterfell extends Environment {
    Winterfell() {
    }
    Winterfell(final String name, final int mana, final String description,
                      final ArrayList<String> colors) {
        super(name, mana, description, colors);
    }
    public void ability(final int row) {
        ArrayList<ArrayList<Minion>> table = Database.getDatabase().getGame().getTable();
        for (Minion minion : table.get(row)) {
            minion.setFrozen(true);
        }
    }
}

class HeartHound extends Environment {
    HeartHound() {
    }

    HeartHound(final String name, final int mana, final String description,
                      final ArrayList<String> colors) {
        super(name, mana, description, colors);
    }
    @Override
    public void ability(final int row) {
        int rowToPlace = -1;
        if (row == 1) {
            rowToPlace = 2;
        }
        if (row == 0) {
            rowToPlace = 3;
        }
        if (row == 2) {
            rowToPlace = 1;
        }
        if (row == 3) {
            rowToPlace = 0;
        }
        ArrayList<ArrayList<Minion>> table = Database.getDatabase().getGame().getTable();
        Minion minionStolen = table.get(row).get(0);
        for (int i = 1; i < table.get(row).size(); i++) {
            if (table.get(row).get(i).getHealth() > minionStolen.getHealth()) {
                minionStolen = table.get(row).get(i);
            }
        }
        table.get(row).remove(minionStolen);
        table.get(rowToPlace).add(minionStolen);
    }

}

abstract class Hero extends Card {
    private int health;

    private boolean hasAttacked;
    public int getHealth() {
        return health;
    }

    public void setHealth(final int health) {
        this.health = health;
    }

    @JsonIgnore
    public boolean isHasAttacked() {
        return hasAttacked;
    }

    public void setHasAttacked(final boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    abstract void ability(int row);
    Hero(final String name, final int mana, final String description,
                                            final ArrayList<String> colors) {
        super(name, mana, description, colors);
        health = 30;
        hasAttacked = false;
    }
    Hero(final Hero hero) {
        super(hero);
        this.health = hero.health;
        this.hasAttacked = hero.hasAttacked;
    }

    public void decrementHealth(final int attack) {
        this.health -= attack;
    }

    @JsonIgnore
    public boolean isEnvironment() {
        return false;
    }
}

class LordRoyce extends Hero {

    LordRoyce(final String name, final int mana, final String description,
                                                    final ArrayList<String> colors) {
        super(name, mana, description, colors);
    }

    LordRoyce(final Hero hero) {
        super(hero);
    }
    public void ability(final int row) {
        ArrayList<ArrayList<Minion>> table = Database.getDatabase().getGame().getTable();
        Minion minion = table.get(row).get(0);
        for (int i = 1; i < table.get(row).size(); i++) {
            if (minion.getAttackDamage() < table.get(row).get(i).getAttackDamage()) {
                minion = table.get(row).get(i);
            }
        }
        minion.setFrozen(true);
    }
}

class EmpressThorina extends Hero {

    EmpressThorina(final String name, final int mana, final String description,
                                            final ArrayList<String> colors) {
        super(name, mana, description, colors);
    }

    EmpressThorina(final Hero hero) {
        super(hero);
    }
    public void ability(final int row) {
        ArrayList<ArrayList<Minion>> table = Database.getDatabase().getGame().getTable();
        Minion minion = table.get(row).get(0);
        for (int i = 1; i < table.get(row).size(); i++) {
            if (minion.getHealth() < table.get(row).get(i).getHealth()) {
                minion = table.get(row).get(i);
            }
        }
        table.get(row).remove(minion);
    }
}

class KingMudface extends Hero {

    KingMudface(final String name, final int mana, final String description,
                                                final ArrayList<String> colors) {
        super(name, mana, description, colors);
    }

    KingMudface(final Hero hero) {
        super(hero);
    }
    public void ability(final int row) {
        ArrayList<ArrayList<Minion>> table = Database.getDatabase().getGame().getTable();
        for (Minion minion : table.get(row)) {
            int health = minion.getHealth();
            minion.setHealth(health + 1);
        }
    }
}

class GeneralKocioraw extends Hero {

    GeneralKocioraw(final String name, final int mana, final String description,
                                                        final ArrayList<String> colors) {
        super(name, mana, description, colors);
    }

    GeneralKocioraw(final Hero hero) {
        super(hero);
    }
    public void ability(final int row) {
        ArrayList<ArrayList<Minion>> table = Database.getDatabase().getGame().getTable();
        for (Minion minion : table.get(row)) {
            int attack = minion.getAttackDamage();
            minion.setAttackDamage(attack + 1);
        }
    }
}

