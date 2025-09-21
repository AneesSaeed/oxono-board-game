package model;

class Tile {
    private Token token;

    public boolean isEmpty() {
        return token == null;
    }

    void setToken(Token token) {
        this.token = token;
    }

    TileState getState(){
        if (token == null){
            return TileState.EMPTY;
        } else if (token instanceof Totem) {
            return token.getSymbol() == Symbol.X ? TileState.TOTEM_X : TileState.TOTEM_O;
        } else if (token instanceof Pawn pawn) {
            return switch (pawn.getColor()){
                case Color.PINK -> token.getSymbol() == Symbol.X ? TileState.PINK_X : TileState.PINK_O;
                case Color.BLACK -> token.getSymbol() == Symbol.X ? TileState.BLACK_X : TileState.BLACK_O;
            };
        }
        throw new IllegalStateException("unexpected token type in tile");
    }

    //TODO: should use the getState() method
    String getColor() {
        if (token == null) {
            return "\033[48;2;40;40;40m"; // dark gray background
        }
        if (token instanceof Totem) {
            return "\033[1;104;30m"; // Light blue background with bold black text
        }
        if (token instanceof Pawn pawn) {
            return switch (pawn.getColor()){
                case Color.PINK -> "\033[1;105;30m"; //Pink background with bold black text
                case Color.BLACK -> "\033[48;2;0;0;0m\033[38;2;255;255;255m"; // black background with bold white text
            };
        }
        return "\033[0m"; // reset
    }

    void removeToken() {
        this.token = null;
    }

    Token getToken() {
        return token;
    }
}
