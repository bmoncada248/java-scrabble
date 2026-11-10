package Scrabble;

import java.util.ArrayList;

/**
 * Controls the main Scrabble game including players, turns, scoring, the tile bag, and game ending rules
 */
public class ScrabbleGame {
    private final Board board;
    private final Bag bag;
    private final Player humanPlayer;
    private final Player computerPlayer;
    private final Dictionary dictionary;
    private boolean gameOver;
    private String gameOverMessage;

    /**
     * Creates a Scrabble game and deals seven tiles to the human and computer players
     * @param board the board used for the game
     * @param dictionary the dictionary used to validate words
     */
    public ScrabbleGame(Board board, Dictionary dictionary){
        gameOver = false;
        gameOverMessage = "";
        this.board = board;
        this.dictionary = dictionary;
        bag = new Bag();
        humanPlayer = new Player();
        computerPlayer = new Player();
        humanPlayer.drawTiles(bag);
        computerPlayer.drawTiles(bag);

    }

    /**
     * Gets the current game board
     * @return the current game board
     */
    public Board getBoard(){
        return board;
    }

    /**
     * Gets the human player
     * @return the human player
     */
    public Player getHumanPlayer(){
        return humanPlayer;
    }

    /**
     * Gets the computer player
     * @return the computer player
     */
    public Player getComputerPlayer(){
        return computerPlayer;
    }

    /**
     * Gets the game's tile bag
     * @return the tile bag
     */
    public Bag getBag(){
        return bag;
    }

    /**
     * Gets the dictionary used by the game
     * @return the dictionary
     */
    public Dictionary getDictionary(){
        return dictionary;
    }

    /**
     * Performs the computer player's turn using the solver
     * @return the computer's move, or null if no move is available
     */
    public Move computerTurn(){
        Move move = Solver.findBestMove(board, computerPlayer.getTray(), dictionary);
        if (move == null){
            checkGameOver();
            return null;
        }
        Board resultBoard = move.board();
        for (int row = 0; row < board.getSize(); row++){
            for (int col = 0; col < board.getSize(); col++){
                Square oldSquare = board.getSquare(row, col);
                Square newSquare = resultBoard.getSquare(row, col);
                if (!oldSquare.isOccupied() && newSquare.isOccupied()){
                    Tile tile = newSquare.getTile();
                    oldSquare.placeTile(tile);
                    if (tile.isBlank()){
                        computerPlayer.getTray().removeLetter('*');
                    } else {
                        computerPlayer.getTray().removeLetter(tile.getLetter());
                    }
                }
            }
        }
        computerPlayer.addScore(move.score());
        computerPlayer.drawTiles(bag);
        checkGameOver();
        return move;
    }

    /**
     * Attempts to apply a human player's move
     * @param resultBoard the board containing the attempted move
     * @return the score of the move, or -1 if the move is illegal
     */
    public int humanTurn(Board resultBoard){
        int score = ScoreChecker.checkPlay(board, resultBoard, dictionary);
        if (score == -1){
            return -1;
        }
        ArrayList<Character> availableTiles = new ArrayList<>(humanPlayer.getTray().getLetters());
        for (int row = 0; row < board.getSize(); row++){
            for (int col = 0; col < board.getSize(); col++){
                Square oldSquare = board.getSquare(row, col);
                Square newSqaure = resultBoard.getSquare(row, col);
                if (!oldSquare.isOccupied() && newSqaure.isOccupied()){
                    Tile tile = newSqaure.getTile();
                    char needed;
                    if (tile.isBlank()){
                        needed = '*';
                    } else {
                        needed = tile.getLetter();
                    }
                    if (!availableTiles.remove(Character.valueOf(needed))){
                        return -1;
                    }
                }
            }
        }
        for (int row = 0; row < board.getSize(); row++){
            for (int col = 0; col < board.getSize(); col++){
                Square oldSquare = board.getSquare(row, col);
                Square newSquare = resultBoard.getSquare(row, col);
                if (!oldSquare.isOccupied() && newSquare.isOccupied()){
                    Tile tile = newSquare.getTile();
                    oldSquare.placeTile(tile);
                    if (tile.isBlank()){
                        humanPlayer.getTray().removeLetter('*');
                    } else {
                        humanPlayer.getTray().removeLetter(tile.getLetter());
                    }
                }
            }
        }
        humanPlayer.addScore(score);
        humanPlayer.drawTiles(bag);
        checkGameOver();
        return score;
    }

