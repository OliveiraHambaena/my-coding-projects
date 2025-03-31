import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class TrafficGame extends JPanel implements ActionListener, KeyListener {
    private Timer gameTimer;
    private int playerX = 250;
    private int playerY = 400;
    private int score = 0;
    private boolean isGameRunning = false;
    private final int ROAD_WIDTH = 600;
    private final int ROAD_HEIGHT = 500;

    private ArrayList<Rectangle> obstacles; // List of obstacles
    private Random random;

    private JButton startButton, restartButton;

    private int obstacleSpeed = 5; // Initial speed of obstacles

    public TrafficGame() {
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
        obstacles.clear();
        obstacleSpeed = 5; // Reset obstacle speed
        startButton.setVisible(false);
        restartButton.setVisible(false);

        // Stop existing timer if any
        if (gameTimer != null) gameTimer.stop();

        gameTimer = new Timer(16, this); // ~60fps
        gameTimer.start();
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
        restartButton.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw road
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, ROAD_WIDTH, ROAD_HEIGHT);

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
        }

        // Game over screen
        if (!isGameRunning && score > 0) {
            g.setColor(new Color(255, 255, 255, 200));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("GAME OVER", 200, 150);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameRunning) return;

        // Move obstacles
        Iterator<Rectangle> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            Rectangle obstacle = iterator.next();
            obstacle.y += obstacleSpeed; // Move obstacle down based on speed

            // Remove obstacle if it goes off-screen
            if (obstacle.y > ROAD_HEIGHT) {
                iterator.remove();
                score += 10; // Increase score for avoiding an obstacle

                // Increase obstacle speed every 50 points
                if (score % 50 == 0) {
                    obstacleSpeed++;
                    System.out.println("Obstacle speed increased to: " + obstacleSpeed); // Debugging
                }
            }

            // Check for collision
            if (obstacle.intersects(new Rectangle(playerX, playerY, 50, 30))) {
                stopGame();
            }
        }

        // Spawn new obstacles
        if (random.nextInt(100) < 2) { // 2% chance to spawn a new obstacle
            int obstacleX = random.nextInt(ROAD_WIDTH - 50);
            obstacles.add(new Rectangle(obstacleX, 0, 50, 30));
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!isGameRunning) return;

        int speed = 10;
        if (e.getKeyCode() == KeyEvent.VK_LEFT && playerX > 0) playerX -= speed;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT && playerX < ROAD_WIDTH - 50) playerX += speed;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Traffic Avoidance Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setResizable(false);

        TrafficGame game = new TrafficGame();
        frame.add(game);

        frame.setVisible(true);
        game.requestFocusInWindow(); // Important for immediate key response
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}