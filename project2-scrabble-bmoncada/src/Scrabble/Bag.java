package Scrabble;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents the Scrabble tile bag. The bag contains the standard 100 Scrabble tiles
 */
public class Bag {
    private final ArrayList<Tile> tiles;

    /**
     * Creates and shuffles the tile bag
     */
    public Bag() {
        tiles = new ArrayList<>();
        createTiles();
        Collections.shuffle(tiles);
    }
    private void createTiles(){
        addTiles('a', 9);
        addTiles('b', 2);
        addTiles('c', 2);
        addTiles('d', 4);
        addTiles('e', 12);
        addTiles('f', 2);
        addTiles('g', 3);
        addTiles('h', 2);
        addTiles('i', 9);
        addTiles('j', 1);
        addTiles('k', 1);
        addTiles('l', 4);
        addTiles('m', 2);
        addTiles('n', 6);
        addTiles('o', 8);
        addTiles('p', 2);
        addTiles('q', 1);
        addTiles('r', 6);
        addTiles('s', 4);
        addTiles('t', 6);
        addTiles('u', 4);
        addTiles('v', 2);
        addTiles('w', 2);
        addTiles('x', 1);
        addTiles('y', 2);
        addTiles('z', 1);
        tiles.add(new Tile('*', 0, true));
        tiles.add(new Tile('*', 0, true));
    }
    private void addTiles(char letter, int amount){
        for (int i = 0; i < amount; i++){
            tiles.add(new Tile(letter, Tile.getLetterValue(letter), false));
        }
    }

    /**
     * Checks whether the bag is empty
     * @return true if no tiles remain
     */
    public boolean isEmpty(){
        return tiles.isEmpty();
    }

    /**
     * Gets the number of tiles remaining in the bag
     * @return the number of tiles remaining in the bag
     */
    public int size(){
        return tiles.size();
    }

    /**
     * Draws a tile from the bag
     * @return the drawn tile, or null if bag is empty
     */
    public Tile drawTile(){
        if (tiles.isEmpty()){
            return null;
        }
        return tiles.remove(tiles.size() - 1);
    }

    /**
     * Returns a tile to the bag
     * @param tile the tile to return
     */
    public void returnTile(Tile tile){
        tiles.add(tile);
    }

    /**
     * Randomly shuffles the tiles in the bag
     */
    public void shuffle(){
        Collections.shuffle(tiles);
    }
}
