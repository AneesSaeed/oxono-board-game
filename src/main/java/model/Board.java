package model;

class Board {
    private final int rows;
    private final int cols;
    private Position posTotemX;
    private Position posTotemO;

    private final Tile[][] grid;

    Board(int rows, int cols){
        if (rows < 4 || cols < 4) {
            throw new IllegalArgumentException("Board size must be at least 4x4.");
        }
        this.rows = rows;
        this.cols = cols;
        grid = new Tile[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Tile();
            }
        }
        posTotemX = new Position((rows / 2)-1,(cols / 2)-1);
        posTotemO = new Position((rows / 2),(cols / 2));
        grid[posTotemX.x()][posTotemX.y()].setToken(new Totem(Symbol.X));
        grid[posTotemO.x()][posTotemO.y()].setToken(new Totem(Symbol.O));
    }


    void moveTotem(Totem totem, Position newPos){
        Tile targetTile = getTile(newPos);
        targetTile.setToken(totem);
        refreshTotemPos(totem, newPos);
    }

    void insertPawn(Pawn pawn, Position pos){
        Tile targetTile = getTile(pos);
        if (!targetTile.isEmpty()){
            throw new IllegalStateException("Tile is already occupied");
        }
        targetTile.setToken(pawn);
    }

    Tile getTile(Position pos){
        return grid[pos.x()][pos.y()];
    }

    int getCols() {
        return cols;
    }

    int getRows() {
        return rows;
    }

    public Position getTotemPos(Totem totem){
        return (totem.getSymbol() == Symbol.O) ? posTotemO : posTotemX;
    }

    public Position getTotemPos(Symbol sym){
        return (sym == Symbol.O) ? posTotemO : posTotemX;
    }

    private void refreshTotemPos(Totem totem, Position newPos){
        Position oldPos = getTotemPos(totem);
        getTile(oldPos).removeToken();

        if (totem.getSymbol() == Symbol.X){
            posTotemX = newPos;
        }else{
            posTotemO = newPos;
        }
    }
}

