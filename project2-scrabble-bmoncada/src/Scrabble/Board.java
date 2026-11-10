package Scrabble;

import java.util.Scanner;

/**
 * Represents the Scrabble board and the squares contained on it.
 */
public class Board {
    private final Square[][] squares;

    /**
     * Creates an empty square board of the given size
     * @param size the number of rows and columns
     */
    public Board(int size){
        squares = new Square[size][size];

        for (int row = 0; row < size; row++){
            for (int col = 0; col < size; col++){
                squares[row][col] = new Square(1,1);
            }
        }
    }

    /**
     * Creates a copy of another board
     * @param other the other board to copy
     */
    public Board(Board other){
        squares = new Square[other.getSize()][other.getSize()];
        for (int row = 0; row < other.getSize(); row++){
            for (int col = 0; col < other.getSize(); col++){
                Square oldSquare = other.getSquare(row, col);
                Square newSquare = new Square(oldSquare.getLetterMultiplier(), oldSquare.getWordMultiplier());
                if (oldSquare.isOccupied()){
                    Tile oldTile = oldSquare.getTile();
                    newSquare.placeTile(new Tile(oldTile.getLetter(), oldTile.getValue(), oldTile.isBlank()));
                }
                squares[row][col] = newSquare;
            }
        }
    }

    /**
     * Gets the size of the board
     * @return the number of rows and columns
     */
    public int getSize(){
        return squares.length;
    }

    /**
     * Gets the square at the specified position
     * @param row the row of the square
     * @param col the column of the square
     * @return the square at the specified position
     */
    public Square getSquare(int row, int col){
        return squares[row][col];
    }

    /**
     * Sets the square at the specified position
     * @param row the row of the square
     * @param col the column of the square
     * @param square the square to place at the position
     */
    public void setSquare(int row, int col, Square square){
        squares[row][col] = square;
    }

    /**
     * Checks whether a position is on the board
     * @param row the row to check
     * @param col the column to check
     * @return true if the position is on the board
     */
    public boolean isInBounds(int row, int col){
        return row >= 0 && row < squares.length && col >= 0 && col < squares.length;
    }

    /**
     * Reads a Scrabble board from a scanner
     * @param scanner the scanner containing the board input
     * @return the board that was read , or null if there is no board
     */
    public static Board readBoard(Scanner scanner) {
        String line;
        String squareText;
        do {
            if(!scanner.hasNextLine()){
                return null;
            }
            line = scanner.nextLine().trim();
        } while (line.isEmpty());
        int size = Integer.parseInt(line);
        Board board = new Board(size);
        for (int row = 0; row < size; row++){
            line = scanner.nextLine();
            Scanner rowScanner = new Scanner(line);
            for (int col = 0; col < size; col++){
                squareText = rowScanner.next();
                if (squareText.length() == 1){
                    char letter = squareText.charAt(0);
                    boolean blank = Character.isUpperCase(letter);
                    letter = Character.toLowerCase(letter);
                    int value;
                    if (blank) {
                        value = 0;
                    } else {
                        value = Tile.getLetterValue(letter);
                    }
                    Square square = new Square(1,1);
                    square.placeTile(new Tile(letter, value, blank));
                    board.setSquare(row, col, square);
                } else {
                    char first = squareText.charAt(0);
                    char second = squareText.charAt(1);
                    int wordMultiplier;
                    int letterMultiplier;
                    if (first == '.') {
                        wordMultiplier = 1;
                    } else {
                        wordMultiplier = Character.getNumericValue(first);
                    }
                    if (second == '.'){
                        letterMultiplier = 1;
                    } else {
                        letterMultiplier = Character.getNumericValue(second);
                    }
                    board.setSquare(row, col, new Square(letterMultiplier, wordMultiplier));
                }
            }
            rowScanner.close();
        }
        return board;
    }

    /**
     * Returns the board as a string
     * @return the board as a string
     */
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        for (int row = 0; row < getSize(); row++){
            for (int col = 0; col < getSize(); col++){
                Square square = squares[row][col];
                if (square.isOccupied()){
                    Tile tile = square.getTile();
                    char letter  = tile.getLetter();
                    if (tile.isBlank()){
                        letter = Character.toUpperCase(letter);
                    }
                    result.append(" ").append(letter);
                } else {
                    if (square.getWordMultiplier() == 1){
                        result.append(".");
                    } else {
                        result.append(square.getWordMultiplier());
                    }
                    if (square.getLetterMultiplier() == 1){
                        result.append(".");
                    } else {
                        result.append(square.getLetterMultiplier());
                    }
                }
                if (col < getSize() - 1){
                    result.append(" ");
                }
            }
            if (row < getSize() - 1){
                result.append("\n");
            }
        }
        return result.toString();
    }
}
