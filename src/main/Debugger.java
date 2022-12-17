package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;

import java.util.ArrayList;

public class Debugger {
    /**
     * @param action
     * @param output for the program
     * @param objectMapper
     */
    public static void getPlayerDeck(final ActionsInput action, final ArrayNode output,
                                      final ObjectMapper objectMapper) {
        ObjectNode commandPrinter = objectMapper.createObjectNode();
        commandPrinter.put("command", action.getCommand());
        commandPrinter.put("playerIdx", action.getPlayerIdx());
        if (action.getPlayerIdx() == 1) {
            ArrayList<Card> deckUsed =  new ArrayList<>(Database.getDatabase().getGame().
                    getPlayer1().getDeckUsed());
            commandPrinter.putPOJO("output", deckUsed);
        }
        if (action.getPlayerIdx() == 2) {
            ArrayList<Card> deckUsed =  new ArrayList<>(Database.getDatabase().getGame().
                    getPlayer2().getDeckUsed());
            commandPrinter.putPOJO("output", deckUsed);
        }
        output.add(commandPrinter);
    }

    /**
     * @param action
     * @param output for the program
     * @param objectMapper
     */
    public static void getPlayerHero(final ActionsInput action, final ArrayNode output,
                                      final ObjectMapper objectMapper) {
        ObjectNode commandPrinter = objectMapper.createObjectNode();
        commandPrinter.put("command", action.getCommand());
        commandPrinter.put("playerIdx", action.getPlayerIdx());
        if (action.getPlayerIdx() == 1) {
            Hero heroCopy;
            Hero hero = Database.getDatabase().getGame().getPlayer1().getHero();
            switch (hero.getName()) {
                case "Lord Royce":
                    heroCopy = new LordRoyce(hero);
                    break;
                case "Empress Thorina":
                    heroCopy =  new EmpressThorina(hero);
                    break;
                case "King Mudface":
                    heroCopy = new KingMudface(hero);
                    break;
                case "General Kocioraw":
                    heroCopy = new GeneralKocioraw(hero);
                    break;
                default:
                    heroCopy = null;
            }

            commandPrinter.putPOJO("output", heroCopy);
        }
        if (action.getPlayerIdx() == 2) {
            Hero heroCopy;
            Hero hero = Database.getDatabase().getGame().getPlayer2().getHero();
            switch (hero.getName()) {
                case "Lord Royce":
                    heroCopy = new LordRoyce(hero);
                    break;
                case "Empress Thorina":
                    heroCopy =  new EmpressThorina(hero);
                    break;
                case "King Mudface":
                    heroCopy = new KingMudface(hero);
                    break;
                case "General Kocioraw":
                    heroCopy = new GeneralKocioraw(hero);
                    break;
                default:
                    heroCopy = null;
            }

            commandPrinter.putPOJO("output", heroCopy);
        }
        output.add(commandPrinter);
    }

    /**
     * @param action
     * @param output for the program
     * @param objectMapper
     */
    public static void getPlayerTurn(final ActionsInput action, final ArrayNode output,
                                      final ObjectMapper objectMapper) {
        ObjectNode commandPrinter = objectMapper.createObjectNode();
        commandPrinter.put("command", action.getCommand());
        commandPrinter.put("output", Database.getDatabase().getGame().getPlayerTurn());
        output.add(commandPrinter);
    }

    /**
     * @param action
     * @param output for the program
     * @param objectMapper
     */
    public static void getCardsInHand(final ActionsInput action, final ArrayNode output,
                                       final ObjectMapper objectMapper) {
        Player player;
        if (action.getPlayerIdx() == 1) {
            player = Database.getDatabase().getGame().getPlayer1();
        } else {
            player = Database.getDatabase().getGame().getPlayer2();
        }
        ObjectNode commandPrinter = objectMapper.createObjectNode();
        commandPrinter.put("command", action.getCommand());
        commandPrinter.put("playerIdx", action.getPlayerIdx());
        ArrayList<Card> cardsInHand = new ArrayList<>(player.getCardsInHand());
        commandPrinter.putPOJO("output", cardsInHand);
        output.add(commandPrinter);
    }

    /**
     * @param action
     * @param output for the program
     * @param objectMapper
     */
    public static void getPlayerMana(final ActionsInput action, final ArrayNode output,
                                      final ObjectMapper objectMapper) {
        Player player;
        if (action.getPlayerIdx() == 1) {
            player = Database.getDatabase().getGame().getPlayer1();
        } else {
            player = Database.getDatabase().getGame().getPlayer2();
        }
        ObjectNode commandPrinter = objectMapper.createObjectNode();
        commandPrinter.put("command", action.getCommand());
        commandPrinter.put("playerIdx", action.getPlayerIdx());
        commandPrinter.put("output", player.getMana());
        output.add(commandPrinter);
    }

