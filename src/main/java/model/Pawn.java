package model;

/**
 * Represents a Pawn game piece that extends the functionality of the {@link Token} class.
 * <p>
 * A Pawn can have a specific color (black or pink) and symbol (X or O).
 */
public class Pawn extends Token{
    private final Color color;

    /**
     * Creates a new Pawn with the specified symbol and color.
     *
     * @param symbol the symbol associated with the pawn (e.g. X or O).
     * @param color the color associated with the pawn (e.g. BLACK, PINK)
     */
    Pawn(Symbol symbol, Color color) {
        super(symbol);
        this.color = color;
    }

    Color getColor() {
        return color;
    }
}
