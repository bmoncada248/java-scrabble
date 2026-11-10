package Scrabble;

/**
 * Represents a player with a tray and score
 */
public class Player {
    private final Tray tray;
    private int score;

    /**
     * Creates a player with an empty tray and a score of zero
     */
    public Player(){
        tray = new Tray("");
        score = 0;
    }

    /**
     * Gets the player's tray
     * @return the player's tray
     */
    public Tray getTray(){
        return tray;
    }

    /**
     * Gets the player's current score
     * @return the player's current score
     */
    public int getScore(){
        return score;
    }

    /**
     * Adds points to the player's score
     * @param points the number of points to add
     */
    public void addScore(int points){
        score += points;
    }

    /**
     * Draws tiles until the player's tray contains 7 tiles or the bag becomes empty
     * @param bag the tile bag to draw from
     */
    public void drawTiles(Bag bag){
        while (tray.size() < 7 && !bag.isEmpty()){
            tray.addTile(bag.drawTile());
        }
    }
}
