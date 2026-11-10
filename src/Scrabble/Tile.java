package Scrabble;

/**
 * Represents a scrabble tile with a letter, point value, and whether the tile is a blank
 */
public class Tile {
    private final char letter;
    private final int value;
    private final boolean blank;

    /**
     * Creates a tile
     * @param letter the letter represented by the tile
     * @param value the point value of the tile
     * @param blank whether the tile is a blank value
     */
    public Tile(char letter, int value, boolean blank) {
        this.letter = Character.toLowerCase(letter);
        this.value = value;
        this.blank = blank;
    }

    /**
     * Gets the tile's letter
     * @return the tile's letter
     */
    public char getLetter(){
        return letter;
    }

    /**
     * Gets the tile's point value
     * @return the tile's point value
     */
    public int getValue(){
        return value;
    }

    /**
     * Checks whether the tile is a blank
     * @return true if the tile is blank, false otherwise
     */
    public boolean isBlank(){
        return blank;
    }

    /**
     * Gets the point value for a letter
     * @param letter the letter to check
     * @return the point value of the letter
     */
    public static int getLetterValue(char letter) {
        letter = Character.toLowerCase(letter);
        return switch(letter) {
            case 'a', 'e', 'i', 'l', 'n', 'o', 'r', 's', 't', 'u' -> 1;
            case 'd', 'g' -> 2;
            case 'b', 'c', 'm', 'p' -> 3;
            case 'f', 'h', 'v', 'w', 'y' -> 4;
            case 'k' -> 5;
            case 'j', 'x' -> 8;
            case 'q', 'z' -> 10;
            default -> 0;
        };
    }
}
