package main;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.*;

import java.util.*;

public class Helper {

    private static void endPlayerTurn() {
        GameInfo game = Database.getDatabase().getGame();
        Player player;
        if (game.getPlayerTurn() == 1) {
            player = game.getPlayer1();
            game.setPlayerTurn(2);
        } else {
            player = game.getPlayer2();
            game.setPlayerTurn(1);
        }

        int frontRow = player.getFrontRow();
        int backRow = player.getBackRow();
        for (int i = 0; i < game.getTable().get(frontRow).size(); i++) {
            game.getTable().get(frontRow).get(i).setFrozen(false);
            game.getTable().get(frontRow).get(i).setHasAttacked(false);
        }
        for (int i = 0; i < game.getTable().get(backRow).size(); i++) {
            game.getTable().get(backRow).get(i).setFrozen(false);
            game.getTable().get(backRow).get(i).setHasAttacked(false);
        }

        player.getHero().setHasAttacked(false);

        if (game.getPlayerTurn() == game.getStartingPlayer()) {
            Player player1 = Database.getDatabase().getGame().getPlayer1();
            Player player2 = Database.getDatabase().getGame().getPlayer2();
            if (player1.getDeckUsed().size() != 0 && player2.getDeckUsed().size() != 0) {
                addCardInPlayerHand(player1);
                addCardInPlayerHand(player2);
            }
            game.incrementRound();
            if (game.getRound() <= Database.MAXMANA) {
                game.getPlayer1().incrementMana(game.getRound());
                game.getPlayer2().incrementMana(game.getRound());
            } else {
                game.getPlayer1().incrementMana(Database.MAXMANA);
                game.getPlayer2().incrementMana(Database.MAXMANA);
            }
        }

    }

    public static void addCardInPlayerHand(final Player player) {
        Card card = player.getDeckUsed().get(0);
        player.getCardsInHand().add(card);
        player.getDeckUsed().remove(0);
    }

    private static void placeCard(final ActionsInput action, final ArrayNode output,
                                  final ObjectMapper objectMapper) {
        GameInfo game = Database.getDatabase().getGame();
        Card card;
        ArrayList<Card> cardsInHand;
        Player player;
        int row;
        if (game.getPlayerTurn() == 1) {
            cardsInHand = game.getPlayer1().getCardsInHand();
            card = cardsInHand.get(action.getHandIdx());
            player = game.getPlayer1();
            //vedem pe ce rand trebuie pusa cartea, daca se poate pune
            if (Database.FRONT_ROW_CARDS.contains(card.getName())) {
                row = 2;
            } else {
                row = 3;
            }

        } else {
            cardsInHand = game.getPlayer2().getCardsInHand();
            card = cardsInHand.get(action.getHandIdx());
            player = game.getPlayer2();
            if (Database.FRONT_ROW_CARDS.contains(card.getName())) {
                row = 1;
            } else {
                row = 0;
            }
        }
        if (card.isEnvironment()) {
            ObjectNode commandPrinter = objectMapper.createObjectNode();
            commandPrinter.put("command", action.getCommand());
            commandPrinter.put("handIdx", action.getHandIdx());
            commandPrinter.put("error", "Cannot place environment card on table.");
            output.add(commandPrinter);
            return;
        }
        if (card.getMana() > player.getMana()) {
            ObjectNode commandPrinter = objectMapper.createObjectNode();
            commandPrinter.put("command", action.getCommand());
            commandPrinter.put("handIdx", action.getHandIdx());
            commandPrinter.put("error", "Not enough mana to place card on table.");
            output.add(commandPrinter);
            return;
        }

        if (game.getTable().get(row).size() == 5) {
            ObjectNode commandPrinter = objectMapper.createObjectNode();
            commandPrinter.put("command", action.getCommand());
            commandPrinter.put("handIdx", action.getHandIdx());
            commandPrinter.put("error", "Cannot place card on table since row is full.");
            output.add(commandPrinter);
        } else {
            Minion minion = (Minion) card;
            Minion minionPlaced;
            if (minion.isSpecialMinion()) {
                switch (minion.getName()) {
                    case "Miraj":
                        minionPlaced = new Miraj((SpecialMinion) minion);
                        break;
                    case "The Cursed One":
                        minionPlaced = new TheCursedOne((SpecialMinion) minion);
                        break;
                    case "Disciple":
                        minionPlaced = new Disciple((SpecialMinion) minion);
                        break;
                    case "The Ripper":
                        minionPlaced = new TheRipper((SpecialMinion) minion);
                        break;
                    default:
                        minionPlaced = null;
                }
            } else {
                minionPlaced = new Minion(minion);
            }

            game.getTable().get(row).add(minionPlaced);
            player.useMana(card.getMana());
            cardsInHand.remove(action.getHandIdx());
        }

    }

