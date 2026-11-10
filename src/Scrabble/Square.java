package Scrabble;

/**
 * Represents one square on a Scrabble board. A square stores it's multipliers
 * and any tile placed on it
 */
public class Square {
    private Tile tile;
    private final int letterMultiplier;
    private final int wordMultiplier;

    /**
     * Creates an empty board square
     * @param letterMultiplier the square's letter multiplier
     * @param wordMultiplier the square's word multiplier
     */
    public Square(int letterMultiplier, int wordMultiplier){
        this.letterMultiplier = letterMultiplier;
        this.wordMultiplier = wordMultiplier;
        this.tile = null;
    }

    /**
     * Gets the tile currently on this square
     * @return the tile, or null if the square is empty
     */
    public Tile getTile(){
        return tile;
    }

    /**
     * Checks whether the square contains a tile
     * @return true if the square contains a tile
     */
    public boolean isOccupied(){
        return tile != null;
    }

    /**
     * Places a tile on this square
     * @param tile the tile to place
     */
    public void placeTile(Tile tile){
        this.tile = tile;
    }

    /**
     * Gets the letter multiplier
     * @return the letter multiplier
     */
    public int getLetterMultiplier(){
        return letterMultiplier;
    }

    /**
     * Removes the tile from this square
     */
    public void removeTile(){
        tile = null;
    }

    /**
     * Gets the word multipler
     * @return the word multiplier
     */
    public int getWordMultiplier() {
        return wordMultiplier;
    }
}
