import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class TrafficGame extends JPanel implements ActionListener, KeyListener {
    private Timer gameTimer;
    private Timer difficultyTimer; // Timer to increase difficulty over time
    private int playerX = 250;
    private int playerY = 400;
    private int playerVelocityX = 0; // Horizontal velocity of the car
    private int score = 0;
    private boolean isGameRunning = false;

    private ArrayList<Rectangle> obstacles; // List of obstacle
    private Random random;

    private JButton startButton, restartButton;

    private int obstacleSpeed = 5; // Initial speed of obstacles
    private int spawnChance = 100; // Initial spawn chance (1 in 100)

    // Add a variable to track lives
    private int lives = 3; // 3 lives for players

    // Add a variable to track full-screen mode
    private boolean isFullScreen = false;
    private JFrame frame; // Reference to the main frame

    // Add a variable to track unstoppable mode
    private boolean isUnstoppable = false;
    private Timer unstoppableTimer; // Timer to deactivate unstoppable mode

    // Add a variable to store the high score
    private int highScore = 0;

    // Add a variable to track if the game is paused
    private boolean isGamePaused = false;

    public TrafficGame(JFrame frame) {
        this.frame = frame; // Store the frame reference
        setLayout(null); // For absolute positioning

        // Create the menu bar
        JMenuBar menuBar = new JMenuBar();

        JMenu gameMenu = new JMenu("MENU");

        // Add Restart menu item
        JMenuItem restartMenuItem = new JMenuItem("Restart");
        restartMenuItem.addActionListener(e -> {
            pauseGame(); // Pause the game when the menu is opened
            restartGame();
        });
        gameMenu.add(restartMenuItem);

        // add the view high score menu ite
        JMenuItem highScoreMenuItem = new JMenuItem("View High Score");
        highScoreMenuItem.addActionListener(e -> {
            pauseGame(); // Pause the game when the menu is opened
            JOptionPane.showMessageDialog(
                    frame,
                    "High Score: " + highScore,
                    "High Score",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        gameMenu.add(highScoreMenuItem);

        // Add Pause/Resume menu item
        JMenuItem pauseMenuItem = new JMenuItem("Pause/Resume");
        pauseMenuItem.addActionListener(e -> togglePauseGame());
        gameMenu.add(pauseMenuItem);

        // Add the Game menu to the menu bar
        menuBar.add(gameMenu);

        // Create the Help menu
        JMenu helpMenu = new JMenu("Help");

        // Add Instructions menu item
        JMenuItem instructionsMenuItem = new JMenuItem("Instructions");
        instructionsMenuItem.addActionListener(e -> {
            pauseGame(); // Pause the game when the menu is opened
            JOptionPane.showMessageDialog(
                    frame,
                    "Instructions:\n" +
                            "- Use LEFT and RIGHT arrow keys to move the car.\n" +
                            "- Avoid red obstacles.\n" +
                            "- Collect gold obstacles for unstoppable mode (15 seconds).\n" +
                            "- Press SPACE to pause/unpause the game.\n" +
                            "- Press ENTER to start or restart the game.\n" +
                            "- Press F11 to toggle full-screen mode.",
                    "How to Play",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        helpMenu.add(instructionsMenuItem);

        // Add the Help menu to the menu bar
        menuBar.add(helpMenu);

        // Set the menu bar to the frame
        frame.setJMenuBar(menuBar);

        // Initialize buttons and other components
        startButton = new JButton("START");
        startButton.setBounds(250, 200, 100, 40);
        startButton.addActionListener(e -> startGame());
        add(startButton);

        restartButton = new JButton("RESTART");
        restartButton.setBounds(250, 200, 100, 40);
        restartButton.addActionListener(e -> restartGame());
        restartButton.setVisible(false);
        add(restartButton);

        setFocusable(true);
        addKeyListener(this);

        obstacles = new ArrayList<>();
        random = new Random();
    }

    private void startGame() {
        System.out.println("Game started!"); // Debugging
        isGameRunning = true;
        score = 0;
        playerX = 250;
        playerY = 400;
        playerVelocityX = 0; // Reset velocity
        obstacles.clear();
        obstacleSpeed = 5; // Reset obstacle speed
        spawnChance = 100; // Reset spawn chance
        lives = 3; // Reset lives
        isUnstoppable = false; // Reset unstoppable mode

        isGamePaused = false; // Reset paused state
        startButton.setVisible(false);
        restartButton.setVisible(false);

        // Stop existing timers if any
        if (gameTimer != null)
            gameTimer.stop();
        if (difficultyTimer != null)
            difficultyTimer.stop();

        // Game timer (~60fps)
        gameTimer = new Timer(16, this);
        gameTimer.start();

        // Difficulty timer (increment every 5 seconds)
        difficultyTimer = new Timer(5000, e -> increaseDifficulty());
        difficultyTimer.start();

        System.out.println("Game timer started."); // Debugging
        requestFocusInWindow(); // Crucial for key listeners
    }

    private void restartGame() {
        stopGame();
        startGame();
    }

    // Method to stop the game
    private void stopGame() {
        isGameRunning = false;
        if (gameTimer != null)
            gameTimer.stop();
        if (difficultyTimer != null)
            difficultyTimer.stop();
        restartButton.setVisible(true);
    }

    private void increaseDifficulty() {
        // obstacle speed Increase
        obstacleSpeed++;
        // Decrease spawn chance (minimum value is 10)
        spawnChance = Math.max(10, spawnChance - 10);

        System.out
                .println("Difficulty increased: obstacleSpeed=" + obstacleSpeed + ", spawnChance=1 in " + spawnChance);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Get the current panel dimensions
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Center the buttons
        int buttonWidth = 100;
        int buttonHeight = 40;
        int buttonX = (panelWidth - buttonWidth) / 2;
        int buttonY = (panelHeight - buttonHeight) / 2 + 50;

        startButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        restartButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);

        // Draw road
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, panelWidth, panelHeight);

        if (isGameRunning) {
            // Draw player car
            g.setColor(Color.BLUE);
            g.fillRect(playerX, playerY, 50, 30);

            // Draw obstacles
            for (Rectangle obstacle : obstacles) {
                if (obstacle.width == 40 && obstacle.height == 40) {
                    g.setColor(Color.YELLOW); // Gold obstacle
                } else {
                    g.setColor(Color.RED); // Regular obstacle
                }
                g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
            }

            // Draw player scores
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, 20, 30);

            // Draw lives
            g.drawString("Lives: " + lives, 20, 60);

            // Indicate unstoppable mode
            if (isUnstoppable) {
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.drawString("UNSTOPPABLE!", panelWidth / 2 - 80, 30);
            }
        }

        // Game over
        if (!isGameRunning && score > 0) {
            g.setColor(new Color(255, 255, 255, 200));
            g.fillRect(0, 0, panelWidth, panelHeight);

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("GAME OVER", panelWidth / 2 - 100, panelHeight / 2 - 100); // Move text slightly above the
                                                                                    // center
            g.drawString("Final Score: " + score, panelWidth / 2 - 100, panelHeight / 2 - 50); // Move score text
                                                                                               // slightly above the
                                                                                               // button

            // Update the high score
            if (score > highScore) {
                highScore = score;
            }

            // Add personal information
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 15));
            g.drawString("Made by Olly", panelWidth / 2 - 50, panelHeight - 60); // 60px from the bottom
            g.drawString("Contact: ollymeansoliveira@gmail.com", panelWidth / 2 - 120, panelHeight - 40); // 40px from
                                                                                                          // the bottom
            g.drawString("Phone: +264818696891", panelWidth / 2 - 80, panelHeight - 20); // 20px from the bottom
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameRunning || isGamePaused)
            return;

        // Get the current panel dimensions
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Dynamically position the player one-third from the bottom
        playerY = (int) (panelHeight * 2 / 3.0); // Move the player to one-third from the bottom

        // Update player position based on velocity
        playerX += playerVelocityX;

        // Prevent the player from moving out of bounds
        if (playerX < 0)
            playerX = 0;
        if (playerX > panelWidth - 50)
            playerX = panelWidth - 50;

        // Move obstacles
        Iterator<Rectangle> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            Rectangle obstacle = iterator.next();
            obstacle.y += obstacleSpeed; // Move obstacle down based on speed

            // Remove obstacle if it goes off-screen
            if (obstacle.y > panelHeight) {
                iterator.remove();
                score += 10; // Increase score for avoiding an obstacle
            }

            // Check for collision
            if (obstacle.intersects(new Rectangle(playerX, playerY, 50, 30))) {
                if (isUnstoppable) {
                    iterator.remove(); // Remove the obstacle without losing lives
                    continue;
                }

                // Check if it's a gold obstacle
                if (obstacle.width == 40 && obstacle.height == 40) { // Gold obstacle dimensions
                    activateUnstoppableMode();
                    iterator.remove();
                    continue;
                }

                iterator.remove(); // Remove the obstacle
                lives--; // Decrement lives
                System.out.println("Collision! Lives remaining: " + lives);

                if (lives <= 0) {
                    stopGame(); // End the game if no lives are left
                }
            }
        }

        // Spawn new obstacles
        if (random.nextInt(spawnChance) < 2) { // Dynamic spawn chance
            int obstacleX = random.nextInt(panelWidth - 40); // Adjust for smaller obstacle width
            obstacles.add(new Rectangle(obstacleX, 0, 40, 20)); // Regular obstacle size
        }

        // Spawn a rare gold obstacle
        if (random.nextInt(5000) == 0) { // 1 in 5000 chance
            int goldObstacleX = random.nextInt(panelWidth - 40);
            obstacles.add(new Rectangle(goldObstacleX, 0, 40, 40)); // Gold obstacle size
        }

        repaint();
    }

    private void activateUnstoppableMode() {
        isUnstoppable = true;
        System.out.println("Unstoppable mode activated!");

        // Stop any existing unstoppable timer
        if (unstoppableTimer != null) {
            unstoppableTimer.stop();
        }

        // Start a new timer for 15 seconds
        unstoppableTimer = new Timer(15000, e -> {
            isUnstoppable = false;
            System.out.println("Unstoppable mode deactivated!");
        });
        unstoppableTimer.setRepeats(false);
        unstoppableTimer.start();
    }

    // Add a method to toggle pause
    private void togglePauseGame() {
        if (!isGameRunning)
            return;

        isGamePaused = !isGamePaused;
        if (isGamePaused) {
            System.out.println("Game paused.");
            if (gameTimer != null)
                gameTimer.stop();
            if (difficultyTimer != null)
                difficultyTimer.stop();
        } else {
            System.out.println("Game resumed.");
            if (gameTimer != null)
                gameTimer.start();
            if (difficultyTimer != null)
                difficultyTimer.start();
        }
    }

    // Add a method to pause the game
    private void pauseGame() {
        if (!isGameRunning || isGamePaused)
            return;

        isGamePaused = true;
        System.out.println("Game paused.");
        if (gameTimer != null)
            gameTimer.stop();
        if (difficultyTimer != null)
            difficultyTimer.stop();
    }

    // Add a method to resume the game
    private void resumeGame() {
        if (!isGameRunning || !isGamePaused)
            return;

        isGamePaused = false;
        System.out.println("Game resumed.");
        if (gameTimer != null)
            gameTimer.start();
        if (difficultyTimer != null)
            difficultyTimer.start();
    }

    // Add a method to toggle full-screen mode
    private void toggleFullScreen() {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (isFullScreen) {
            // windowed mode
            frame.dispose();
            frame.setUndecorated(false);
            frame.setVisible(true);
            frame.setSize(600, 500); // Restore original size
            isFullScreen = false;
        } else {
            // full-screen mode
            frame.dispose();
            frame.setUndecorated(true);
            device.setFullScreenWindow(frame);
            isFullScreen = true;
        }
    }

    // Update keyPressed to handle Enter key for menu options
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F11) {
            toggleFullScreen();
        }

        // Handle Enter key for starting or restarting the game
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!isGameRunning) {
                if (startButton.isVisible()) {
                    startButton.doClick(); // Start the game
                } else if (restartButton.isVisible()) {
                    restartButton.doClick(); // Restart the game
                }
            }
        }

        // Handle Space key for pausing/unpausing the game
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (isGameRunning) {
                if (isGamePaused) {
                    // Resume the game immediately after closing the dialog
                    JOptionPane.showMessageDialog(
                            frame,
                            "Game Paused\nPress OK to resume.",
                            "Pause Menu",
                            JOptionPane.INFORMATION_MESSAGE);
                    togglePauseGame(); // Resume the game after the dialog is closed
                } else {
                    togglePauseGame(); // Pause the game
                }
            }
        }

        if (!isGameRunning || isGamePaused)
            return;

        // Dynamically adjust movement speed based on score
        int baseSpeed = 10; // Base speed
        int speedIncrement = score / 500; // Increase speed every 500 points
        int movementSpeed = baseSpeed + speedIncrement;

        // Set velocity based on key press
        if (e.getKeyCode() == KeyEvent.VK_LEFT)
            playerVelocityX = -movementSpeed;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            playerVelocityX = movementSpeed;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Stop movement when the key is released
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            playerVelocityX = 0;
        }
    }

    // Update the main method to set the frame to full-screen size
    public static void main(String[] args) {
        JFrame frame = new JFrame("Traffic Avoidance Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);

        // Get screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize.width, screenSize.height);

        TrafficGame game = new TrafficGame(frame);
        frame.add(game);

        frame.setVisible(true);
        game.requestFocusInWindow(); // Important for immediate key response
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}