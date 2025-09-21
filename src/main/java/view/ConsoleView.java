package view;

import model.*;

import java.util.Scanner;

public class ConsoleView {

    private final Scanner scanner = new Scanner(System.in);
    private static final int CELL_WIDTH = 7; // Standard width for cells

    // -------------------------------
    // Display Methods
    // -------------------------------

    /**
     * Displays the game board
     *
     * @param game The current game instance.
     */
    public void displayBoard(Game game) {
        int rows = game.getRows();
        int cols = game.getCols();
        String playerColor = getPlayerColor(game.getCurrPlayerColor());

        int tileCounter = 1;
        for (int row = 0; row < rows; row++) {
            printRowBorder(cols, playerColor);
            for (int col = 0; col < cols; col++) {
                Position position = new Position(row, col);

                // Determine tile color
                String tileColor = game.getTileColor(position);

                // Determine tile content
                String content = game.isTileEmpty(position)
                        ? String.valueOf(tileCounter) // Tile number for empty tiles
                        : game.getTileSymbol(position);

                System.out.print(playerColor + "|" + tileColor + padContent(content) + "\033[0m");
                tileCounter++;
            }
            System.out.println(playerColor + "|");
        }
        printRowBorder(cols, playerColor);
        showMessage("Tiles left: "+game.getEmptyTileCount());
    }

    /**
     * Displays the current player's turn and the remaining pawns.
     *
     * @param game The current game instance.
     */
    public void displayPlayerStatus(Game game) {
        showMessage(game.getCurrPlayerColor() + "'s turn.");
        showMessage("Pawns left ——> 'X': " + game.remainingX() + ", 'O': " + game.remainingO());
    }

    /**
     * Displays the game title with formatting.
     */
    public void showTitle() {
        showMessage("\n+_+_+_+_+ Welcome to Oxono +_+_+_+_+\n");
    }

    // -------------------------------
    // User Interaction Methods
    // -------------------------------
    /**
     * Prompts the user for input and returns their response.
     *
     * @return The trimmed user input.
     */
    public String getUserInput() {
        System.out.print("Enter your command: ");
        return scanner.nextLine().trim();
    }

    // -------------------------------
    // Messaging Methods
    // -------------------------------
    /**
     * Displays a general message in green text.
     *
     * @param message The message to display.
     */
    public void showMessage(String message) {
        System.out.println("\033[32m" + message + "\033[0m");
    }

    /**
     * Displays an error message in red text.
     *
     * @param message The error message to display.
     */
    public void showError(String message) {
        System.out.println("\033[91mError: " + message + "\033[0m");
    }

    // -------------------------------
    // Helper Methods
    // -------------------------------
    /**
     * Gets the ANSI color code for the current player's color.
     */
    private String getPlayerColor(Color color) {
        return (color == Color.PINK) ? "\033[38;2;240;128;160m" : "\033[38;2;192;192;192m";
    }

    /**
     * Pads the content of a tile to center-align it within the cell.
     */
    private String padContent(String content) {
        int padding = (CELL_WIDTH - content.length()) / 2;
        return " ".repeat(padding) + content + " ".repeat(CELL_WIDTH - padding - content.length());
    }

    /**
     * Prints the border of a row.
     */
    private void printRowBorder(int cols, String playerColor) {
        System.out.println(playerColor + "+-------".repeat(cols) + "+" + "\033[0m");
    }
}