    private static void useEnvironmentCard(final ActionsInput action, final ArrayNode output,
                                           final ObjectMapper objectMapper) {
        GameInfo game = Database.getDatabase().getGame();
        Card card;
        ArrayList<Card> cardsInHand;
        Player player;
        if (game.getPlayerTurn() == 1) {
            player = game.getPlayer1();
        } else {
            player = game.getPlayer2();
        }

        cardsInHand = player.getCardsInHand();
        card = cardsInHand.get(action.getHandIdx());

        if (!(card.isEnvironment())) {
            ObjectNode commandPrinter = objectMapper.createObjectNode();
            commandPrinter.put("command", action.getCommand());
            commandPrinter.put("affectedRow", action.getAffectedRow());
            commandPrinter.put("handIdx", action.getHandIdx());
            commandPrinter.put("error", "Chosen card is not of type environment.");
            output.add(commandPrinter);
            return;
        }
        if (card.getMana() > player.getMana()) {
            ObjectNode commandPrinter = objectMapper.createObjectNode();
            commandPrinter.put("command", action.getCommand());
            commandPrinter.put("affectedRow", action.getAffectedRow());
            commandPrinter.put("handIdx", action.getHandIdx());
            commandPrinter.put("error", "Not enough mana to use environment card.");
            output.add(commandPrinter);
            return;
        }
        if (player.getBackRow() == action.getAffectedRow()
                || player.getFrontRow() == action.getAffectedRow()) {
            ObjectNode commandPrinter = objectMapper.createObjectNode();
            commandPrinter.put("command", action.getCommand());
            commandPrinter.put("affectedRow", action.getAffectedRow());
            commandPrinter.put("handIdx", action.getHandIdx());
            commandPrinter.put("error", "Chosen row does not belong to the enemy.");
            output.add(commandPrinter);
            return;
        }

        if (card.getName().equals("Heart Hound")) {
            if (action.getAffectedRow() == player.getEnemyBackRow()) {
                if (game.getTable().get(player.getBackRow()).size() == 5) {
                    ObjectNode commandPrinter = objectMapper.createObjectNode();
                    commandPrinter.put("command", action.getCommand());
                    commandPrinter.put("affectedRow", action.getAffectedRow());
                    commandPrinter.put("handIdx", action.getHandIdx());
                    commandPrinter.put("error", "Cannot steal enemy card since the player's row is full.");
                    output.add(commandPrinter);
                    return;
                }
            } else {
                if (game.getTable().get(player.getFrontRow()).size() == 5) {
                    ObjectNode commandPrinter = objectMapper.createObjectNode();
                    commandPrinter.put("command", action.getCommand());
                    commandPrinter.put("affectedRow", action.getAffectedRow());
                    commandPrinter.put("handIdx", action.getHandIdx());
                    commandPrinter.put("error",
                                    "Cannot steal enemy card since the player's row is full.");
                    output.add(commandPrinter);
                    return;
                }
            }
        }

        Environment environmentCard = (Environment) card;
        environmentCard.ability(action.getAffectedRow());
        player.useMana(card.getMana());
        cardsInHand.remove(action.getHandIdx());
    }

