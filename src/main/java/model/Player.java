package model;


class Player {
    private final Color color;
    private int remainingX;
    private int remainingO;
    private Symbol chosenTotem;

    public Player(Color color) {
        this.color = color;
        this.remainingX = 8;
        this.remainingO = 8;
    }

    void decreasePawn(Pawn pawn) {
        if (pawn.getSymbol() == Symbol.O) {
            if (remainingO > 0) {
                remainingO--;
            }
        } else if (pawn.getSymbol() == Symbol.X) {
            if (remainingX > 0) {
                remainingX--;
            }
        }
    }

    void increasePawn(Pawn pawn) {
        if (pawn.getSymbol() == Symbol.O) {
            if (remainingO < 8) {
                remainingO++;
            }
        } else if (pawn.getSymbol() == Symbol.X) {
            if (remainingX < 8) {
                remainingX++;
            }
        }
    }

    Color getColor() {
        return color;
    }

    int getRemainingX() {
        return remainingX;
    }

    int getRemainingO() {
        return remainingO;
    }

    Symbol getChosenTotemSym() {
        return chosenTotem;
    }

    void setChosenTotem(Symbol symbol) {
        this.chosenTotem = symbol;
    }

    void setXtoZero(){
        remainingX = 0;
    }

    void setOToZero(){
        remainingO = 0;
    }
}
