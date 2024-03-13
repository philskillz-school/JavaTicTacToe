import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.util.concurrent.TimeUnit;

/*
TicTacToe.java
Authors Philskillz, Areesh
*/

public class TicTacToe extends Frame {
    private static class Move {
        int row, col;

        public Move(int x, int y) {
            this.row = x;
            this.col = y;
        }
    }

    private enum FieldType {
        unset,
        player_x,
        player_o,
        draw
    }

    final int size = 3;
    long max_fields = (long)size*size;
    long fields_placed;
    final FieldType[][] grid = new FieldType[size][size]; // 0: nichts, 1: X, 2: O
    final int buttonSize = 200;
    Button[][] gridButtons = new Button[size][size];
    Label playerTurnLabel;
    boolean playerTurn; // false: X, true: O
    FieldType gameWon;
    
    Label player1WinsLabel;
    int player1Wins;
    
    Label player2WinsLabel;
    int player2Wins;

    Label drawLabel;
    int draws;

//    Button beginnerButton;
    Button newGameButton;
    Button botButton;
    Button resetButton;
    boolean botEnabled;

    public TicTacToe() {
        super("-----  Tic Tac Toe -----");
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                dispose();
                System.exit(0);
            }
        });
        
        setLayout(null);
        
        playerTurn = false; // anfang ist spieler X
        gameWon = FieldType.unset;
        fields_placed = 0;
        player1Wins = 0;
        player2Wins = 0;
        draws = 0;
        botEnabled = false;

        for (int row=0; row<size; row++) { // reihe
            for (int col=0; col<size; col++) { // spalte
                grid[row][col] = FieldType.unset;

                Button b = createGridButton(row, col);
                add(b);
                gridButtons[row][col] = b;
            }
        }

        Font labelFont = new Font("arial", Font.PLAIN, 20);
        Font buttonFont = new Font("arial", Font.PLAIN, 14);

        playerTurnLabel = new Label(""); 
        playerTurnLabel.setBounds(650, 100, 200, 20);
        playerTurnLabel.setFont(labelFont.deriveFont(Font.BOLD));
        
        player1WinsLabel = new Label("");
        player1WinsLabel.setBounds(650, 150, 200, 20);
        player1WinsLabel.setFont(labelFont);
        
        player2WinsLabel = new Label("");
        player2WinsLabel.setBounds(650, 200, 200, 20);
        player2WinsLabel.setFont(labelFont);

        drawLabel = new Label("");
        drawLabel.setBounds(650, 250, 200, 20);
        drawLabel.setFont(labelFont);

        newGameButton = new Button("Neues Spiel");
        newGameButton.setBounds(650, 400, 200, 40);
        newGameButton.addActionListener(e -> this.restartGame());
        newGameButton.setFont(buttonFont);

        botButton = new Button("Gegen Bot spielen");
        botButton.setBounds(650, 450, 200, 40);
        botButton.addActionListener(e -> this.onBotButtonClick());
        botButton.setFont(buttonFont);

        resetButton = new Button("Zurücksetzen");
        resetButton.setBounds(650, 500, 200, 40);
        resetButton.addActionListener(e -> this.onResetButtonClick());
        resetButton.setFont(buttonFont);

        updateLabel();

        add(playerTurnLabel);
        add(player1WinsLabel);
        add(player2WinsLabel);
        add(drawLabel);
