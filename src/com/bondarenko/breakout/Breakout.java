package com.bondarenko.breakout;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.util.RandomGenerator;
import com.shpp.cs.a.graphics.WindowProgram;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * The class Breakout extends WindowProgram.
 * <p>
 * Simulate the arcade video game "Breakout"
 */
public class Breakout extends WindowProgram {

    /*
     * Width and height of application window in pixels
     */
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;

    /*
     * Dimensions of game board (usually the same)
     */
    private static final int WIDTH = APPLICATION_WIDTH;
    private static final int HEIGHT = APPLICATION_HEIGHT;

    /*
     * Dimensions of the paddle
     */
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;

    /*
     * Offset of the paddle up from the bottom
     */
    private static final int PADDLE_Y_OFFSET = 30;

    /*
     * Number of bricks per row
     */
    private static final int NBRICKS_PER_ROW = 10;

    /*
     * Number of rows of bricks
     */
    private static final int NBRICK_ROWS = 10;

    /*
     * Separation between bricks
     */
    private static final int BRICK_SEP = 4;

    /*
     * Width of a brick
     */
    private static final int BRICK_WIDTH =
            (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

    /*
     * Height of a brick
     */
    private static final int BRICK_HEIGHT = 8;

    /*
     * Radius of the ball in pixels
     */
    private static final int BALL_RADIUS = 10;

    /*
     * Offset of the top brick row from the top
     */
    private static final int BRICK_Y_OFFSET = 70;

    /*
     * Number of turns
     */
    private static final int NTURNS = 3;

    /*
     * Color of first upper 2 rows
     */
    private static final Color FIRST_COLOR = Color.RED;

    /*
     * Color of second upper 2 rows
     */
    private static final Color SECOND_COLOR = Color.ORANGE;

    /*
     * Color of third upper 2 rows
     */
    private static final Color THIRD_COLOR = Color.YELLOW;

    /*
     * Color of fourth upper 2 rows
     */
    private static final Color FOURTH_COLOR = Color.GREEN;

    /*
     * Color of fifth upper 2 rows
     */
    private static final Color FIFTH_COLOR = Color.CYAN;

    /*
     * Score for broken brick with first color
     */
    private static final int FIRST_COLOR_SCORE = 100;

    /*
     * Score for broken brick with second color
     */
    private static final int SECOND_COLOR_SCORE = 40;

    /*
     * Score for broken brick with third color
     */
    private static final int THIRD_COLOR_SCORE = 20;

    /*
     * Score for broken brick with fourth color
     */
    private static final int FOURTH_COLOR_SCORE = 10;

    /*
     * Score for broken brick with fifth color
     */
    private static final int FIFTH_COLOR_SCORE = 5;

    /*
     * Text template for score label
     */
    private static final String SCORE_TEMPLATE = "Score: %d";

    /*
     * Text template for turns label
     */
    private static final String TURNS_TEMPLATE = "Number of turns: %d";

    /*
     * Text of message when program wait for mouse click
     */
    private static final String CLICK_MOUSE_MESSAGE = "Click mouse to start!";

    /*
     * Text of message when player won
     */
    private static final String WIN_MESSAGE = "Congratulations, you WON!";

    /*
     * Text template of message when player lose
     */
    private static final String LOSE_TEMPLATE = "You lose, your score: %d";

    /*
     * Coefficient of shift of click message from upper
     * if 1 is a full screen
     */
    private static final double MESSAGE_SHIFT_FROM_UPPER = 0.75;

    /*
     * Text size of the score and turns labels
     */
    private static final int SCORE_AND_TURNS_TEXT_SIZE = 14;

    /*
     * Text size of the final message
     */
    private static final int FINAL_MESSAGE_TEXT_SIZE = 20;

    /*
     * Pause between frames in milliseconds
     */
    private static final int PAUSE_TIME = 10;

    /*
     * Paddle object
     */
    private GRect paddle;

    /*
     * Ball displacement along the axes x and y per frame
     */
    private double vx = generateDouble();
    private double vy = 3.0;

    /*
     * Number of bricks still on the field
     */
    private int numberOfBricks = NBRICK_ROWS * NBRICKS_PER_ROW;

    /*
     * Total score
     */
    private int score;

    /**
     * Adds paddle and bricks to window.
     * <p>
     * Adds mouse listeners for control paddle.
     * <p>
     * Adds ball and label with message.
     * <p>
     * Then game start!
     */
    @Override
    public void run() {
        addDelay();

        addPaddle();
        addMouseListeners();

        addAllBricks();

        startGame();
    }

    /**
     * Adds a ball and a label with message. Then start move the ball,
     * check colliders and change ball directory if the ball hit them.
     * <p>
     * If the ball hit a brick, a brick disappears.
     * <p>
     * If the ball falls down, player loses the turn.
     * <p>
     * If all turns loses, player gets message that he lose.
     * <p>
     * If all bricks are broken, player gets message that he won.
     * <p>
     * Additionally check a score and adds labels with number of turns and score.
     */
    private void startGame() {
        int turn = 1;
        GLabel info = addInfo();
        GLabel turnsLabel = addTurnsLabel();
        GLabel scoreLabel = addScoreLabel();
        GOval ball = addBall();

        while (numberOfBricks > 0 && turn <= NTURNS) {
            ball.setLocation(getWidth() / 2.0 - BALL_RADIUS, getHeight() / 2.0 - BALL_RADIUS);
            info.setVisible(true);
            waitForClick();
            turnsLabel.setLabel(String.format(TURNS_TEMPLATE, NTURNS - turn));
            info.setVisible(false);
            moveBallAndProcessHits(ball, scoreLabel);
            turn++;
            this.vx = generateDouble();
        }

        addFinalLabel();
    }

    /**
     * Moves a ball, check colliders and change ball directory if the ball hit them.
     * <p>
     * If the ball hit a brick, a brick disappears, score increases and edit score label.
     * <p>
     * Method finished when there are no bricks
     * or ball fall down.
     *
     * @param ball a ball for move
     */
    private void moveBallAndProcessHits(GOval ball, GLabel scoreLabel) {
        while (numberOfBricks > 0 && ball.getY() < getHeight()) {
            ball.move(this.vx, this.vy);
            checkCollider(ball, getCollidingObject(ball));
            checkWalls(ball);
            scoreLabel.setLabel(String.format(SCORE_TEMPLATE, this.score));
            pause(PAUSE_TIME);
        }
    }

    /**
     * Adds a paddle to our window.
     * <p>
     * Paddle width, height and offset from below
     * declared as constants.
     */
    private void addPaddle() {
        GRect paddle = new GRect((getWidth() - PADDLE_WIDTH) / 2.0,
                getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT,
                PADDLE_WIDTH,
                PADDLE_HEIGHT);
        paddle.setFilled(true);
        add(paddle);
        this.paddle = paddle;
    }

    /**
     * Adds a ball to the center of our window.
     * <p>
     * Radius of a ball declared as constant.
     *
     * @return link to added ball
     */
    private GOval addBall() {
        double ballX = getWidth() / 2.0 - BALL_RADIUS;
        double ballY = getHeight() / 2.0 - BALL_RADIUS;
        GOval ball = new GOval(ballX, ballY, BALL_RADIUS * 2, BALL_RADIUS * 2);
        ball.setFilled(true);
        add(ball);
        return ball;
    }

    /**
     * Change paddle location when mouse moved
     *
     * @param mouseEvent the event to be processed
     */
    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        double x = mouseEvent.getX();
        if ((x >= PADDLE_WIDTH / 2.0) && (x <= getWidth() - PADDLE_WIDTH / 2.0)) {
            this.paddle.setLocation(x - PADDLE_WIDTH / 2.0, this.paddle.getY());
        } else if (x < PADDLE_WIDTH / 2.0) {
            this.paddle.setLocation(0, this.paddle.getY());
        } else {
            this.paddle.setLocation(getWidth() - PADDLE_WIDTH, this.paddle.getY());
        }
    }