    /**
     * Exchanges selected human-player tiles with tiles from the bag
     * @param letters the letters to exchange
     * @return true if the exchange was completed, false otherwise
     */
    public boolean exchangeTiles(ArrayList<Character> letters){
        if (letters.isEmpty() || bag.size() < letters.size()){
            return false;
        }
        ArrayList<Character> availableTiles = new ArrayList<>(humanPlayer.getTray().getLetters());
        for (char letter : letters){
            if (!availableTiles.remove(Character.valueOf(letter))){
                return false;
            }
        }
        for (char letter : letters) {
            humanPlayer.getTray().removeLetter(letter);
        }
        humanPlayer.drawTiles(bag);
        for (char letter:letters){
            if (letter == '*'){
                bag.returnTile(new Tile('*', 0, true));
            } else {
                bag.returnTile(new Tile(letter, Tile.getLetterValue(letter), false));
            }
        }
        bag.shuffle();
        checkGameOver();
        return true;
    }

    /**
     * Passes the human's player turn
     */
    public void passHumanTurn(){
        checkGameOver();
    }
    private int getTrayValue(Tray tray){
        int value = 0;
        for (char letter : tray.getLetters()){
            if (letter != '*'){
                value += Tile.getLetterValue(letter);
            }
        }
        return value;
    }
    private void checkGameOver(){
        if (gameOver){
            return;
        }
        boolean humanOut = bag.isEmpty() && humanPlayer.getTray().size() == 0;
        boolean computerOut = bag.isEmpty() && computerPlayer.getTray().size() == 0;
        if (humanOut){
            int humanBefore = humanPlayer.getScore();
            int computerBefore = computerPlayer.getScore();
            int computerTiles = getTrayValue(computerPlayer.getTray());
            humanPlayer.addScore(computerTiles);
            computerPlayer.addScore(-computerTiles);
            finishGame(humanBefore, computerBefore);
            return;
        }
        if (computerOut){
            int humanBefore = humanPlayer.getScore();
            int computerBefore = computerPlayer.getScore();
            int humanTiles = getTrayValue(humanPlayer.getTray());
            computerPlayer.addScore(humanTiles);
            humanPlayer.addScore(-humanTiles);
            finishGame(humanBefore, computerBefore);
            return;
        }
        if (bag.isEmpty()){
            Move humanMove = Solver.findBestMove(board, humanPlayer.getTray(), dictionary);
            Move computerMove = Solver.findBestMove(board, computerPlayer.getTray(), dictionary);
            if (humanMove == null && computerMove == null){
                int humanBefore = humanPlayer.getScore();
                int computerBefore = computerPlayer.getScore();
                humanPlayer.addScore(-getTrayValue(humanPlayer.getTray()));
                computerPlayer.addScore(-getTrayValue(computerPlayer.getTray()));
                finishGame(humanBefore, computerBefore);
            }
        }
    }
    private void finishGame(int humanBefore, int computerBefore){
        gameOver = true;
        if (humanPlayer.getScore() > computerPlayer.getScore()){
            gameOverMessage = "Game Over - You Win!";
        } else if (computerPlayer.getScore() > humanPlayer.getScore()){
            gameOverMessage = "Game Over - Computer Wins!";
        } else {
            if (humanBefore > computerBefore) {
                gameOverMessage = "Game Over - You Win!";
            } else if (computerBefore > humanBefore) {
                gameOverMessage = "Game Over - Computer Wins!";
            } else {
                gameOverMessage = "Game Over - Draw!";
            }
        }
    }

    /**
     * Checks whether the game has ended
     * @return true if the game is over
     */
    public boolean isGameOver(){
        return gameOver;
    }

    /**
     * Gets the message describing the result of the finished game
     * @return the game over message
     */
    public String getGameOverMessage(){
        return gameOverMessage;
    }
}