    private static void cardUsesAttack(final ActionsInput action, final ArrayNode output,
                                       final ObjectMapper objectMapper) {
        GameInfo game = Database.getDatabase().getGame();
        Player player;
        ArrayList<ArrayList<Minion>> table = game.getTable();

        if (game.getPlayerTurn() == 1) {
            player = game.getPlayer1();
        } else {
            player = game.getPlayer2();
        }
        if (player.getEnemyBackRow() != action.getCardAttacked().getX()
                && player.getEnemyFrontRow() != action.getCardAttacked().getX()) {
            ObjectNode commandPrinter = objectMapper.createObjectNode();
            commandPrinter.put("command", action.getCommand());
            Coordinates coordinates = new Coordinates();
            coordinates.setX(action.getCardAttacker().getX());
            coordinates.setY(action.getCardAttacker().getY());
            commandPrinter.putPOJO("cardAttacker", coordinates);
            coordinates = new Coordinates();
            coordinates.setX(action.getCardAttacked().getX());
            coordinates.setY(action.getCardAttacked().getY());
            commandPrinter.putPOJO("cardAttacked", coordinates);
            commandPrinter.put("error", "Attacked card does not belong to the enemy.");
            output.add(commandPrinter);
            return;
        }

        Minion cardAttacker = table.get(action.getCardAttacker().getX())
                                            .get(action.getCardAttacker().getY());
        Minion cardAttacked = table.get(action.getCardAttacked().getX())
                                            .get(action.getCardAttacked().getY());

        if (cardAttacker.isHasAttacked()) {
            ObjectNode commandPrinter = objectMapper.createObjectNode();
            commandPrinter.put("command", action.getCommand());
            Coordinates coordinates = new Coordinates();
            coordinates.setX(action.getCardAttacker().getX());
            coordinates.setY(action.getCardAttacker().getY());
            commandPrinter.putPOJO("cardAttacker", coordinates);
            coordinates = new Coordinates();
            coordinates.setX(action.getCardAttacked().getX());
            coordinates.setY(action.getCardAttacked().getY());
            commandPrinter.putPOJO("cardAttacked", coordinates);
            commandPrinter.put("error", "Attacker card has already attacked this turn.");
            output.add(commandPrinter);
            return;
        }

        if (cardAttacker.isFrozen()) {
            ObjectNode commandPrinter = objectMapper.createObjectNode();
            commandPrinter.put("command", action.getCommand());
            Coordinates coordinates = new Coordinates();
            coordinates.setX(action.getCardAttacker().getX());
            coordinates.setY(action.getCardAttacker().getY());
            commandPrinter.putPOJO("cardAttacker", coordinates);
            coordinates = new Coordinates();
            coordinates.setX(action.getCardAttacked().getX());
            coordinates.setY(action.getCardAttacked().getY());
            commandPrinter.putPOJO("cardAttacked", coordinates);
            commandPrinter.put("error", "Attacker card is frozen.");
            output.add(commandPrinter);
            return;
        }

        boolean enemyTankMinion = false;
        for (Minion minion : table.get(player.getEnemyFrontRow())) {
            if (minion.isTank()) {
                enemyTankMinion = true;
                break;
            }
        }
        if (enemyTankMinion) {
            if (!(cardAttacked.isTank())) {
                ObjectNode commandPrinter = objectMapper.createObjectNode();
                commandPrinter.put("command", action.getCommand());
                Coordinates coordinates = new Coordinates();
                coordinates.setX(action.getCardAttacker().getX());
                coordinates.setY(action.getCardAttacker().getY());
                commandPrinter.putPOJO("cardAttacker", coordinates);
                coordinates = new Coordinates();
                coordinates.setX(action.getCardAttacked().getX());
                coordinates.setY(action.getCardAttacked().getY());
                commandPrinter.putPOJO("cardAttacked", coordinates);
                commandPrinter.put("error", "Attacked card is not of type 'Tank'.");
                output.add(commandPrinter);
                return;
            }
        }
        cardAttacked.decrementHealth(cardAttacker.getAttackDamage());
        cardAttacker.setHasAttacked(true);
        if (cardAttacked.getHealth() <= 0) {
            table.get(action.getCardAttacked().getX()).remove(cardAttacked);
        }
    }

