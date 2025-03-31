import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TrafficGame extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private int playerX = 250;
    private boolean isRedLight = false;
    private int score = 0;

    public TrafficGame() {
        timer = new Timer(100, this); // Updates every 100ms
        timer.start();
        addKeyListener(this);
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw traffic light
        g.setColor(isRedLight ? Color.RED : Color.BLACK);
        g.fillOval(280, 50, 40, 40);
        
        // Draw player car
        g.setColor(Color.BLUE);
        g.fillRect(playerX, 400, 50, 30);
        
        // Draw score
        g.drawString("Score: " + score, 20, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Toggle traffic light every 3 seconds
        if (timer.getDelay() % 30 == 0) {
            isRedLight = !isRedLight;
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) playerX -= 10;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) playerX += 10;
        
        // Check if player crossed during red light
        if (isRedLight && playerX > 300) {
            JOptionPane.showMessageDialog(this, "Game Over! Ran a red light!");
            System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No action needed for keyReleased in this game
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No action needed for keyTyped in this game
    }
    

    public static void main(String[] args) {
        JFrame frame = new JFrame("Traffic Light Game");
        frame.add(new TrafficGame());
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}