//        add(beginnerButton);
        add(newGameButton);
        add(botButton);
        add(resetButton);

    }

    private ArrayList<Move> getLegalMoves() {
        ArrayList<Move> moves = new ArrayList<>();

        for (int row=0; row<size; row++) {
            for (int col=0; col<size; col++) {
                if (grid[row][col] == FieldType.unset) {
                    moves.add(new Move(row, col));
                }
            }
        }

        return moves;
    }

    private void onBotButtonClick() {
        this.botEnabled = !this.botEnabled;
        this.botButton.setLabel("Gegen " + (this.botEnabled ? "Spieler" : "Bot") + " Spielen");
    }

    private void onResetButtonClick() {
        restartGame();
        this.player1Wins = 0;
        this.player2Wins = 0;
        this.draws = 0;
        updateLabel();
    }

    private Button createGridButton(int row, int col) {
        int index = row *3+ col;
        Button b = new Button(String.valueOf(index+1));
        b.setBounds(col *buttonSize, row *buttonSize, buttonSize, buttonSize);
        b.setFont(new Font("arial", Font.PLAIN, 40));

        b.addActionListener(e -> this.doMove(b, row, col));
        return b;
    }

    void doMove(Button b, int row, int col) {
        botButton.setEnabled(false);
        System.out.println("Row: " + row + " | Column: " + col);

        b.setEnabled(false);
        b.setLabel(playerTurn ? "O" : "X");
        grid[row][col] = (playerTurn ? FieldType.player_o : FieldType.player_x);
        playerTurn = !playerTurn;
        fields_placed++;

        FieldType playerWon = getPlayerWon(row, col);
        this.gameWon = playerWon;
        //noinspection EnhancedSwitchMigration
        switch (playerWon) {
            case draw:
                draws++;
                break;
            case player_o:
                disableGrid();
                player2Wins++;
                break;
            case player_x:
                disableGrid();
                player1Wins++;
                break;
        }

        updateLabel();

        if (playerWon == FieldType.unset && botEnabled && playerTurn) {
            disableGrid();
            generateBotMove();
            enableActiveGrid();
        }
    }

    void random_bot() {
        ArrayList<Move> legalMoves = getLegalMoves();
        long field = getRandomNumber(legalMoves.size());
        Move m = legalMoves.get((int) field);

        Button b = gridButtons[m.row][m.col];
        doMove(b, m.row, m.col);
    }

    void generateBotMove() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        random_bot();
    }

    void disableGrid() {
        for (int i=0; i<size; i++) { // row
            for (int j=0; j<size; j++) { // column
                gridButtons[i][j].setEnabled(false);
            }
        }
    }

    void enableActiveGrid() {
        for (int row=0; row<size; row++) { // row
            for (int col=0; col<size; col++) { // column
                FieldType f = grid[row][col];
                gridButtons[row][col].setEnabled(f == FieldType.unset);
            }
        }
    }

    void restartGame() {
        fields_placed = 0;
        playerTurn = false; // set player turn
        draws = 0;
        botButton.setEnabled(true);
        gameWon = FieldType.unset;

        updateLabel();

        for (int i=0; i<size; i++) { // row
            for (int j=0; j<size; j++) { // column
                grid[i][j] = FieldType.unset;
                int index = i*3+j;
                gridButtons[i][j].setEnabled(true);
                gridButtons[i][j].setLabel(String.valueOf(index+1));
            }
        }
    }
    
    void updateLabel() {
        //noinspection EnhancedSwitchMigration : doesn't work here
        switch (gameWon) {
            case unset:
                playerTurnLabel.setText("Spieler " + (playerTurn ? "O" : "X") + " ist dran");
                break;
            case draw:
                playerTurnLabel.setText("Unentschieden");
                break;
            case player_o:
                playerTurnLabel.setText("Spieler O hat gewonnen");
                break;
            case player_x:
                playerTurnLabel.setText("Spieler X hat gewonnen");
                break;
        }
        player1WinsLabel.setText("Spieler X: " + player1Wins + " Wins");
        player2WinsLabel.setText("Spieler O: " + player2Wins + " Wins");
        drawLabel.setText("Unentschieden: " + draws);
    }
    
    private FieldType getPlayerWon(int row, int col) {
        if (fields_placed < size*2-1) { // if you had 3x3 board, you would need at least 5 (3*2-1) fields for a player to win
            return FieldType.unset;
        }

        // you only need to check the row <row> and col <col> and diagonals from row,col
        FieldType firstCol = grid[row][0];
        boolean equal = true;
        if (firstCol != FieldType.unset) {
            for (int iCol = 1; iCol < size; iCol++) {
                if (grid[row][iCol] != firstCol) {
                    equal = false;
                    break;
                }
            }
            if (equal) return firstCol;
        }

        equal = true;
        FieldType firstRow = grid[0][col];
        if (firstRow != FieldType.unset) {
            for (int iRow = 1; iRow < size; iRow++) {
                // If any element in the col is different from the first row,
                // break out of the loop. if not broke out -> all elements same
                if (grid[iRow][col] != firstRow) {
                    equal = false;
                    break;
                }
            }
            if (equal) return firstRow;
        }

        equal = true;
        FieldType firstItem = grid[0][0]; // top left
        if (firstRow != FieldType.unset) {
            for (int diagonal=1; diagonal<size; diagonal++) {
                if (grid[diagonal][diagonal] != firstItem) {
                    equal = false;
                    break;
                }
            }
            if (equal) return firstItem;
        }

        equal = true;
        FieldType lastItem = grid[size-1][0]; // bottom left
        if (lastItem != FieldType.unset) {
            for (int diagonal = 1; diagonal < size; diagonal++) {
                if (grid[size - 1 - diagonal][diagonal] != lastItem) {
                    equal = false;
                    break;
                }
            }
            if (equal) return lastItem;
        }

        if (fields_placed >= max_fields) {
            return FieldType.draw;
        }

        return FieldType.unset;
    }

    public static long getRandomNumber(long n) {
        Random random = new Random();
        return (long) (random.nextDouble() * n);
    }
    
    public static void main(String[] sdfs){
        TicTacToe t = new TicTacToe();
        t.setSize(900, 900);
        t.setVisible(true);
    }
}