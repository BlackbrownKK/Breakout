
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.util.RandomGenerator;
import com.shpp.cs.a.graphics.WindowProgram;

import java.awt.*;
import java.awt.event.MouseEvent;


public class Breakout extends WindowProgram {
    /**
     * Width and height of application window in pixels
     */
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;
    /**
     * Dimensions of the paddle
     */
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;
    /**
     * Offset of the paddle up from the bottom
     */
    private static final int PADDLE_Y_OFFSET = 30;
    /**
     * Number of bricks per row
     */
    private static final int NBRICKS_PER_ROW = 10;
    /**
     * Number of rows of bricks
     */
    private static final int NBRICK_ROWS = 10;
    /**
     * Separation between bricks
     */
    private static final int BRICK_SEP = 4;
    /**
     * Height of a brick
     */
    private static final int BRICK_HEIGHT = 8;
    /*
    set the color of the ball
     */
    private static final Color BALL_CALAR = Color.BLACK;
    /*
    set the color of the paddle
    */
    private static final Color PADDLE_CALAR = Color.BLACK;
    /**
     * Radius of the ball in pixels
     */
    private static final int BALL_RADIUS = 10;
    /**
     * Diameter of the ball in pixels
     */
    private static final int BALL_DIAMETER = BALL_RADIUS * 2;
    /**
     * Offset of the top brick row from the top
     */
    private static final int BRICK_Y_OFFSET = 70;
    /**
     * Number of turns
     */
    private static final int NTURNS = 3;
    /**
     * val of the gravity
     */
    private static final double GRAVITY = 0.000;
    /**
     * the random first val of dX
     */
    private static final double FIRST_VAL_RANDOM_DX = 1;
    /**
     * the random second val of dX
     */
    private static final double SECOND_VAL_RANDOM_DX = 3;
    /**
     * the random val of dX when + change to minus or minus change to plus.
     */
    private static final double CHANGEING_VAL_RANDOM_DX = 0.5;

    /* FPS */
    private static final double PAUSE_TIME = 1000 / 50.0;
    /*
    the width of the ball;
     */
    double brickWidth = 0;
    /*
    num of all brick in the window, when the ball catch one brick, bricksCount--;
    */
    int bricksCount;
    /*
    the GOval that is the ball, the class GObject
    */
    GOval ball;
    /*
    the GRect that is the paddle, the class GObject
    */
    GRect paddle = makePaddle();
    /*
    all colors of rows of bricks, they are manage in this array;
    */
    Color[] colorsOfBricks = makeColorsOfBlocks();
    /*
    the val of dX of the ball, used in many methods
    */
    private static double dX = 0;
    /*
    the val of dY of the ball, used in many methods
    */
    private static double dY = 3;

    boolean gameOver = true;



    /*
    this method do the limit of movie of the paddle (left and right) and controls by mouse the moving of the paddle
    */
    public void mouseMoved(MouseEvent mouseEvent) {
        add(paddle);
        paddle.setLocation(mouseEvent.getX() - PADDLE_WIDTH / 2.0, makePaddle().getY());
        if (paddle.getX() + PADDLE_WIDTH > getWidth()) {
            paddle.setLocation(getWidth() - PADDLE_WIDTH, makePaddle().getY());
        }
        if (paddle.getX() < 0) {
            paddle.setLocation(0, makePaddle().getY());
        }
    }

    /*
    here app starts. first add the mouse listeners, then draws the rows of bricks. At last starts the method runGame;
    */
    @Override
    public void run() {
        brickWidth = (getWidth() - (NBRICKS_PER_ROW + 1) * BRICK_SEP) / (double) NBRICKS_PER_ROW;
        setTitle("Breakout");
        addMouseListeners();
        drawBricksBlock();
        runGame();
    }


    /*
    In this method there are the main loop with 3 lives of the game and the loop witch controls the game processing:
    when the ball put on to the paddle of catch the bricks, or falls down. Every situation with the ball is
    written here. Here is two reports about the game status.
     */
    private void runGame() {
        ball = makeBall(getWidth() / 2.0 - BALL_RADIUS, getHeight() / 2.0 - BALL_RADIUS);
        for (int i = 0; i < NTURNS; i++) {
            add(ball);
            dX = xTrajectory();
            waitForClick();
            while (checkGameOver(gameOver)) {
                ball.move(dX, dY);
                if (dY > 0) dY += GRAVITY;
                if (checkContacktWithObject() && dY > 0) ballColliding(); // case ball + paddle
                if (ball.getY() + BALL_DIAMETER > getHeight()) { // case the ball fall down
                    remove(ball);
                    gameOver = false;
                    System.out.println("you have lost the round " + (i + 1) + ", you have " + (2 - i) + " lives");
                }
                if (ball.getY() < 0 && dY < 0) ballColliding(); // ball jumps from the ceiling
                if (ball.getX() < 0 && dX < 0) dX = -dX; // ball jumps from left wall
                if (ball.getX() + BALL_DIAMETER > getWidth() && dX > 0) dX = -dX; // ball jumps from right wall
                pause(PAUSE_TIME);
            }
            if (bricksCount == 0) {
                System.out.println("You win");
                System.exit(0);
            }
            replayGame();
        }
        if (bricksCount != 0) System.out.println("You lost this game");
        System.exit(0);
    }

    /*
    here are the actions of the ball, when it has a contact with a brick, the puddle, down or up wall of the window.
    */
    private void ballColliding() {
        dY = -dY;
        dX = xTrajectory();
    }

    /*
    Here are the actions after when the players had lost the current game
    */
    private void replayGame() {
        gameOver = true;
        ball = makeBall(getWidth() / 2.0 - BALL_RADIUS, getHeight() / 2.0 - BALL_RADIUS);
        dX = 0;
        dY = 3;
    }

