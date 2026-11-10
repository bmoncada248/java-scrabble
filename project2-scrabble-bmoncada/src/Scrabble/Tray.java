package Scrabble;

import java.util.ArrayList;

/**
 * Represents the letters currently held by a player
 */
public class Tray {
    private final ArrayList<Character> letters;

    /**
     * Creates a tray from a string of letters. An asterisk represents a blank tile
     * @param input the letters to place in the tray
     */
    public Tray(String input){
        letters = new ArrayList<>();
        for (char letter : input.toCharArray()){
            letters.add(Character.toLowerCase(letter));
        }
    }

    /**
     * Gets the letters currently in the tray
     * @return the letters currently in the tray
     */
    public ArrayList<Character> getLetters() {
        return letters;
    }

    /**
     * Returns the tray as a string of letters
     * @return the trays contents
     */
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        for (char letter : letters){
            result.append(letter);
        }
        return result.toString();
    }

    /**
     * Adds a tile to the tray
     * @param tile the tile to add
     */
    public void addTile(Tile tile){
        if (tile.isBlank()){
            letters.add('*');
        } else {
            letters.add(tile.getLetter());
        }
    }

    /**
     * Removes a letter from the tray
     * @param letter the letter to remove
     */
    public void removeLetter(char letter){
        letters.remove(Character.valueOf(letter));
    }

    /**
     * Gets the number of tiles in the tray
     * @return the tray size
     */
    public int size(){
        return letters.size();
    }
}