    /**
     * Returns a random double value from 1.0 to 3.0
     * and with a random sign.
     *
     * @return a random double from 1.0 to 3.0 or from -3.0 to -1.0
     */
    private double generateDouble() {
        RandomGenerator randomGenerator = RandomGenerator.getInstance();
        double value = randomGenerator.nextDouble(1.0, 3.0);

        if (randomGenerator.nextBoolean()) {
            value = -value;
        }
        return value;
    }

    /**
     * Checks a ball location and if it is near a wall
     * then change ball direction
     *
     * @param ball a ball for check
     */
    private void checkWalls(GOval ball) {
        double ballX = ball.getX();
        double ballY = ball.getY();

        if (((ballX <= 0) && this.vx < 0)
                || ((ballX >= (getWidth() - BALL_RADIUS * 2)) && this.vx > 0)) {
            this.vx = -this.vx;
        }
        if ((ballY <= 0) && this.vy < 0) {
            this.vy = -this.vy;
        }
    }

    /**
     * Checks collider type and call appropriate process
     *
     * @param ball     a ball for location checks
     * @param collider a collider for check
     */
    private void checkCollider(GOval ball, GObject collider) {
        if (collider == null || collider instanceof GLabel) {
            return;
        }

        if (collider == this.paddle) {
            processPaddle(ball);
        } else {
            processBrick(ball, collider);
        }
    }

