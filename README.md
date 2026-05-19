# Enhanced Snake Game using Java

This project is based on the original **Snake Game using Java**. It was modified for a Java course assignment.

Java Snake game source code

https://zetcode.com/javagames/snake/  

![Snake game screenshot](snake.png)

## Modified Features

The modified version includes current score display, obstacles, WASD controls, and a pause menu.

The snake can be controlled using arrow keys or WASD. Press P to pause the game. When the pause menu appears, press C to continue, R to restart, or E/Esc to exit.

## GUI Modifications

The game interface was modified by adding a current score display, visible obstacles, and a pause menu.

The chosen project is **Snake Game using Java**. It belongs to **Java Projects For Beginners**.

The game now has a scoring system, obstacle collision mechanism, pause menu system, restart function, exit function, and WASD keyboard control.

The player gets 10 points whenever the snake eats an apple. The game starts with three obstacles. If the snake hits an obstacle, the game ends. The apple generation logic also prevents apples from appearing on the snake body or on obstacles.

## Code Structure Modification

A new class named SnakeGameModel.java was added to separate part of the game logic from the Swing GUI. This makes the game logic easier to test with JUnit.

## JUnit Testing

JUnit 5 tests were added in:

```text
src/test/java/com/zetcode/SnakeGameModelTest.java