    /**
     * @param action
     * @param output for the program
     * @param objectMapper
     */
    public static void getCardsOnTable(final ActionsInput action, final ArrayNode output,
                                        final ObjectMapper objectMapper) {
        ObjectNode commandPrinter = objectMapper.createObjectNode();
        commandPrinter.put("command", action.getCommand());
        ArrayList<ArrayList<Minion>> table = new ArrayList<>(Database.getDatabase().getGame().getTable());
        commandPrinter.putPOJO("output", table);
        output.add(commandPrinter);
    }

    /**
     * @param action
     * @param output for the program
     * @param objectMapper
     */
    public static void getEnvironmentCardsInHand(final ActionsInput action, final ArrayNode output,
                                                  final ObjectMapper objectMapper) {
        Player player;
        if (action.getPlayerIdx() == 1) {
            player = Database.getDatabase().getGame().getPlayer1();
        } else {
            player = Database.getDatabase().getGame().getPlayer2();
        }
        ArrayList<Card> environmentCards = new ArrayList<>();
        for (Card card : player.getCardsInHand())
            if (card.isEnvironment()) {
                environmentCards.add(card);
            }
        ObjectNode commandPrinter = objectMapper.createObjectNode();
        commandPrinter.put("command", action.getCommand());
        commandPrinter.put("playerIdx", action.getPlayerIdx());
        commandPrinter.putPOJO("output", environmentCards);
        output.add(commandPrinter);
    }

    /**
     * @param action
     * @param output for the program
     * @param objectMapper
     */
    public static void getCardAtPosition(final ActionsInput action, final ArrayNode output,
                                          final ObjectMapper objectMapper){
        ArrayList<ArrayList<Minion>> table = Database.getDatabase().getGame().getTable();

        ObjectNode commandPrinter = objectMapper.createObjectNode();
        commandPrinter.put("command", action.getCommand());
        if (action.getY() >= table.get(action.getX()).size()) {
            commandPrinter.put("output", "No card available at that position.");
            commandPrinter.put("x", action.getX());
            commandPrinter.put("y", action.getY());
        } else {
            Minion minion = new Minion(table.get(action.getX()).get(action.getY()));
            commandPrinter.putPOJO("output", minion);
            commandPrinter.put("x", action.getX());
            commandPrinter.put("y", action.getY());
        }
        output.add(commandPrinter);
    }

    /**
     * @param action
     * @param output for the program
     * @param objectMapper
     */
    public static void getFrozenCardsOnTable(final ActionsInput action, final ArrayNode output,
                                              final ObjectMapper objectMapper) {
        ArrayList<ArrayList<Minion>> table = Database.getDatabase().getGame().getTable();
        ArrayList<Minion> frozenCards = new ArrayList<>();
        for (int i = 0; i < table.size(); i++) {
            for (int j = 0; j < table.get(i).size(); j++) {
                if (table.get(i).get(j).isFrozen()) {
                    frozenCards.add(new Minion(table.get(i).get(j)));
                }
            }
        }
        ObjectNode commandPrinter = objectMapper.createObjectNode();
        commandPrinter.put("command", action.getCommand());
        commandPrinter.putPOJO("output", frozenCards);
        output.add(commandPrinter);
    }

    /**
     * @param action
     * @param output for the program
     * @param objectMapper
     * @param statistics
     */
    public static void getPlayerOneWins(final ActionsInput action, final ArrayNode output,
                                         final ObjectMapper objectMapper,
                                         final Statistics statistics) {
        ObjectNode commandPrinter = objectMapper.createObjectNode();
        commandPrinter.put("command", action.getCommand());
        commandPrinter.put("output", statistics.getGamesWinPlayer1());
        output.add(commandPrinter);
    }

    /**
     * @param action
     * @param output for the program
     * @param objectMapper
     * @param statistics
     */
    public static void getPlayerTwoWins(final ActionsInput action, final ArrayNode output,
                                         final ObjectMapper objectMapper,
                                         final Statistics statistics) {
        ObjectNode commandPrinter = objectMapper.createObjectNode();
        commandPrinter.put("command", action.getCommand());
        commandPrinter.put("output", statistics.getGamesWinPlayer2());
        output.add(commandPrinter);
    }

    /**
     * @param action
     * @param output for the program
     * @param objectMapper
     * @param statistics
     */
    public static void getTotalGamesPlayed(final ActionsInput action, final ArrayNode output,
                                            final ObjectMapper objectMapper,
                                            final Statistics statistics) {
        ObjectNode commandPrinter = objectMapper.createObjectNode();
        commandPrinter.put("command", action.getCommand());
        commandPrinter.put("output", statistics.getGames());
        output.add(commandPrinter);
    }
}
