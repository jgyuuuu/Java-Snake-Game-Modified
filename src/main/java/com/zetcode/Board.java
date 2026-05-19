package com.zetcode;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    private final int DELAY = 140;

    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;

    // Modified by me:
    // Board now delegates testable game rules and state to SnakeGameModel.
    private SnakeGameModel model;

    public Board() {

        initBoard();
    }

    private void initBoard() {

        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(SnakeGameModel.B_WIDTH, SnakeGameModel.B_HEIGHT));
        loadImages();

        // Modified by me:
        // Create the pure logic model before starting the Swing timer.
        model = new SnakeGameModel();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void loadImages() {

        // Modified by me:
        // Load images from Maven classpath resources.
        ImageIcon iid = new ImageIcon(getClass().getResource("/resources/dot.png"));
        ball = iid.getImage();

        ImageIcon iia = new ImageIcon(getClass().getResource("/resources/apple.png"));
        apple = iia.getImage();

        ImageIcon iih = new ImageIcon(getClass().getResource("/resources/head.png"));
        head = iih.getImage();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        if (model.isInGame()) {

            drawScores(g);
            g.drawImage(apple, model.getAppleX(), model.getAppleY(), this);
            drawObstacles(g);
            drawSnake(g);
            drawControlGuide(g);

            if (model.isPaused()) {
                drawPauseMenu(g);
            }

            Toolkit.getDefaultToolkit().sync();

        } else {

            gameOver(g);
        }
    }

    // Modified by me:
    // Current score is visible during gameplay.
    private void drawScores(Graphics g) {
        Font scoreFont = new Font("Helvetica", Font.BOLD, 12);

        g.setColor(Color.white);
        g.setFont(scoreFont);
        g.drawString("Score: " + model.getScore(), 8, 16);
    }

    // Modified by me:
    // Obstacles are visible blocks on the board.
    private void drawObstacles(Graphics g) {
        g.setColor(Color.gray);

        for (SnakeGameModel.Position obstacle : model.getObstacles()) {
            g.fillRect(obstacle.getX(), obstacle.getY(), SnakeGameModel.DOT_SIZE, SnakeGameModel.DOT_SIZE);
        }
    }

    // Modified by me:
    // Snake drawing now reads coordinates from the model.
    private void drawSnake(Graphics g) {
        for (int z = 0; z < model.getDots(); z++) {
            if (z == 0) {
                g.drawImage(head, model.getSnakeX(z), model.getSnakeY(z), this);
            } else {
                g.drawImage(ball, model.getSnakeX(z), model.getSnakeY(z), this);
            }
        }
    }

    // Modified by me:
    // Draw on-screen keyboard instructions near the bottom-left of the board.
    private void drawControlGuide(Graphics g) {
        Font guideFont = new Font("Helvetica", Font.PLAIN, 10);
        String[] lines = {"P: Pause", "C: Continue", "R: Restart", "E / Esc: Exit"};
        int x = 8;
        int y = SnakeGameModel.B_HEIGHT - 50;

        g.setColor(new Color(255, 255, 255, 190));
        g.setFont(guideFont);

        for (String line : lines) {
            g.drawString(line, x, y);
            y += 12;
        }
    }

    // Modified by me:
    // Visible pause menu required by the enhanced game assignment.
    private void drawPauseMenu(Graphics g) {
        Font titleFont = new Font("Helvetica", Font.BOLD, 18);
        Font itemFont = new Font("Helvetica", Font.BOLD, 14);
        String[] lines = {"PAUSED", "C - Continue", "R - Restart", "E - Exit Game"};
        int y = 120;

        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(50, 85, 200, 120);

        for (int i = 0; i < lines.length; i++) {
            Font font = i == 0 ? titleFont : itemFont;
            FontMetrics metrics = getFontMetrics(font);

            g.setColor(Color.white);
            g.setFont(font);
            g.drawString(lines[i], (SnakeGameModel.B_WIDTH - metrics.stringWidth(lines[i])) / 2, y);
            y += 24;
        }
    }

    private void gameOver(Graphics g) {

        String msg = "Game Over";
        String scoreMsg = "Score: " + model.getScore();
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (SnakeGameModel.B_WIDTH - metr.stringWidth(msg)) / 2, SnakeGameModel.B_HEIGHT / 2);
        g.drawString(scoreMsg, (SnakeGameModel.B_WIDTH - metr.stringWidth(scoreMsg)) / 2,
                SnakeGameModel.B_HEIGHT / 2 + 24);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Modified by me:
        // Timer events update only the model; rendering reads model state afterward.
        model.update();
        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            // Modified by me:
            // Pause menu controls and restart/exit shortcuts.
            if (key == KeyEvent.VK_ESCAPE || key == KeyEvent.VK_E) {
                System.exit(0);
            }

            if (key == KeyEvent.VK_R) {
                model.restartGame();
                repaint();
                return;
            }

            if (key == KeyEvent.VK_P) {
                model.togglePause();
                repaint();
                return;
            }

            if (key == KeyEvent.VK_C && model.isPaused()) {
                model.resumeGame();
                repaint();
                return;
            }

            if (model.isPaused()) {
                return;
            }

            // Modified by me:
            // Direction input supports both arrow keys and WASD keys.
            if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
                model.setDirection(SnakeGameModel.Direction.LEFT);
            }

            if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
                model.setDirection(SnakeGameModel.Direction.RIGHT);
            }

            if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
                model.setDirection(SnakeGameModel.Direction.UP);
            }

            if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
                model.setDirection(SnakeGameModel.Direction.DOWN);
            }
        }
    }
}
