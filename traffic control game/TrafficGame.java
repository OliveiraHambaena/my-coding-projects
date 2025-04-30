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

    private ArrayList<Rectangle> obstacles; // List of obstacles
    private Random random;

    private JButton startButton, restartButton;

    private int obstacleSpeed = 5; // Initial speed of obstacles
    private int spawnChance = 100; // Initial spawn chance (1 in 100)

    // Add a variable to track lives
    private int lives = 3; // Player starts with 3 lives

    // Add a variable to track full-screen mode
    private boolean isFullScreen = false;
    private JFrame frame; // Reference to the main frame

    public TrafficGame(JFrame frame) {
        this.frame = frame; // Store the frame reference
        setLayout(null); // For absolute positioning

        // Start Button
        startButton = new JButton("START");
        startButton.setBounds(250, 200, 100, 40);
        startButton.addActionListener(e -> startGame());
        add(startButton);

        // Restart Button
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
        startButton.setVisible(false);
        restartButton.setVisible(false);

        // Stop existing timers if any
        if (gameTimer != null) gameTimer.stop();
        if (difficultyTimer != null) difficultyTimer.stop();

        // Game timer (~60fps)
        gameTimer = new Timer(16, this);
        gameTimer.start();

        // Difficulty timer (adjusts every 5 seconds)
        difficultyTimer = new Timer(5000, e -> increaseDifficulty());
        difficultyTimer.start();

        System.out.println("Game timer started."); // Debugging
        requestFocusInWindow(); // Crucial for key listeners
    }

    private void restartGame() {
        stopGame();
        startGame();
    }

    private void stopGame() {
        isGameRunning = false;
        if (gameTimer != null) gameTimer.stop();
        if (difficultyTimer != null) difficultyTimer.stop();
        restartButton.setVisible(true);
    }

    private void increaseDifficulty() {
        // Increase obstacle speed
        obstacleSpeed++;
        // Decrease spawn chance (minimum value is 10)
        spawnChance = Math.max(10, spawnChance - 10);

        System.out.println("Difficulty increased: obstacleSpeed=" + obstacleSpeed + ", spawnChance=1 in " + spawnChance);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Get the current panel dimensions
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Draw road
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, panelWidth, panelHeight);

        if (isGameRunning) {
            // Draw player car
            g.setColor(Color.BLUE);
            g.fillRect(playerX, playerY, 50, 30);

            // Draw obstacles
            g.setColor(Color.RED);
            for (Rectangle obstacle : obstacles) {
                g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
            }

            // Draw score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, 20, 30);

            // Draw lives
            g.drawString("Lives: " + lives, 20, 60);
        }

        // Game over screen
        if (!isGameRunning && score > 0) {
            g.setColor(new Color(255, 255, 255, 200));
            g.fillRect(0, 0, panelWidth, panelHeight);

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("GAME OVER", panelWidth / 2 - 100, panelHeight / 2 - 50);
            g.drawString("Final Score: " + score, panelWidth / 2 - 100, panelHeight / 2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameRunning) return;

        // Get the current panel dimensions
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Update player position based on velocity
        playerX += playerVelocityX;

        // Prevent the player from moving out of bounds
        if (playerX < 0) playerX = 0;
        if (playerX > panelWidth - 50) playerX = panelWidth - 50;

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
            int obstacleX = random.nextInt(panelWidth - 50);
            obstacles.add(new Rectangle(obstacleX, 0, 50, 30));
        }

        repaint();
    }

    // Add a method to toggle full-screen mode
    private void toggleFullScreen() {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (isFullScreen) {
            // Switch to windowed mode
            frame.dispose();
            frame.setUndecorated(false);
            frame.setVisible(true);
            frame.setSize(600, 500); // Restore original size
            isFullScreen = false;
        } else {
            // Switch to full-screen mode
            frame.dispose();
            frame.setUndecorated(true);
            device.setFullScreenWindow(frame);
            isFullScreen = true;
        }
    }

    // Update keyPressed to handle F11 for toggling full-screen
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F11) {
            toggleFullScreen();
        }

        if (!isGameRunning) return;

        // Set velocity based on key press
        if (e.getKeyCode() == KeyEvent.VK_LEFT) playerVelocityX = -10;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) playerVelocityX = 10;
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

    @Override public void keyTyped(KeyEvent e) {}
}