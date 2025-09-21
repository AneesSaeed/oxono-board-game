package model;

abstract class Token {
    private final Symbol symbol;

    Token(Symbol symbol) {
        this.symbol = symbol;
    }

    Symbol getSymbol() {
        return symbol;
    }
}
