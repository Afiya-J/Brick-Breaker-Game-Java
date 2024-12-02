import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;

public class AdvancedBrickBreaker extends JPanel implements ActionListener {
    private Timer timer;
    private int ballX = 200, ballY = 200, ballDX = 2, ballDY = 3, ballSize = 20;
    private int paddleX = 300, paddleY = 550, paddleWidth = 120, paddleHeight = 10;
    private boolean[][] bricks;
    private int brickRows = 5, brickCols = 7, brickWidth = 100, brickHeight = 30, brickPadding = 10;
    private int lives = 3, score = 0, totalBricks;
    private boolean gameRunning = true;

    public AdvancedBrickBreaker() {
        bricks = new boolean[brickRows][brickCols];
        totalBricks = brickRows * brickCols;

        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickCols; j++) {
                bricks[i][j] = true;
            }
        }

        timer = new Timer(10, this);
        timer.start();

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    paddleX -= 20;
                    if (paddleX < 0) paddleX = 0;
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    paddleX += 20;
                    if (paddleX > getWidth() - paddleWidth) paddleX = getWidth() - paddleWidth;
                }
            }
        });
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Ball
        g.setColor(Color.RED);
        g.fillOval(ballX, ballY, ballSize, ballSize);

        // Paddle
        g.setColor(Color.BLUE);
        g.fillRect(paddleX, paddleY, paddleWidth, paddleHeight);

        // Bricks
        g.setColor(Color.GREEN);
        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickCols; j++) {
                if (bricks[i][j]) {
                    int brickX = j * (brickWidth + brickPadding) + 50;
                    int brickY = i * (brickHeight + brickPadding) + 50;
                    g.fillRect(brickX, brickY, brickWidth, brickHeight);
                }
            }
        }

        // Score and Lives
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Lives: " + lives, getWidth() - 80, 20);

        // Game Over Message
        if (!gameRunning) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.setColor(Color.RED);
            g.drawString("GAME OVER", getWidth() / 2 - 100, getHeight() / 2);
            g.drawString("Score: " + score, getWidth() / 2 - 70, getHeight() / 2 + 40);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (!gameRunning) {
            timer.stop();
            return;
        }

        // Ball Movement
        ballX += ballDX;
        ballY += ballDY;

        // Ball-Wall Collision
        if (ballX < 0 || ballX > getWidth() - ballSize) {
            ballDX = -ballDX;
        }
        if (ballY < 0) {
            ballDY = -ballDY;
        }

        // Ball-Paddle Collision
        if (new Rectangle2D.Double(ballX, ballY, ballSize, ballSize)
                .intersects(paddleX, paddleY, paddleWidth, paddleHeight)) {
            ballDY = -ballDY;
        }

        // Ball-Brick Collision
        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickCols; j++) {
                if (bricks[i][j]) {
                    int brickX = j * (brickWidth + brickPadding) + 50;
                    int brickY = i * (brickHeight + brickPadding) + 50;
                    Rectangle2D brickRect = new Rectangle2D.Double(brickX, brickY, brickWidth, brickHeight);

                    if (brickRect.intersects(ballX, ballY, ballSize, ballSize)) {
                        bricks[i][j] = false;
                        ballDY = -ballDY;
                        score += 10;
                        totalBricks--;
                        if (totalBricks == 0) {
                            gameRunning = false;
                        }
                        break;
                    }
                }
            }
        }

        // Ball Misses Paddle
        if (ballY > getHeight()) {
            lives--;
            if (lives == 0) {
                gameRunning = false;
            } else {
                resetBallAndPaddle();
            }
        }

        repaint();
    }

    private void resetBallAndPaddle() {
        ballX = getWidth() / 2;
        ballY = getHeight() / 2;
        ballDX = 2 + (int) (Math.random() * 3);
        ballDY = 3 + (int) (Math.random() * 3);
        paddleX = (getWidth() - paddleWidth) / 2;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Advanced Brick Breaker");
        AdvancedBrickBreaker game = new AdvancedBrickBreaker();
        frame.add(game);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
