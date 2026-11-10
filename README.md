# Java Scrabble Game & Solver
This project implements Scrabble. It contains three seperate
runnable programs:\
scorechecker.jar - checks whether a play is legal and calculates its score.\
solver.jar - finds the highest scoring legal move for a given board and tray.\
scrabble.jar - runs a JavaFX Scrabble game between a human player and a computer player.\
The programs share classes for the board, tiles, dictionary, scoring, and move validation.

## Classes:
Tile - Represents a scrabble tile.\
Square - Represents one board position. \
Board - Stores the scrabble board.\
Dictionary - Stores valid words and prefixes.\
Tray - Stores a player's tiles.\
Bag - Stores the remaining tiles.\
Player - Stores a player's tray and score.\
Move - Stores a possible move and its score.\
ScoreChecker - Checks legality and scoring\
Solver - Finds the highest scoring legal move.\
ScrabbleGame - Controls the main game.\
ScrabbleGUI - Provides the JavaFX interface.

## Game Controls: 
- Select a tray tile, then select a board square to place it.
- "Submit move" checks and plays the move
- "Reset move" removes the tiles placed during the current turn
- "Pass move" skips the human player's turn
- "Exchange tiles" allows the player to select tiles and exchange them for tiles from the bag.
- For a blank tile ('*'), select the blank, choose a letter using the a-z buttons, then place it on the board.
The human player always goes first.
Moves are checked automatically using the selected dictionary, so there is no challenge system.
## Solver
The dictionary uses two HashSet<String> objects. One stores complete words and the other stores valid word prefixes.
The solver searches possible horizontal and vertical moves using the letters on the board and in the tray. It stops
searching when the current letters are not a valid prefix. For each possible word, the Solver checks that the move is legal,
checks any crosswords that are created, calculates the score, and keeps the highest scoring move. Blank tiles are also
handled by trying possible letters while keeping the blank's score equal to zero.
## Game Rules
The game ends when all tiles have been drawn and a player uses their last tile, or when the bag is empty and neither player
has a legal move. At the end of the game, unplayed tile values are deducted from the player's score. If one player uses all
their tiles, the value of the other player's remaining tiles is added to their score.

## How to Run
Requires Java 25.
Run the Scrabble GUI from the project directory:
java -jar scrabble.jar dictionaries_and_examples/sowpods.txt dictionaries_and_examples/scrabble_board.txt
## Screenshots

### Game in Progress
![Scrabble Gameplay](screenshots/Scrabble GUI2.png)

### Starting Board
![Scrabble starting board](screenshots/Scrabble GUI.png)
## Bugs
There are no known bugs. 
