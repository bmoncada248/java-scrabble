package Scrabble;

/**
 * Represents a possible move with its word, resulting board, and score
 * @param word the word created by the move
 * @param board the board after the move
 * @param score the number of points earned
 */
public record Move(String word, Board board, int score){
    }