    private static void cardUsesAbility(final ActionsInput action, final ArrayNode output,
                                        final ObjectMapper objectMapper) {
        GameInfo game = Database.getDatabase().getGame();
        Player player;
        ArrayList<ArrayList<Minion>> table = game.getTable();

        if (game.getPlayerTurn() == 1) {
            player = game.getPlayer1();
        } else {
            player = game.getPlayer2();
        }
        SpecialMinion cardAttacker = (SpecialMinion) table.get(action.getCardAttacker().getX())
                .get(action.getCardAttacker().getY());
        Minion cardAttacked = table.get(action.getCardAttacked().getX())
                .get(action.getCardAttacked().getY());

        if (cardAttacker.isFrozen()) {
            ObjectNode commandPrinter = objectMapper.createObjectNode();
            commandPrinter.put("command", action.getCommand());
            Coordinates coordinates = new Coordinates();
            coordinates.setX(action.getCardAttacker().getX());
            coordinates.setY(action.getCardAttacker().getY());
            commandPrinter.putPOJO("cardAttacker", coordinates);
            coordinates = new Coordinates();
            coordinates.setX(action.getCardAttacked().getX());
            coordinates.setY(action.getCardAttacked().getY());
            commandPrinter.putPOJO("cardAttacked", coordinates);
            commandPrinter.put("error", "Attacker card is frozen.");
            output.add(commandPrinter);
            return;
        }

        if (cardAttacker.isHasAttacked()) {
            ObjectNode commandPrinter = objectMapper.createObjectNode();
            commandPrinter.put("command", action.getCommand());
            Coordinates coordinates = new Coordinates();
            coordinates.setX(action.getCardAttacker().getX());
            coordinates.setY(action.getCardAttacker().getY());
            commandPrinter.putPOJO("cardAttacker", coordinates);
            coordinates = new Coordinates();
            coordinates.setX(action.getCardAttacked().getX());
            coordinates.setY(action.getCardAttacked().getY());
            commandPrinter.putPOJO("cardAttacked", coordinates);
            commandPrinter.put("error", "Attacker card has already attacked this turn.");
            output.add(commandPrinter);
            return;
        }

        if (cardAttacker.getName().equals("Disciple")) {
            if (action.getCardAttacked().getX() != player.getBackRow()
                        && action.getCardAttacked().getX() != player.getFrontRow()) {
                ObjectNode commandPrinter = objectMapper.createObjectNode();
                commandPrinter.put("command", action.getCommand());
                Coordinates coordinates = new Coordinates();
                coordinates.setX(action.getCardAttacker().getX());
                coordinates.setY(action.getCardAttacker().getY());
                commandPrinter.putPOJO("cardAttacker", coordinates);
                coordinates = new Coordinates();
                coordinates.setX(action.getCardAttacked().getX());
                coordinates.setY(action.getCardAttacked().getY());
                commandPrinter.putPOJO("cardAttacked", coordinates);
                commandPrinter.put("error", "Attacked card does not belong to the current player.");
                output.add(commandPrinter);
                return;
            }
        } else {
            if (action.getCardAttacked().getX() != player.getEnemyBackRow()
                    && action.getCardAttacked().getX() != player.getEnemyFrontRow()) {
                ObjectNode commandPrinter = objectMapper.createObjectNode();
                commandPrinter.put("command", action.getCommand());
                Coordinates coordinates = new Coordinates();
                coordinates.setX(action.getCardAttacker().getX());
                coordinates.setY(action.getCardAttacker().getY());
                commandPrinter.putPOJO("cardAttacker", coordinates);
                coordinates = new Coordinates();
                coordinates.setX(action.getCardAttacked().getX());
                coordinates.setY(action.getCardAttacked().getY());
                commandPrinter.putPOJO("cardAttacked", coordinates);
                commandPrinter.put("error", "Attacked card does not belong to the enemy.");
                output.add(commandPrinter);
                return;
            }
        }

        boolean enemyTankMinion = false;
        for (Minion minion : table.get(player.getEnemyFrontRow())) {
            if (minion.isTank()) {
                enemyTankMinion = true;
                break;
            }
        }
        if (enemyTankMinion && !(cardAttacker.getName().equals("Disciple"))) {
            if (!(cardAttacked.isTank())) {
                ObjectNode commandPrinter = objectMapper.createObjectNode();
                commandPrinter.put("command", action.getCommand());
                Coordinates coordinates = new Coordinates();
                coordinates.setX(action.getCardAttacker().getX());
                coordinates.setY(action.getCardAttacker().getY());
                commandPrinter.putPOJO("cardAttacker", coordinates);
                coordinates = new Coordinates();
                coordinates.setX(action.getCardAttacked().getX());
                coordinates.setY(action.getCardAttacked().getY());
                commandPrinter.putPOJO("cardAttacked", coordinates);
                commandPrinter.put("error", "Attacked card is not of type 'Tank'.");
                output.add(commandPrinter);
                return;
            }
        }
        cardAttacker.ability(cardAttacked);
        cardAttacker.setHasAttacked(true);
        if (cardAttacked.getHealth() <= 0) {
            table.get(action.getCardAttacked().getX()).remove(cardAttacked);
        }
    }

