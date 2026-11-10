package Scrabble;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Provides the JavaFX GUI for the Scrabble game
 */
public class ScrabbleGUI extends Application {
    private ScrabbleGame game;
    private int selectedTrayIndex = -1;
    private Board pendingBoard;
    private Button selectedTrayButton;
    private BorderPane root;
    private char selectedBlankLetter = '\0';
    private boolean exchangeMode = false;
    private final ArrayList<Integer> exchangeIndices = new ArrayList<>();

    /**
     * Creates and displays the GUI window
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws IOException if the dictionary or board file cannot be read
     */
    @Override
    public void start(Stage stage) throws IOException{
        String dictionaryFile = getParameters().getRaw().get(0);
        String boardFile = getParameters().getRaw().get(1);
        Dictionary dictionary = new Dictionary(dictionaryFile);
        Scanner boardScanner = new Scanner(new File(boardFile));
        Board board = Board.readBoard(boardScanner);
        boardScanner.close();
        game = new ScrabbleGame(board, dictionary);
        pendingBoard = new Board(board);
        root = new BorderPane();
        refreshDisplay();
        Scene scene = new Scene(root);
        stage.setTitle("Scrabble");
        stage.setScene(scene);
        stage.show();
    }
    private void refreshDisplay(){
        GridPane boardDisplay = createBoardDisplay();
        HBox trayDisplay = createTrayDisplay();
        VBox centerDisplay = new VBox(15, boardDisplay, trayDisplay);
        centerDisplay.setAlignment(Pos.TOP_CENTER);
        centerDisplay.setFillWidth(false);
        root.setCenter(centerDisplay);
        root.setRight(createInformationDisplay());
    }
    private GridPane createBoardDisplay(){
        GridPane grid = new GridPane();
        for (int row = 0; row < game.getBoard().getSize(); row++){
            for (int col = 0; col < game.getBoard().getSize(); col++){
                Square square = game.getBoard().getSquare(row, col);
                Button button = new Button();
                button.setMinSize(40,40);
                if(game.isGameOver()){
                    button.setDisable(true);
                }
                if (square.isOccupied()){
                    button.setText(String.valueOf(Character.toUpperCase(square.getTile().getLetter())));
                } else if (square.getWordMultiplier() == 3){
                    button.setText("3W");
                } else if (square.getWordMultiplier() == 2){
                    button.setText("2W");
                } else if (square.getLetterMultiplier() == 3){
                    button.setText("3L");
                } else if (square.getLetterMultiplier() == 2){
                    button.setText("2L");
                }
                int currentRow = row;
                int currentCol = col;
                button.setOnAction(event -> {
                    if (exchangeMode) {
                        return;
                    }
                    if (selectedTrayIndex == -1){
                        return;
                    }
                    Square pendingSquare = pendingBoard.getSquare(currentRow, currentCol);
                    if (pendingSquare.isOccupied()){
                        return;
                    }
                    char trayLetter = game.getHumanPlayer().getTray().getLetters().get(selectedTrayIndex);
                    Tile tile;
                    char boardLetter;
                    if (trayLetter == '*'){
                        if (selectedBlankLetter == '\0'){
                            return;
                        }
                        boardLetter = selectedBlankLetter;
                        tile = new Tile(boardLetter, 0 , true);
                    } else {
                        boardLetter = trayLetter;
                        tile = new Tile(boardLetter, Tile.getLetterValue(boardLetter), false);
                    }
                    pendingSquare.placeTile(tile);
                    button.setText(String.valueOf(Character.toUpperCase(boardLetter)));
                    selectedTrayButton.setDisable(true);
                    selectedTrayIndex = -1;
                    selectedTrayButton = null;
                    selectedBlankLetter = '\0';
                });
                grid.add(button, col, row);
            }
        }
        return grid;
    }
    private HBox createTrayDisplay(){
        HBox trayBox = new HBox(5);
        trayBox.setAlignment(Pos.CENTER);
        for (int i = 0; i < game.getHumanPlayer().getTray().getLetters().size(); i++){
            char letter = game.getHumanPlayer().getTray().getLetters().get(i);
            Button tileButton = new Button(String.valueOf(Character.toUpperCase(letter)));
            tileButton.setMinSize(40, 40);
            if (game.isGameOver()){
                tileButton.setDisable(true);
            }
            int index = i;
            tileButton.setOnAction(event -> {
                if (exchangeMode){
                    if (exchangeIndices.contains(index)){
                        exchangeIndices.remove(Integer.valueOf(index));
                        tileButton.setText(String.valueOf(Character.toUpperCase(letter)));
                    } else {
                        exchangeIndices.add(index);
                        tileButton.setText("[" + Character.toUpperCase(letter) + "]");
                    }
                    return;
                }
                selectedTrayIndex = index;
                selectedTrayButton = tileButton;
                if (letter != '*'){
                    selectedBlankLetter = '\0';
            }
        });
        trayBox.getChildren().add(tileButton);
        }
        return trayBox;
    }
    private VBox createInformationDisplay(){
        Label humanScore = new Label("Your Score: " + game.getHumanPlayer().getScore());
        Label computerScore = new Label("Computer Score: " + game.getComputerPlayer().getScore());
        Label tilesRemaining = new Label("Tiles remaining: " + game.getBag().size());
        Label blankLabel = new Label("Blank letter: ");
        GridPane blankLetters = createBlankLetterButtons();
        Label message = new Label();
        message.setWrapText(true);
        message.setMaxWidth(200);
        Button submitButton = new Button("Submit move");
        submitButton.setOnAction(event -> {
            int score = game.humanTurn(pendingBoard);
            if (score == -1){
                message.setText("Illegal move");
                return;
            }
            if (!game.isGameOver()){
                game.computerTurn();
            }
            pendingBoard = new Board(game.getBoard());
            selectedTrayIndex = -1;
            selectedTrayButton = null;
            selectedBlankLetter = '\0';
            refreshDisplay();
        });
        Button resetButton = new Button("Reset move");
        resetButton.setOnAction(event -> {
            pendingBoard = new Board(game.getBoard());
            selectedTrayIndex = -1;
            selectedTrayButton = null;
            selectedBlankLetter = '\0';
            refreshDisplay();
        });
        Button passButton = new Button("Pass move");
        passButton.setOnAction(event -> {
            pendingBoard = new Board(game.getBoard());
            selectedTrayIndex = -1;
            selectedTrayButton = null;
            selectedBlankLetter = '\0';
            game.passHumanTurn();
            if (!game.isGameOver()){
                game.computerTurn();
            }
            pendingBoard = new Board(game.getBoard());
            refreshDisplay();
        });
        Button exchangeButton = new Button("Exchange tiles");
        exchangeButton.setOnAction(event -> {
            if (!exchangeMode){
                exchangeMode = true;
                exchangeIndices.clear();
                selectedTrayIndex = -1;
                selectedTrayButton = null;
                selectedBlankLetter = '\0';
                message.setText("Select tiles, then click exchange tiles again");
                return;
            }
            if (exchangeIndices.isEmpty()){
                message.setText("Select atleast one tile");
                return;
            }
            ArrayList<Character> letters = new ArrayList<>();
            for (int index : exchangeIndices){
                letters.add(game.getHumanPlayer().getTray().getLetters().get(index));
            }
            boolean exchanged = game.exchangeTiles(letters);
            if (!exchanged) {
                message.setText("Tiles could not be exchanged");
                return;
            }
            exchangeMode = false;
            exchangeIndices.clear();
            pendingBoard = new Board(game.getBoard());
            if (!game.isGameOver()){
                game.computerTurn();
            }
            pendingBoard = new Board(game.getBoard());
            refreshDisplay();
        });
        Label gameStatus = new Label();
        if (game.isGameOver()){
            gameStatus.setText(game.getGameOverMessage());
        }
        VBox information = new VBox(10, humanScore, computerScore, tilesRemaining, blankLabel,
                blankLetters, submitButton, resetButton, passButton, exchangeButton, message, gameStatus);
        information.setPrefWidth(220);
        if(game.isGameOver()){
            submitButton.setDisable(true);
            resetButton.setDisable(true);
            passButton.setDisable(true);
            exchangeButton.setDisable(true);
        }
        return information;
    }
    private GridPane createBlankLetterButtons(){
        GridPane blankGrid = new GridPane();
        int row = 0;
        int col = 0;
        for (char letter = 'a'; letter <= 'z'; letter++){
            Button letterButton = new Button(String.valueOf(Character.toUpperCase(letter)));
            letterButton.setMinSize(35, 35);
            if(game.isGameOver()){
                letterButton.setDisable(true);
            }
            char blankLetter = letter;
            letterButton.setOnAction(event -> {
                if (selectedTrayIndex == -1){
                    return;
                }
                char selectedLetter = game.getHumanPlayer().getTray().getLetters().get(selectedTrayIndex);
                if (selectedLetter != '*'){
                    return;
                }
                selectedBlankLetter = blankLetter;
            });
            blankGrid.add(letterButton, col, row);
            col++;
            if (col == 5){
                col = 0;
                row++;
            }
        }
        return blankGrid;
    }

    /**
     * Launches the Scrabble JavaFX application
     * @param args command-line arguments containing the dictionary and board files
     */
    public static void main(String[] args) {
        launch(args);
    }
}
