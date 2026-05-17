package com.zetcode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

// Modified by me:
// JUnit 5 tests for the pure game logic model, without Swing or image loading.
class SnakeGameModelTest {

    // Modified by me:
    // Eating an apple should add 10 points.
    @Test
    void scoreIncreasesAfterSnakeEatsApple() {
        SnakeGameModel model = new SnakeGameModel();

        model.setObstacles(Collections.emptyList());
        model.setApplePosition(60, 50);
        model.update();

        assertEquals(10, model.getScore());
    }

    // Modified by me:
    // High score should follow the highest current score reached.
    @Test
    void highScoreUpdatesWhenCurrentScoreBecomesHigher() {
        SnakeGameModel model = new SnakeGameModel();

        model.setObstacles(Collections.emptyList());
        model.setApplePosition(60, 50);
        model.update();

        assertEquals(10, model.getHighScore());
    }

    // Modified by me:
    // Restart resets current score but preserves high score during the program run.
    @Test
    void restartResetsCurrentScoreButKeepsHighScore() {
        SnakeGameModel model = new SnakeGameModel();

        model.setObstacles(Collections.emptyList());
        model.setApplePosition(60, 50);
        model.update();
        model.restartGame();

        assertEquals(0, model.getScore());
        assertEquals(10, model.getHighScore());
    }

    // Modified by me:
    // Pause and resume states should be controllable from the model.
    @Test
    void gameCanBePausedAndResumed() {
        SnakeGameModel model = new SnakeGameModel();

        model.pauseGame();
        assertTrue(model.isPaused());

        model.resumeGame();
        assertFalse(model.isPaused());
    }

    // Modified by me:
    // A paused game should not advance the snake position.
    @Test
    void snakeDoesNotMoveWhilePaused() {
        SnakeGameModel model = new SnakeGameModel();
        int startX = model.getSnakeX(0);
        int startY = model.getSnakeY(0);

        model.pauseGame();
        model.update();

        assertEquals(startX, model.getSnakeX(0));
        assertEquals(startY, model.getSnakeY(0));
    }

    // Modified by me:
    // Hitting an obstacle should end the game.
    @Test
    void gameEndsWhenSnakeHitsObstacle() {
        SnakeGameModel model = new SnakeGameModel();

        model.setObstacles(List.of(new SnakeGameModel.Position(60, 50)));
        model.setApplePosition(100, 100);
        model.update();

        assertFalse(model.isInGame());
    }

    // Modified by me:
    // Moving beyond the board boundary should end the game.
    @Test
    void gameEndsWhenSnakeHitsWall() {
        SnakeGameModel model = new SnakeGameModel();

        model.setSnakePosition(List.of(
                new SnakeGameModel.Position(290, 50),
                new SnakeGameModel.Position(280, 50),
                new SnakeGameModel.Position(270, 50)));
        model.setObstacles(Collections.emptyList());
        model.setApplePosition(100, 100);
        model.update();

        assertFalse(model.isInGame());
    }

    // Modified by me:
    // Repeated apple placement should never choose snake or obstacle cells.
    @Test
    void appleDoesNotSpawnOnSnakeBodyOrObstacles() {
        SnakeGameModel model = new SnakeGameModel();

        model.setSnakePosition(List.of(
                new SnakeGameModel.Position(50, 50),
                new SnakeGameModel.Position(40, 50),
                new SnakeGameModel.Position(30, 50)));
        model.setObstacles(List.of(
                new SnakeGameModel.Position(70, 70),
                new SnakeGameModel.Position(80, 80)));

        for (int i = 0; i < 100; i++) {
            model.locateApple();

            assertFalse(model.isOnSnake(model.getAppleX(), model.getAppleY()));
            assertFalse(model.isOnObstacle(model.getAppleX(), model.getAppleY()));
        }
    }

    // Modified by me:
    // The model should create exactly three obstacles, and not on the initial snake.
    @Test
    void gameStartsWithThreeObstaclesAwayFromInitialSnake() {
        SnakeGameModel model = new SnakeGameModel();

        assertEquals(3, model.getObstacles().size());

        for (SnakeGameModel.Position obstacle : model.getObstacles()) {
            assertFalse(model.isOnSnake(obstacle.getX(), obstacle.getY()));
        }
    }
}