    private static void useAttackHero(final ActionsInput action, final ArrayNode output,
                                    final ObjectMapper objectMapper, final Statistics statistics) {
        GameInfo game = Database.getDatabase().getGame();
        Player player;
        Player enemyPlayer;
        ArrayList<ArrayList<Minion>> table = game.getTable();

        if (game.getPlayerTurn() == 1) {
            player = game.getPlayer1();
            enemyPlayer =  game.getPlayer2();
        } else {
            player = game.getPlayer2();
            enemyPlayer = game.getPlayer1();
        }

        Minion cardAttacker = table.get(action.getCardAttacker().getX())
                .get(action.getCardAttacker().getY());

        if (cardAttacker.isFrozen()) {
            ObjectNode commandPrinter = objectMapper.createObjectNode();
            commandPrinter.put("command", action.getCommand());
            Coordinates coordinates = new Coordinates();
            coordinates.setX(action.getCardAttacker().getX());
            coordinates.setY(action.getCardAttacker().getY());
            commandPrinter.putPOJO("cardAttacker", coordinates);
            commandPrinter.put("error", "Attacker card is frozen.");
            output.add(commandPrinter);
            return;
        }

        if (cardAttacker.isHasAttacked()) {
            ObjectNode commandPrinter = objectMapper.createObjectNode();
            commandPrinter.put("command", action.getCommand());
            Coordinates coordinates = new Coordinates();
            coordinates.setX(action.getCardAttacker().getX());
            coordinates.setY(action.getCardAttacker().getY());
            commandPrinter.putPOJO("cardAttacker", coordinates);
            commandPrinter.put("error", "Attacker card has already attacked this turn.");
            output.add(commandPrinter);
            return;
        }

        boolean enemyTankMinion = false;
        for (Minion minion : table.get(player.getEnemyFrontRow())) {
            if (minion.isTank()) {
                enemyTankMinion = true;
                break;
            }
        }

        if (enemyTankMinion) {
            ObjectNode commandPrinter = objectMapper.createObjectNode();
            commandPrinter.put("command", action.getCommand());
            Coordinates coordinates = new Coordinates();
            coordinates.setX(action.getCardAttacker().getX());
            coordinates.setY(action.getCardAttacker().getY());
            commandPrinter.putPOJO("cardAttacker", coordinates);
            commandPrinter.put("error", "Attacked card is not of type 'Tank'.");
            output.add(commandPrinter);
            return;
        }

        Hero heroAttacked = enemyPlayer.getHero();
        heroAttacked.decrementHealth(cardAttacker.getAttackDamage());
        cardAttacker.setHasAttacked(true);
        if (heroAttacked.getHealth() <= 0) {
            if (game.getPlayerTurn() == 1) {
                statistics.incrementGamesWinPlayer1();
                ObjectNode finalGamePrinter = objectMapper.createObjectNode();
                finalGamePrinter.put("gameEnded", "Player one killed the enemy hero.");
                output.add(finalGamePrinter);
            } else {
                statistics.incrementGamesWinPlayer2();
                ObjectNode finalGamePrinter = objectMapper.createObjectNode();
                finalGamePrinter.put("gameEnded", "Player two killed the enemy hero.");
                output.add(finalGamePrinter);
            }
        }
    }

