package model;

/**
 * Represents a Totem
 * <p>
 * Totems are unique game pieces that extend the functionality of the {@link Token} class.
 */
public class Totem extends Token{
    /**
     * Creates a new Totem with the specified symbol.
     *
     * @param symbol the symbol associated with the Totem (e.g., X or O).
     */
    public Totem(Symbol symbol) {
        super(symbol);
    }
}
