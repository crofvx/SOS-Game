import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.*;

public class SOSGame extends JFrame implements ActionListener {
    private JButton[] buttons;
    private String currentPlayer = "S";
    private int movesCount = 0;
    private String gameMode = "Player v Player"; //default mode
    private String winMode = "General Mode"; //default win mode
    private int boardSize;

    public SOSGame() {
        setTitle("SOS Game!!!");
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);

        // Input for board size
        String sizeInput = JOptionPane.showInputDialog("Enter board size (enter one integer value >3):");
        try {
            boardSize = Integer.parseInt(sizeInput);
            if (boardSize < 3) {
                throw new NumberFormatException();
            }
            buttons = new JButton[boardSize * boardSize];

            // Create mode buttons
            JButton pvpButton = new JButton("Player v Player");
            pvpButton.addActionListener(e -> selectWinMode("Player v Player"));

            JButton pvcButton = new JButton("Player v Computer");
            pvcButton.addActionListener(e -> selectWinMode("Player v Computer"));

            JButton cvcButton = new JButton("Computer v Computer");
            cvcButton.addActionListener(e -> selectWinMode("Computer v Computer"));

            add(pvpButton);
            add(pvcButton);
            add(cvcButton);
            add(new JLabel("")); //spacing for empty space

            // create buttons for the board
            JPanel boardPanel = new JPanel();
            boardPanel.setLayout(new GridLayout(boardSize, boardSize));
            for (int i = 0; i < buttons.length; i++) {
                buttons[i] = new JButton("");
                buttons[i].setFont(new Font("Arial", Font.PLAIN, 40));
                buttons[i].addActionListener(this);
                boardPanel.add(buttons[i]);
            }

            add(boardPanel);
            setVisible(true);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a number greater than or equal to 3... Restarting");
            System.exit(0);
        }
    }

    private void selectWinMode(String mode) {
        gameMode = mode;
        String[] options = {"General Mode", "Simple Mode"};
        winMode = (String) JOptionPane.showInputDialog(this, "Select Game Mode:",
                "Game Mode", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        resetGame();
        JOptionPane.showMessageDialog(this, "Starting " + gameMode + " in " + winMode);

        if (gameMode.equals("Computer v Computer")) {
            computerVsComputerGame();
        }
    }

    // handle player action
    public void actionPerformed(ActionEvent e) {
        if (gameMode.equals("Computer v Computer")) {
            return;
        }

        JButton buttonClicked = (JButton) e.getSource();

        if (buttonClicked.getText().equals("")) {
            buttonClicked.setText(currentPlayer);
            buttonClicked.setForeground(currentPlayer.equals("S") ? Color.RED : Color.BLUE); // Set color
            movesCount++;

            //check for winner after placing the move
            if (checkWinner()) {
                JOptionPane.showMessageDialog(this, "Player " + currentPlayer + " wins with SOS!!!");
                resetGame();
            } else if (movesCount == boardSize * boardSize) {
                JOptionPane.showMessageDialog(this, "It's a draw...try again");
                resetGame();
            } else {
                if (gameMode.equals("Player v Computer") && currentPlayer.equals("S")) {
                    currentPlayer = "S"; //switch to computer
                    computerMove();
                } else {
                    currentPlayer = currentPlayer.equals("S") ? "O" : "S";
                }
            }
        }
    }

    // user v computer and computer v computer implementation
    private void computerMove() {
        Random rand = new Random();
        while (true) {
            int move = rand.nextInt(boardSize * boardSize);
            if (buttons[move].getText().equals("")) {
                buttons[move].setText(currentPlayer);
                buttons[move].setForeground(currentPlayer.equals("S") ? Color.RED : Color.BLUE);
                movesCount++;

                //check for winner after computer's move
                if (checkWinner()) {
                    JOptionPane.showMessageDialog(this, "Computer (" + currentPlayer + ") wins with SOS!");
                    resetGame();
                    return;
                } else if (movesCount == boardSize * boardSize) {
                    JOptionPane.showMessageDialog(this, "It's a draw!");
                    resetGame();
                    return;
                } else {
                    currentPlayer = currentPlayer.equals("S") ? "O" : "S"; //alternate turns
                }
                break;
            }
        }
    }

    // initiates comp vs comp
    private void computerVsComputerGame() {
        new Thread(() -> {
            try {
                while (movesCount < boardSize * boardSize) {
                    computerMove();
                    Thread.sleep(1000); //pause to simulate wait time
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private boolean checkWinner() {
        for (int i = 0; i < boardSize; i++) {
            // Check rows
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < boardSize; j++) {
                row.append(buttons[i * boardSize + j].getText());
            }
            if (row.toString().contains("SOS")) return true;

            //check columns
            StringBuilder col = new StringBuilder();
            for (int j = 0; j < boardSize; j++) {
                col.append(buttons[j * boardSize + i].getText());
            }
            if (col.toString().contains("SOS")) return true;
        }

        //check diagonals
        StringBuilder diag1 = new StringBuilder();
        StringBuilder diag2 = new StringBuilder();
        for (int i = 0; i < boardSize; i++) {
            diag1.append(buttons[i * boardSize + i].getText());
            diag2.append(buttons[(boardSize - 1 - i) * boardSize + i].getText());
        }
        if (diag1.toString().contains("SOS") || diag2.toString().contains("SOS")) return true;

        return false;
    }

    private void resetGame() {
        for (JButton button : buttons) {
            button.setText("");
            button.setForeground(Color.BLACK);
        }
        currentPlayer = "S"; //reset to starting player
        movesCount = 0;
    }

    public static void main(String[] args) {
        new SOSGame();
    }
}