    private static void useHeroAbility(final ActionsInput action, final ArrayNode output,
                                       final ObjectMapper objectMapper) {
        GameInfo game = Database.getDatabase().getGame();
        Player player;
        ArrayList<ArrayList<Minion>> table = game.getTable();

        if (game.getPlayerTurn() == 1) {
            player = game.getPlayer1();
        } else {
            player = game.getPlayer2();
        }

        Hero hero = player.getHero();

        if (hero.getMana() > player.getMana()) {
            ObjectNode commandPrinter = objectMapper.createObjectNode();
            commandPrinter.put("command", action.getCommand());
            commandPrinter.put("affectedRow", action.getAffectedRow());
            commandPrinter.put("error", "Not enough mana to use hero's ability.");
            output.add(commandPrinter);
            return;
        }

        if (hero.isHasAttacked()) {
            ObjectNode commandPrinter = objectMapper.createObjectNode();
            commandPrinter.put("command", action.getCommand());
            commandPrinter.put("affectedRow", action.getAffectedRow());
            commandPrinter.put("error", "Hero has already attacked this turn.");
            output.add(commandPrinter);
            return;
        }

        if (hero.getName().equals("Lord Royce") || hero.getName().equals("Empress Thorina")) {
            if (action.getAffectedRow() != player.getEnemyBackRow()
                    && action.getAffectedRow() != player.getEnemyFrontRow()) {
                ObjectNode commandPrinter = objectMapper.createObjectNode();
                commandPrinter.put("command", action.getCommand());
                commandPrinter.put("affectedRow", action.getAffectedRow());
                commandPrinter.put("error", "Selected row does not belong to the enemy.");
                output.add(commandPrinter);
                return;
            }
        }

        if (hero.getName().equals("General Kocioraw") || hero.getName().equals("King Mudface")) {
            if (action.getAffectedRow() != player.getBackRow()
                    && action.getAffectedRow() != player.getFrontRow()) {
                ObjectNode commandPrinter = objectMapper.createObjectNode();
                commandPrinter.put("command", action.getCommand());
                commandPrinter.put("affectedRow", action.getAffectedRow());
                commandPrinter.put("error", "Selected row does not belong to the current player.");
                output.add(commandPrinter);
                return;
            }
        }

        hero.ability(action.getAffectedRow());
        hero.setHasAttacked(true);
        player.useMana(hero.getMana());
    }

    public static void executeActions(final ArrayList<ActionsInput> actions,
                                      final ArrayNode output, final ObjectMapper objectMapper,
                                      final Statistics statistics) {
        for (ActionsInput action : actions) {
            switch (action.getCommand()) {
                case "getPlayerDeck":
                    Debugger.getPlayerDeck(action, output, objectMapper);
                    break;
                case "getPlayerHero":
                    Debugger.getPlayerHero(action, output, objectMapper);
                    break;
                case "getPlayerTurn":
                    Debugger.getPlayerTurn(action, output, objectMapper);
                    break;
                case "endPlayerTurn":
                    Helper.endPlayerTurn();
                    break;
                case "placeCard":
                    Helper.placeCard(action, output, objectMapper);
                    break;
                case "getCardsInHand":
                    Debugger.getCardsInHand(action, output, objectMapper);
                    break;
                case "getPlayerMana":
                    Debugger.getPlayerMana(action, output, objectMapper);
                    break;
                case "getCardsOnTable":
                    Debugger.getCardsOnTable(action, output, objectMapper);
                    break;
                case "useEnvironmentCard":
                    Helper.useEnvironmentCard(action, output, objectMapper);
                    break;
                case "getEnvironmentCardsInHand":
                    Debugger.getEnvironmentCardsInHand(action, output, objectMapper);
                    break;
                case "getCardAtPosition":
                    Debugger.getCardAtPosition(action, output, objectMapper);
                    break;
                case "getFrozenCardsOnTable":
                    Debugger.getFrozenCardsOnTable(action, output, objectMapper);
                    break;
                case "cardUsesAttack":
                    Helper.cardUsesAttack(action, output, objectMapper);
                    break;
                case "cardUsesAbility":
                    Helper.cardUsesAbility(action, output, objectMapper);
                    break;
                case "useAttackHero":
                    Helper.useAttackHero(action, output, objectMapper, statistics);
                    break;
                case "useHeroAbility":
                    Helper.useHeroAbility(action, output, objectMapper);
                    break;
                case "getPlayerOneWins":
                    Debugger.getPlayerOneWins(action, output, objectMapper, statistics);
                    break;
                case "getPlayerTwoWins":
                    Debugger.getPlayerTwoWins(action, output, objectMapper, statistics);
                    break;
                case "getTotalGamesPlayed":
                    Debugger.getTotalGamesPlayed(action, output, objectMapper, statistics);
                    break;
                default:
                    System.out.println("Invalid command");
            }
        }
    }
}
