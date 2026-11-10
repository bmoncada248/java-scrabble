package Scrabble;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Checks plays for board compatability, legality, and score
 */
public class ScoreChecker {
    private record Placement(int row, int col, Tile tile) {
    }

    /**
     * Runs the score checker using a dictionary file and board pairs read from standard input
     * @param args command-line arguments containing the dictionary files
     * @throws IOException if the dictionary file cannot be read
     */
    public static void main(String[] args) throws IOException {
        Dictionary dictionary = new Dictionary(args[0]);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            Board originalBoard = Board.readBoard(scanner);
            if (originalBoard == null){
                break;
            }
            Board resultBoard = Board.readBoard(scanner);
            if (resultBoard == null){
                break;
            }
            System.out.println("original board:");
            System.out.println(originalBoard);
            System.out.println("result board:");
            System.out.println(resultBoard);
            if (!boardsCompatible(originalBoard, resultBoard)) {
                System.out.println("Boards are not compatible");
                System.out.println();
                continue;
            }
            ArrayList<Placement> playedTiles = findPlayedTiles(originalBoard, resultBoard);
            printPlayedTiles(playedTiles);
            boolean legal = isLegalPlay(originalBoard, resultBoard, playedTiles, dictionary);
            if (legal) {
                System.out.println("play is legal");
                int score = calculateScore(originalBoard, resultBoard, playedTiles);
                System.out.println("score is " + score);
            } else {
                System.out.println("play is not legal");
            }
            System.out.println();
        }
    }

    /**
     * Checks whether a resulting board represents a legal play and calculates its score
     * @param original the board before the play
     * @param result the board after the play
     * @param dictionary the dictionary used to validate the words
     * @return the score of the play or -1 if the play is illegal
     */
    public static int checkPlay(Board original, Board result, Dictionary dictionary){
        if (!boardsCompatible(original, result)){
            return -1;
        }
        ArrayList<Placement> playedTiles = findPlayedTiles(original, result);
        if (!isLegalPlay(original, result, playedTiles, dictionary)){
            return -1;
        }
        return calculateScore(original, result, playedTiles);
    }
    private static boolean boardsCompatible(Board original, Board result) {
        if (original.getSize() != result.getSize()) {
            return false;
        }
        for (int row = 0; row < original.getSize(); row++) {
            for (int col = 0; col < original.getSize(); col++) {
                Square oldSquare = original.getSquare(row, col);
                Square newSquare = result.getSquare(row, col);
                if (oldSquare.isOccupied()) {
                    if (!newSquare.isOccupied()) {
                        return false;
                    }
                    Tile oldTile = oldSquare.getTile();
                    Tile newTile = newSquare.getTile();
                    if (oldTile.getLetter() != newTile.getLetter()) {
                        return false;
                    }
                    if (oldTile.isBlank() != newTile.isBlank()) {
                        return false;
                    }
                } else if (!newSquare.isOccupied()){
                    if (oldSquare.getLetterMultiplier() != newSquare.getLetterMultiplier()){
                        return false;
                    }
                    if (oldSquare.getWordMultiplier() != newSquare.getWordMultiplier()){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    private static ArrayList<Placement> findPlayedTiles(Board original, Board result){
        ArrayList<Placement> playedTiles = new ArrayList<>();
        for (int row = 0; row < original.getSize(); row++){
            for (int col = 0; col < original.getSize(); col++){
                Square oldSquare = original.getSquare(row, col);
                Square newSquare = result.getSquare(row, col);
                if (!oldSquare.isOccupied() && newSquare.isOccupied()){
                    playedTiles.add(new Placement(row, col, newSquare.getTile()));
                }
            }
        }
        return playedTiles;
    }

    private static void printPlayedTiles(ArrayList<Placement> playedTiles){
        if (playedTiles.isEmpty()){
            System.out.println("play is empty");
            return;
        }
        System.out.print("play is ");
        for (int i = 0; i < playedTiles.size(); i++){
            Placement placement = playedTiles.get(i);
            System.out.print(placement.tile().getLetter() + " at (" + placement.row() + ", " +
                    placement.col() + ")");
            if (i < playedTiles.size() - 1){
                System.out.print(", ");
            }
        }
        System.out.println();
    }
    private static boolean isLegalPlay(
            Board original, Board result, ArrayList<Placement> playedTiles, Dictionary dictionary){
        if (playedTiles.isEmpty()){
            return false;
        }
        if (!sameRowOrColumn(playedTiles)){
            return false;
        }
        if(!noGaps(result, playedTiles)){
            return false;
        }
        if (boardIsEmpty(original)){
            if(playedTiles.size() < 2){
                return false;
            }
            if(!coversCenter(playedTiles, original.getSize())){
                return false;
            }
        } else {
            if (!touchesExistingTile(original, playedTiles)){
                return false;
            }
        }
        return allWordsValid(result, playedTiles, dictionary);
    }
    private static boolean sameRowOrColumn(ArrayList<Placement> playedTiles){
        int firstRow = playedTiles.get(0).row();
        int firstCol = playedTiles.get(0).col();
        boolean sameRow = true;
        boolean sameCol = true;
        for (Placement placement : playedTiles){
            if (placement.row() != firstRow) {
                sameRow = false;
            }
            if (placement.col() != firstCol) {
                sameCol = false;
            }
        }
        return sameRow || sameCol;
    }
    private static boolean noGaps(Board board, ArrayList<Placement> playedTiles){
        if (playedTiles.size() == 1){
            return true;
        }
        boolean horizontal = playedTiles.get(0).row() == playedTiles.get(1).row();
        if (horizontal) {
            int row = playedTiles.get(0).row();
            int minCol = playedTiles.get(0).col();
            int maxCol = playedTiles.get(0).col();
            for (Placement placement : playedTiles) {
                minCol = Math.min(minCol, placement.col());
                maxCol = Math.max(maxCol, placement.col());
            }
            for (int col = minCol; col <= maxCol; col++){
                if (!board.getSquare(row, col).isOccupied()){
                    return false;
                }
            }
        } else {
            int col = playedTiles.get(0).col();
            int minRow = playedTiles.get(0).row();
            int maxRow = playedTiles.get(0).row();
            for (Placement placement : playedTiles){
                minRow = Math.min(minRow, placement.row());
                maxRow = Math.max(maxRow, placement.row());
            }
            for (int row = minRow; row <= maxRow; row++){
                if (!board.getSquare(row, col).isOccupied()){
                    return false;
                }
            }
        }
        return true;
    }
    private static boolean boardIsEmpty(Board board){
        for (int row = 0; row < board.getSize(); row++){
            for (int col = 0; col < board.getSize(); col++){
                if (board.getSquare(row, col).isOccupied()){
                    return false;
                }
            }
        }
        return true;
    }
    private static boolean coversCenter(ArrayList<Placement> playedTiles, int size){
        int center = size/2;
        for (Placement placement : playedTiles){
            if (placement.row() == center && placement.col() == center){
                return true;
            }
        }
        return false;
    }
    private static boolean touchesExistingTile(Board original, ArrayList<Placement> playedTiles){
        int[][] directions = {{-1, 0}, {1,0}, {0, -1}, {0, 1}};
        for (Placement placement : playedTiles){
            for (int[] direction : directions){
                int row = placement.row() + direction[0];
                int col = placement.col() + direction[1];
                if (original.isInBounds(row, col) && original.getSquare(row,col).isOccupied()){
                    return true;
                }
            }
        }
        return false;
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
    private static boolean allWordsValid(Board board, ArrayList<Placement> playedTiles,Dictionary dictionary){
        boolean wordFound = false;
        for (Placement placement : playedTiles){
            String horizontal = getWord(board, placement.row(), placement.col(), 0, 1);
            String vertical = getWord(board, placement.row(), placement.col(), 1,0);
            if (horizontal.length() > 1){
                wordFound = true;
                if (!dictionary.isWord(horizontal)){
                    return false;
                }
            }
            if (vertical.length() > 1){
                wordFound = true;
                if (!dictionary.isWord(vertical)){
                    return false;
                }
            }
        }
        return wordFound;
    }
    private static int calculateScore(Board original, Board result, ArrayList<Placement> playedTiles){
        int score = 0;
        if (playedTiles.size() == 1) {
            Placement placement = playedTiles.get(0);
            String horizontal = getWord(result, placement.row(), placement.col(), 0, 1);
            String vertical = getWord(result, placement.row(), placement.col(), 1, 0);
            if (horizontal.length() > 1) {
                score += scoreWord(original, result, placement.row(), placement.col(), 0, 1);
            }
            if (vertical.length() > 1){
                score += scoreWord(original, result, placement.row(), placement.col(), 1, 0);
            }
        } else {
            boolean horizontal = playedTiles.get(0).row() == playedTiles.get(1).row();
            Placement first = playedTiles.get(0);
            if (horizontal) {
                score += scoreWord(original, result, first.row(), first.col(), 0, 1);
                for (Placement placement : playedTiles) {
                    String crossWord = getWord(result, placement.row(), placement.col(), 1, 0);
                    if (crossWord.length() > 1){
                        score += scoreWord(original, result, placement.row(), placement.col(), 1, 0);
                    }
                }
            } else {
                score += scoreWord(original, result, first.row(), first.col(), 1, 0);
                for (Placement placement : playedTiles){
                    String crossWord = getWord(result, placement.row(), placement.col(), 0, 1);
                    if (crossWord.length() > 1) {
                        score += scoreWord(original, result, placement.row(), placement.col(), 0, 1);
                    }
                }
            }
        }
        if (playedTiles.size() == 7) {
            score += 50;
        }
        return score;
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
                Square oldSquare = original.getSquare(row, col);
                wordScore += tile.getValue() * oldSquare.getLetterMultiplier();
                wordMultiplier *= oldSquare.getWordMultiplier();
            }
            row += rowChange;
            col += colChange;
        }
        return wordScore * wordMultiplier;
    }
}
