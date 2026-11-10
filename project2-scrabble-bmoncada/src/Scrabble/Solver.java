package Scrabble;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Finds the highest scoring legal move for a given board and tray
 */
public class Solver {
    /**
     * Runs the solver using a dictionary file and board/tray input read from standard input
     * @param args command-line arguments containing the dictionary file
     * @throws IOException if the dictionary file cannot be read
     */
    public static void main(String[] args) throws IOException {
        Dictionary dictionary = new Dictionary(args[0]);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            Board board = Board.readBoard(scanner);
            if (board == null) {
                break;
            }
            if (!scanner.hasNextLine()){
                break;
            }
            String trayInput = scanner.nextLine().trim();
            Tray tray = new Tray(trayInput);
            System.out.println("Input Board:");
            System.out.println(board);
            System.out.println("Tray: " + tray);
            Move bestMove = findBestMove(board, tray, dictionary);
            if (bestMove != null){
                System.out.println("Solution: " + bestMove.word() + " has " + bestMove.score() + " points.");
                System.out.println("Solution Board:");
                System.out.println(bestMove.board());
            } else {
                System.out.println("No solution found");
            }
            System.out.println();
        }
    }

    /**
     * Finds the highest scoring legal move for the given board and tray
     * @param board the current board
     * @param tray the available tray letters
     * @param dictionary the dictionary used to validate the words
     * @return the highest scoring move or null if no move is available
     */
    public static Move findBestMove(Board board, Tray tray, Dictionary dictionary){
        Move bestMove = null;
        for (int row = 0; row < board.getSize(); row++){
            for (int col = 0; col < board.getSize(); col++){
                bestMove = searchFrom(board, tray, dictionary, row, col, 0, 1, bestMove);
                bestMove = searchFrom(board, tray, dictionary, row, col, 1, 0, bestMove);
            }
        }
        return bestMove;
    }
    private static Move searchFrom(Board board, Tray tray, Dictionary dictionary, int row, int col,
                                   int rowChange, int colChange, Move bestMove){
        int previousRow = row - rowChange;
        int previousCol = col - colChange;
        if (board.isInBounds(previousRow, previousCol) && board.getSquare(previousRow, previousCol).isOccupied()){
            return bestMove;
        }
        Board testBoard = new Board(board);
        ArrayList<Character> letters = new ArrayList<>(tray.getLetters());
        return buildWord(board, testBoard, dictionary, letters, row, col, rowChange, colChange,
                "", new ArrayList<>(), bestMove);
    }
    private static Move buildWord(Board original, Board testBoard, Dictionary dictionary,
                                  ArrayList<Character> letters, int row, int col, int rowChange,
                                  int colChange, String word, ArrayList<int[]> placed, Move bestMove){
        if (!testBoard.isInBounds(row, col)){
            return bestMove;
        }
        Square square = testBoard.getSquare(row, col);
        if (square.isOccupied()){
            char letter = square.getTile().getLetter();
            String newWord = word + letter;
            if (!dictionary.isPrefix(newWord) && !dictionary.isWord(newWord)){
                return bestMove;
            }
            int nextRow = row + rowChange;
            int nextCol = col + colChange;
            if (dictionary.isWord(newWord) && !placed.isEmpty() && wordCanEnd(testBoard, nextRow, nextCol)
                && isConnected(original, placed) && crossWordsValid(testBoard, placed, dictionary,
                    rowChange, colChange)){
                int score = scoreMove(original, testBoard, placed, rowChange, colChange);
                if (bestMove == null || score > bestMove.score()){
                    bestMove = new Move(getDisplayWord(testBoard, row, col, rowChange, colChange),
                    new Board(testBoard), score);
                }
            }
            return buildWord(original, testBoard, dictionary, letters, nextRow, nextCol, rowChange, colChange,
                    newWord, placed, bestMove);
        }
        for (int i = 0; i < letters.size(); i++){
            char letter = letters.get(i);
            if (letter == '*') {
                for (char blankLetter = 'a'; blankLetter <= 'z'; blankLetter++) {
                    String newWord = word + blankLetter;
                    if (!dictionary.isPrefix(newWord) && !dictionary.isWord(newWord)) {
                        continue;
                    }
                    Tile tile = new Tile(blankLetter, 0, true);
                    square.placeTile(tile);
                    char removed = letters.remove(i);
                    placed.add(new int[]{row, col});
                    int nextRow = row + rowChange;
                    int nextCol = col + colChange;
                    if (dictionary.isWord(newWord) && wordCanEnd(testBoard, nextRow, nextCol) &&
                            isConnected(original, placed) && crossWordsValid(testBoard, placed, dictionary,
                            rowChange, colChange)) {
                        int score = scoreMove(original, testBoard, placed, rowChange, colChange);
                        if (bestMove == null || score > bestMove.score()) {
                            bestMove = new Move(getDisplayWord(testBoard, row, col, rowChange, colChange),
                                    new Board(testBoard), score);
                        }
                    }
                    bestMove = buildWord(original, testBoard, dictionary, letters, nextRow, nextCol,
                                         rowChange, colChange, newWord, placed, bestMove);
                    placed.remove(placed.size() - 1);
                    letters.add(i, removed);
                    square.removeTile();
                }
                continue;
            }
            String newWord = word + letter;
            if (!dictionary.isPrefix(newWord) && !dictionary.isWord(newWord)){
                continue;
            }
            Tile tile = new Tile(letter, Tile.getLetterValue(letter), false);
            square.placeTile(tile);
            char removed = letters.remove(i);
            placed.add(new int[]{row, col});
            int nextRow = row + rowChange;
            int nextCol = col + colChange;
            if (dictionary.isWord(newWord) && wordCanEnd(testBoard, nextRow, nextCol) && isConnected(original,placed)
                && crossWordsValid(testBoard, placed, dictionary, rowChange, colChange)){
                int score = scoreMove(original, testBoard, placed, rowChange, colChange);
                if (bestMove == null || score > bestMove.score()){
                    bestMove = new Move(getDisplayWord(testBoard, row, col, rowChange, colChange),
                            new Board(testBoard), score);
                }
            }
            bestMove = buildWord(original, testBoard, dictionary, letters, nextRow, nextCol, rowChange, colChange,
                    newWord, placed, bestMove);
            placed.remove(placed.size() - 1);
            letters.add(i, removed);
            square.removeTile();
        }
        return bestMove;
    }
    private static boolean wordCanEnd(Board board, int row, int col){
        return !board.isInBounds(row, col) || !board.getSquare(row, col).isOccupied();
    }
    private static boolean isConnected(Board original, ArrayList<int[]> placed){
        boolean emptyBoard = true;
        for (int row = 0; row < original.getSize(); row++){
            for (int col = 0; col < original.getSize(); col++){
                if (original.getSquare(row,col).isOccupied()){
                    emptyBoard = false;
                    break;
                }
            }
            if (!emptyBoard){
                break;
            }
        }
        if (emptyBoard){
            int center = original.getSize() / 2;
            for (int[] position : placed){
                if (position[0] == center && position[1] == center){
                    return true;
                }
            }
            return false;
        }
        int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };
        for (int[] position : placed){
            for (int[] direction : directions){
                int row = position[0] + direction[0];
                int col = position[1] + direction[1];
                if (original.isInBounds(row, col) && original.getSquare(row, col).isOccupied()){
                    return true;
                }
            }
        }
        return false;
    }
    private static boolean crossWordsValid(Board board, ArrayList<int[]> placed, Dictionary dictionary,
                                           int rowChange, int colChange){
        int crossRowChange = colChange;
        int crossColChange = rowChange;
        for (int[] position : placed){
            String word = getWord(board, position[0], position[1], crossRowChange, crossColChange);
            if (word.length() > 1 && !dictionary.isWord(word)){
                return false;
            }
        }
        return true;
    }
    private static int scoreMove(Board original, Board result, ArrayList<int[]> placed,
                                 int rowChange, int colChange){
        int[] first = placed.get(0);
        int score = scoreWord(original, result, first[0], first[1], rowChange, colChange);
        int crossRowChange = colChange;
        int crossColChange = rowChange;
        for (int[] position : placed){
            String crossWord = getWord(result, position[0], position[1], crossRowChange, crossColChange);
            if (crossWord.length() > 1){
                score += scoreWord(original, result, position[0], position[1], crossRowChange, crossColChange);
            }
        }
        if (placed.size() == 7){
            score += 50;
        }
        return score;
    }
    private static String getWord(Board board, int row, int col, int rowChange, int colChange){
        int startRow = row;
        int startCol = col;
        while (board.isInBounds(startRow - rowChange, startCol - colChange) &&
                board.getSquare(startRow - rowChange, startCol - colChange).isOccupied()){
            startRow -= rowChange;
            startCol -= colChange;
        }
        StringBuilder word = new StringBuilder();
        while (board.isInBounds(startRow, startCol) && board.getSquare(startRow, startCol).isOccupied()){
            word.append(board.getSquare(startRow, startCol).getTile().getLetter());
            startRow += rowChange;
            startCol += colChange;
        }
        return word.toString();
    }
    private static int scoreWord(Board original, Board result, int row, int col, int rowChange, int colChange){
        while (result.isInBounds(row - rowChange, col - colChange) &&
            result.getSquare(row - rowChange, col - colChange).isOccupied()){
            row -= rowChange;
            col -= colChange;
        }
        int wordScore = 0;
        int wordMultiplier = 1;
        while (result.isInBounds(row, col) && result.getSquare(row, col).isOccupied()){
            Tile tile = result.getSquare(row, col).getTile();
            if (original.getSquare(row, col).isOccupied()){
                wordScore += tile.getValue();
            } else {
                Square square = original.getSquare(row, col);
                wordScore += tile.getValue() * square.getLetterMultiplier();
                wordMultiplier *= square.getWordMultiplier();
            }
            row += rowChange;
            col += colChange;
        }
        return wordScore * wordMultiplier;
    }
    private static String getDisplayWord(Board board, int row, int col, int rowChange, int colChange){
        while (board.isInBounds(row - rowChange, col -colChange) && board.getSquare(
                row - rowChange, col - colChange).isOccupied()){
        row -= rowChange;
        col -= colChange;
        }
        StringBuilder word = new StringBuilder();
        while (board.isInBounds(row, col) && board.getSquare(row, col).isOccupied()) {
            Tile tile = board.getSquare(row, col).getTile();
            char letter = tile.getLetter();
            if (tile.isBlank()) {
                letter = Character.toUpperCase(letter);
            }
            word.append(letter);
            row += rowChange;
            col += colChange;
        }
        return word.toString();
    }
}
