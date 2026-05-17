package com.zetcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// Modified by me:
// Pure game logic model for the enhanced Snake game.
public class SnakeGameModel {

    // Modified by me:
    // Shared board constants used by both the model and Swing board.
    public static final int B_WIDTH = 300;
    public static final int B_HEIGHT = 300;
    public static final int DOT_SIZE = 10;
    public static final int ALL_DOTS = 900;
    public static final int RAND_POS = 29;
    // Modified by me:
    // Reduced obstacle count for the updated assignment requirement.
    public static final int OBSTACLE_COUNT = 3;
    public static final int SCORE_INCREMENT = 10;

    // Modified by me:
    // Direction values keep keyboard handling separate from movement rules.
    public enum Direction {
        LEFT, RIGHT, UP, DOWN
    }

    // Modified by me:
    // Simple immutable grid position for apples, snake segments, and obstacles.
    public static class Position {
        private final int x;
        private final int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    // Modified by me:
    // State fields moved out of Board.java so they can be tested without Swing.
    private final int[] x = new int[ALL_DOTS];
    private final int[] y = new int[ALL_DOTS];
    private final Random random;
    private final List<Position> obstacles = new ArrayList<>();

    private int dots;
    private int appleX;
    private int appleY;
    private int score;
    private int highScore;
    private boolean paused;
    private boolean inGame;
    private Direction direction;

    // Modified by me:
    // Default constructor used by the GUI.
    public SnakeGameModel() {
        this(new Random());
    }

    // Modified by me:
    // Constructor with injectable Random makes future deterministic tests possible.
    public SnakeGameModel(Random random) {
        this.random = random;
        restartGame();
    }

    // Modified by me:
    // Restart resets the current run while preserving the program-run high score.
    public void restartGame() {
        dots = 3;
        score = 0;
        paused = false;
        inGame = true;
        direction = Direction.RIGHT;

        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * DOT_SIZE;
            y[z] = 50;
        }

        locateObstacles();
        locateApple();
    }

    // Modified by me:
    // One game tick. Paused and game-over states intentionally do nothing.
    public void update() {
        if (!inGame || paused) {
            return;
        }

        move();
        checkCollision();

        if (inGame) {
            checkApple();
        }
    }

    // Modified by me:
    // Direction changes reject immediate reversal into the snake body.
    public void setDirection(Direction newDirection) {
        if (newDirection == Direction.LEFT && direction != Direction.RIGHT) {
            direction = Direction.LEFT;
        } else if (newDirection == Direction.RIGHT && direction != Direction.LEFT) {
            direction = Direction.RIGHT;
        } else if (newDirection == Direction.UP && direction != Direction.DOWN) {
            direction = Direction.UP;
        } else if (newDirection == Direction.DOWN && direction != Direction.UP) {
            direction = Direction.DOWN;
        }
    }

    // Modified by me:
    // Pause and resume controls used by the visible pause menu.
    public void pauseGame() {
        if (inGame) {
            paused = true;
        }
    }

    public void resumeGame() {
        paused = false;
    }

    public void togglePause() {
        if (!inGame) {
            return;
        }

        paused = !paused;
    }

    // Modified by me:
    // Testable movement logic.
    private void move() {
        for (int z = dots; z > 0; z--) {
            x[z] = x[z - 1];
            y[z] = y[z - 1];
        }

        if (direction == Direction.LEFT) {
            x[0] -= DOT_SIZE;
        } else if (direction == Direction.RIGHT) {
            x[0] += DOT_SIZE;
        } else if (direction == Direction.UP) {
            y[0] -= DOT_SIZE;
        } else if (direction == Direction.DOWN) {
            y[0] += DOT_SIZE;
        }
    }

    // Modified by me:
    // Score and high score update when the head reaches the apple.
    private void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            dots++;
            score += SCORE_INCREMENT;
            updateHighScore();
            locateApple();
        }
    }

    // Modified by me:
    // Wall, self, and obstacle collision checks.
    private void checkCollision() {
        for (int z = dots; z > 0; z--) {
            if (z > 4 && x[0] == x[z] && y[0] == y[z]) {
                inGame = false;
            }
        }

        if (y[0] >= B_HEIGHT || y[0] < 0 || x[0] >= B_WIDTH || x[0] < 0) {
            inGame = false;
        }

        for (Position obstacle : obstacles) {
            if (x[0] == obstacle.getX() && y[0] == obstacle.getY()) {
                inGame = false;
            }
        }
    }

    // Modified by me:
    // Apples are generated away from both the snake and all obstacles.
    public void locateApple() {
        do {
            int r = random.nextInt(RAND_POS);
            appleX = r * DOT_SIZE;

            r = random.nextInt(RAND_POS);
            appleY = r * DOT_SIZE;
        } while (isOnSnake(appleX, appleY) || isOnObstacle(appleX, appleY));
    }

    // Modified by me:
    // Obstacles are generated away from the initial snake body.
    private void locateObstacles() {
        obstacles.clear();

        while (obstacles.size() < OBSTACLE_COUNT) {
            int obstacleX = random.nextInt(RAND_POS) * DOT_SIZE;
            int obstacleY = random.nextInt(RAND_POS) * DOT_SIZE;

            if (!isOnSnake(obstacleX, obstacleY) && !isOnObstacle(obstacleX, obstacleY)) {
                obstacles.add(new Position(obstacleX, obstacleY));
            }
        }
    }

    // Modified by me:
    // Helpers used by spawning rules and tests.
    public boolean isOnSnake(int testX, int testY) {
        for (int z = 0; z < dots; z++) {
            if (x[z] == testX && y[z] == testY) {
                return true;
            }
        }

        return false;
    }

    public boolean isOnObstacle(int testX, int testY) {
        for (Position obstacle : obstacles) {
            if (obstacle.getX() == testX && obstacle.getY() == testY) {
                return true;
            }
        }

        return false;
    }

    private void updateHighScore() {
        if (score > highScore) {
            highScore = score;
        }
    }

    // Modified by me:
    // Accessors used by Board.java and JUnit tests.
    public int getSnakeX(int index) {
        return x[index];
    }

    public int getSnakeY(int index) {
        return y[index];
    }

    public int getDots() {
        return dots;
    }

    public int getAppleX() {
        return appleX;
    }

    public int getAppleY() {
        return appleY;
    }

    public int getScore() {
        return score;
    }

    public int getHighScore() {
        return highScore;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isInGame() {
        return inGame;
    }

    public List<Position> getObstacles() {
        return Collections.unmodifiableList(obstacles);
    }

    // Modified by me:
    // Setup methods keep unit tests independent from random spawning and Swing.
    public void setApplePosition(int appleX, int appleY) {
        this.appleX = appleX;
        this.appleY = appleY;
    }

    public void setObstacles(List<Position> newObstacles) {
        obstacles.clear();
        obstacles.addAll(newObstacles);
    }

    public void setSnakePosition(List<Position> snake) {
        dots = snake.size();

        for (int z = 0; z < dots; z++) {
            x[z] = snake.get(z).getX();
            y[z] = snake.get(z).getY();
        }
    }
}