    /**
     * Checks colliders near a ball
     * and returns link of first found object or null
     *
     * @param ball a ball for check
     * @return link of collider object if exists or null
     */
    private GObject getCollidingObject(GOval ball) {
        int curvature = (int) ((Math.sqrt(BALL_RADIUS * BALL_RADIUS * 2) - BALL_RADIUS) / 2);

        GObject object = getElementAt(ball.getX() - 1, ball.getY() + BALL_RADIUS);
        if (object != null) {
            return object;
        }

        object = getElementAt(ball.getX() + BALL_RADIUS, ball.getY() - 1);
        if (object != null) {
            return object;
        }

        object = getElementAt(ball.getX() + BALL_RADIUS * 2 + 1, ball.getY() + BALL_RADIUS);
        if (object != null) {
            return object;
        }

        object = getElementAt(ball.getX() + BALL_RADIUS, ball.getY() + BALL_RADIUS * 2 + 1);
        if (object != null) {
            return object;
        }

        object = getElementAt(ball.getX() + curvature, ball.getY() + curvature);
        if (object != null) {
            return object;
        }

        object = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
        if (object != null) {
            return object;
        }

        object = getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2);
        if (object != null) {
            return object;
        }

        object = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
        return object;
    }

    /**
     * Adds a brick to our window with input parameters.
     * <p>
     * Brick width and height declared as constants.
     *
     * @param x     x coordinate of left-upper corner of a brick
     * @param y     y coordinate of left-upper corner of a brick
     * @param color color of a brick
     */
    private void addBrick(double x, double y, Color color) {
        GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
        brick.setColor(color);
        brick.setFilled(true);
        add(brick);
    }

    /**
     * Adds bricks to our window.
     * <p>
     * Brick width, height, number in row, number of rows,
     * brick offset from upper and separation between bricks
     * declared as constants.
     */
    private void addAllBricks() {
        double startX = (getWidth() - ((BRICK_WIDTH + BRICK_SEP) * NBRICKS_PER_ROW - BRICK_SEP)) / 2.0;

        for (int i = 0; i < NBRICK_ROWS; i++) {
            for (int j = 0; j < NBRICKS_PER_ROW; j++) {
                addBrick(startX + j * (BRICK_WIDTH + BRICK_SEP),
                        BRICK_Y_OFFSET + i * (BRICK_HEIGHT + BRICK_SEP),
                        getBrickColor(i));
            }
        }
    }

    /**
     * Adds a delay of 200 milliseconds.
     */
    private void addDelay() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks a ball location relative to a brick location
     * and change a ball directory according to it.
     * Removes a brick and reduce counter of bricks.
     *
     * @param ball  a ball for location check
     * @param brick a brick for check and remove
     */
    private void processBrick(GOval ball, GObject brick) {
        double ballCenterX = ball.getX() + BALL_RADIUS;
        double ballCenterY = ball.getY() + BALL_RADIUS;
        boolean isBallCenterLeftThanBrick = ballCenterX < brick.getX();
        boolean isBallCenterRightThanBrick = ballCenterX > brick.getX() + BRICK_WIDTH;
        boolean isBallCenterHigherThanBrick = ballCenterY < brick.getY();
        boolean isBallCenterLowerThanBrick = ballCenterY > brick.getY() + BRICK_HEIGHT;
        boolean isHitInYDirection = (isBallCenterHigherThanBrick || isBallCenterLowerThanBrick)
                && !isBallCenterLeftThanBrick
                && !isBallCenterRightThanBrick;
        boolean isHitInXDirection = (isBallCenterLeftThanBrick || isBallCenterRightThanBrick)
                && !isBallCenterHigherThanBrick
                && !isBallCenterLowerThanBrick;

        if (isHitInXDirection) {
            this.vx = -this.vx;
        } else if (isHitInYDirection) {
            this.vy = -this.vy;
        } else {
            if (this.vx > 0) {
                if (isBallCenterLeftThanBrick) {
                    this.vx = -this.vx;
                    if ((isBallCenterHigherThanBrick && this.vy > 0)
                            || (isBallCenterLowerThanBrick && this.vy < 0)) {
                        this.vy = -this.vy;
                    }
                } else {
                    this.vy = -this.vy;
                }
            } else {
                if (isBallCenterLeftThanBrick) {
                    this.vy = -this.vy;
                } else {
                    this.vx = -this.vx;
                    if ((isBallCenterHigherThanBrick && this.vy > 0)
                            || (isBallCenterLowerThanBrick && this.vy < 0)) {
                        this.vy = -this.vy;
                    }
                }
            }
        }
        addScore(brick);
        remove(brick);
        this.numberOfBricks--;
    }

    /**
     * Checks a ball location relative to a paddle location
     * and change a ball directory according to it
     *
     * @param ball a ball for location check
     */
    private void processPaddle(GOval ball) {
        double ballCenterX = ball.getX() + BALL_RADIUS;
        double ballCenterY = ball.getY() + BALL_RADIUS;

        if (this.vy > 0) {
            if (ballCenterY < this.paddle.getY()) {
                this.vy = -this.vy;
                if (ballCenterX < this.paddle.getX()
                        || ballCenterX > this.paddle.getX() + PADDLE_WIDTH) {
                    this.vx = -this.vx;
                }
            } else if (ballCenterX < this.paddle.getX()) {
                this.vx = -Math.abs(this.vx);
            } else if (ballCenterX > this.paddle.getX() + PADDLE_WIDTH) {
                this.vx = Math.abs(this.vx);
            }
        }
    }

    /**
     * Returns a color of bricks that should be in the input row.
     * <p>
     * Colors are declared as constants.
     *
     * @param rowNumber number of a row (starts from 0)
     * @return a color of bricks that should be in the input row
     */
    private Color getBrickColor(int rowNumber) {

        // The number of rows after which the colors will start to repeat
        int rowsToRepeatColors = 10;
        int counter = rowNumber % rowsToRepeatColors;

        //get next color every 2 rows
        if (counter < 2) {
            return FIRST_COLOR;
        } else if (counter < 4) {
            return SECOND_COLOR;
        } else if (counter < 6) {
            return THIRD_COLOR;
        } else if (counter < 8) {
            return FOURTH_COLOR;
        } else {
            return FIFTH_COLOR;
        }
    }

    /**
     * Adds a turns label to our window.
     * <p>
     * Text template and start number of turns declared as constants
     *
     * @return link to added turns label
     */
    private GLabel addTurnsLabel() {
        GLabel turns = new GLabel(String.format(TURNS_TEMPLATE, NTURNS));
        turns.setLocation(0, getHeight() - turns.getDescent());
        turns.setFont(new Font(Font.SERIF, Font.BOLD, SCORE_AND_TURNS_TEXT_SIZE));
        add(turns);
        return turns;
    }

    /**
     * Adds a score label to our window.
     * <p>
     * Text template declared as constant.
     * Score stored in class field.
     *
     * @return link to added turns label
     */
    private GLabel addScoreLabel() {
        GLabel scoreLabel = new GLabel(String.format(SCORE_TEMPLATE, this.score));
        scoreLabel.setLocation(getWidth() / 2.0, getHeight() - scoreLabel.getDescent());
        scoreLabel.setFont(new Font(Font.SERIF, Font.BOLD, SCORE_AND_TURNS_TEXT_SIZE));
        add(scoreLabel);
        return scoreLabel;
    }

    /**
     * Increases the score according to a removed brick color
     *
     * @param brick a removed brick
     */
    private void addScore(GObject brick) {
        if (brick.getColor().equals(FIRST_COLOR)) {
            this.score += FIRST_COLOR_SCORE;
        } else if (brick.getColor().equals(SECOND_COLOR)) {
            this.score += SECOND_COLOR_SCORE;
        } else if (brick.getColor().equals(THIRD_COLOR)) {
            this.score += THIRD_COLOR_SCORE;
        } else if (brick.getColor().equals(FOURTH_COLOR)) {
            this.score += FOURTH_COLOR_SCORE;
        } else if (brick.getColor().equals(FIFTH_COLOR)) {
            this.score += FIFTH_COLOR_SCORE;
        }
    }

    /**
     * Adds a final label to the center of our window.
     * <p>
     * Text of a label depends on the number of bricks
     * and declared as constants.
     */
    private void addFinalLabel() {
        GLabel finalLabel;

        if (this.numberOfBricks == 0) {
            finalLabel = new GLabel(WIN_MESSAGE);
        } else {
            finalLabel = new GLabel(String.format(LOSE_TEMPLATE, this.score));
        }

        finalLabel.setFont(new Font(Font.SERIF, Font.BOLD, FINAL_MESSAGE_TEXT_SIZE));
        finalLabel.setLocation((getWidth() - finalLabel.getWidth()) / 2, getHeight() / 2.0);
        add(finalLabel);
    }

    /**
     * Adds a label to our window.
     * <p>
     * Text and shift coefficient of a label
     * declared as constants.
     *
     * @return link to added label
     */
    private GLabel addInfo() {
        GLabel info = new GLabel(CLICK_MOUSE_MESSAGE);
        info.setLocation((getWidth() - info.getWidth()) / 2, getHeight() * MESSAGE_SHIFT_FROM_UPPER);
        add(info);
        return info;
    }
}