    /*
    this method takes the true if there was some situation when the players have lost the current game
    check and return boolean true to get out from the loop in the method runGame. Or when the players
    have collect all the bricks from the window, current methods gets the true value too.
    */
    private boolean checkGameOver(boolean gameOver) {
        if (!gameOver || bricksCount == 0) gameOver = false;
        return gameOver;
    }

    /*
    There are a testes on the control points of the ball. If one of them has a contact with a puddle or with a bricks.
    Or if there was a contact with one of them, returns a boolean true.
    */
    private boolean checkContacktWithObject() {
        GObject collider = getCollidingObject(ball.getX(), ball.getY() + BALL_DIAMETER); // point №3
        if (checkContactOfBall(collider)) return checkContactOfBall(collider);
        collider = getCollidingObject(ball.getX() + BALL_DIAMETER, ball.getY() + BALL_DIAMETER); // point №4
        if (checkContactOfBall(collider)) return checkContactOfBall(collider);
        collider = getCollidingObject(ball.getX(), ball.getY()); // point  №1
        if (checkContactOfBall(collider)) return checkContactOfBall(collider);
        collider = getCollidingObject(ball.getX() + BALL_DIAMETER, ball.getY()); //  point №2
        return checkContactOfBall(collider);
    }
    /*
    there is written three cases, it takes a GObject which had a contact with the ball in current time in the loop,
    if GObject was a puddle -> returns true;  if GObject was a not null -> returns true + additional actions:
    ballColliding(), remove(collider), val bricksCount--; else - false, if collider is not a paddle and not null;
    */
    private boolean checkContactOfBall(GObject collider) {
        boolean wasContacted;
        wasContacted = checkContactWithPaddle(collider, paddle);
        if (wasContacted) return wasContacted;
        if (collider != null) { // case ball get to the brick
            ballColliding();
            remove(collider);
            bricksCount--;
            return wasContacted;
        }
        return wasContacted;
    }

    /*
    There is a test if gObjectOne is equals gObjectTwo, if equals, returns a boolean true.
    */
    private boolean checkContactWithPaddle(GObject gObjectOne, GObject gObjectTwo) {
        return gObjectOne == gObjectTwo;
    }

    /*
    gets the coordinates of the ball and returns the GObject which had a contact with the ball
    in current time in the loop.
     */
    private GObject getCollidingObject(double x, double y) {
        return getElementAt(x, y);
    }

    /*
    return a double random valuable of the dX of the ball
     */
    private double xTrajectory() {
        RandomGenerator rgen = RandomGenerator.getInstance();
        dX = rgen.nextDouble(FIRST_VAL_RANDOM_DX, SECOND_VAL_RANDOM_DX);
        if (rgen.nextBoolean(CHANGEING_VAL_RANDOM_DX)) dX = -dX;
        return dX;
    }

    /*
    this method makes the paddle, and describes it`s the first position, in the center of the window;
    */
    private GRect makePaddle() {
        double xPosition = getWidth() / 2.0 - PADDLE_WIDTH / 2.0;
        double yPosition = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT/2.0;
        GRect paddle = new GRect(xPosition, yPosition, PADDLE_WIDTH, PADDLE_HEIGHT);
        paddle.setFilled(true);
        paddle.setColor(PADDLE_CALAR);
        paddle.setFillColor(PADDLE_CALAR);
        return paddle;
    }

    /*
    this method makes the ball, and describes the first position of the ball, in the center of the window;
    */
    private GOval makeBall(double xPosition, double yPosition) {
        GOval ball = new GOval(xPosition, yPosition, BALL_DIAMETER, BALL_DIAMETER);
        ball.setFilled(true);
        ball.setColor(BALL_CALAR);
        ball.setFillColor(BALL_CALAR);
        return ball;
    }

    /*
    this method draws all row of bricks. The brickWidth depends on the getWidth();
    */
    private void drawBricksBlock() {
        double yPosition = BRICK_Y_OFFSET;
        for (int i = 0; i < NBRICK_ROWS; i++) {
            drawBricksRow(BRICK_SEP, yPosition, colorsOfBricks[i]);
            yPosition += BRICK_HEIGHT + BRICK_SEP;
        }
    }


    private Color[] makeColorsOfBlocks() {
        Color[] colors = new Color[]{Color.RED, Color.RED, Color.ORANGE, Color.ORANGE, Color.YELLOW, Color.YELLOW,
                Color.GREEN, Color.GREEN, Color.CYAN, Color.CYAN};
        Color[] colorsOfBricks = new Color[NBRICK_ROWS];
        int j = 0;
        for (int i = 0; i < colorsOfBricks.length; i++) {
            if (i < colors.length) {
                colorsOfBricks[i] = colors[j];
            } else {
                colorsOfBricks[i] = colors[i % colors.length];
            }
            j++;
        }
        return colorsOfBricks;
    }

    /*
    this method draws one row of bricks in according to x, y positions of the first element and color.
    */
    private void drawBricksRow(double xPosition, double yPosition, Color color) {
        for (int i = 0; i < NBRICKS_PER_ROW; i++) {
            drawBrick(xPosition, yPosition, color);
            xPosition += brickWidth + BRICK_SEP;
        }
    }

    /*
    this method draws one brick in according to x, y positions and color. Every time when this method calls,
    added +1 to bricksCount;
    */
    private void drawBrick(double xPosition, double yPosition, Color color) {
        GRect gRect = new GRect(
                xPosition,
                yPosition,
                brickWidth,
                BRICK_HEIGHT);
        gRect.setFilled(true);
        gRect.setFillColor(color);
        gRect.setColor(color);
        add(gRect);
        bricksCount++;
    }
}