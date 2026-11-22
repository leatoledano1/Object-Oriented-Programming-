package bricker.main;

import bricker.brick_strategies.CollisionStrategy;
import bricker.brick_strategies.FactoryOfStrategies;
import bricker.gameobjects.*;

/** imports the main game engine class used to run and manage the game loop */
import danogl.GameManager;
/** import for the base class of all objects in the game world */
import danogl.GameObject;
/** imports layer-management utility for drawing objects in different render layers */
import danogl.collisions.Layer;
import danogl.gui.*;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

import danogl.gui.UserInputListener;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * the main controller class for the Bricker game.
 * this class is responsible for setting up the game world,
 * creating game objects (ball, paddle, bricks, hearts, walls),
 * updating the game each frame,
 * and handling win and losing conditions
 * the manager also supports choosing the number of rows and bricks per row,
 * and allows restarting the game through user dialogs.
 */
public class BrickerGameManager extends GameManager {
    /** height of the game window in pixels */
    private static final int HEIGHT = 500;
    /** width of the game window in pixels */
    private static final int WIDTH = 700;
    /** initial speed of the main ball */
    public static final int BALL_SPEED = 200;
    /** default number of brick rows if the user provides no arguments */
    private static final int DEFAULT_ROWS = 7;
    /** default number of bricks in each row */
    private static final int DEFAULT_BRICKS_PER_ROW = 8;
    /** the fixed height of each brick */
    private static final float BRICK_HEIGHT = 15;
    /** initial number of lives the player starts with */
    private static final int DEFAULT_LIVES = 3;
    /** the width and height of the main ball */
    public static final int BALL_SIZE = 20;
    /** vector representing the ball's dimensions */
    public static final Vector2 BALL_DIM = new Vector2(20, 20);
    /** vector representing the dimensions of a falling heart */
    public static final Vector2 HEART_DIM = new Vector2(20, 20);
    /** height of the paddle in pixels */
    private static final int PADDLE_HEIGHT = 15;
    /** width of the paddle in pixels */
    private static final int PADDLE_WIDTH = 100;
    /** vector containing the paddle's dimensions */
    public static final Vector2 PADDLE_SIZE = new Vector2(PADDLE_WIDTH, PADDLE_HEIGHT);
    /** thickness of the left and right walls */
    private static final float WALL_THICKNESS = 8f;
    /** thickness of the top boundary (roof) */
    private static final float ROOF_THICKNESS = 8f;

    private final int rows;
    private final int bricksPerRow;
    private Ball ball;
    private Vector2 windowDimensions;
    private WindowController windowController;
    private Hearts hearts;
    private Counter brickCounter;
    private UserInputListener inputListener;
    private Brick[][] brickArr;
    private Counter livesCounter;

    /**
     * default constructor: creates a game with 7 rows and 8 bricks per row.
     * @param windowTitle game window title
     * @param windowDimensions window size
     */
    public BrickerGameManager(String windowTitle , Vector2 windowDimensions) {
        super(windowTitle , windowDimensions);
        this.rows= DEFAULT_ROWS;
        this.bricksPerRow = DEFAULT_BRICKS_PER_ROW;
        this.brickArr = new Brick[rows][bricksPerRow];
    }

    /**
     * constructor that allows custom number of rows and bricks per row.
     * @param windowTitle title of the game window
     * @param windowDimensions window size
     * @param rows number of brick rows
     * @param bricksPerRow number of bricks in each row
     */
    public BrickerGameManager(String windowTitle , Vector2 windowDimensions, int rows, int bricksPerRow) {
        super(windowTitle , windowDimensions);
        this.rows = rows;
        this.bricksPerRow = bricksPerRow;
        this.brickArr = new Brick[rows][bricksPerRow];
    }

    /**
     * creates the background image of the game and adds it to the background layer.
     * @param imageReader used to load the background image from assets
     */
    private void createBackground(ImageReader imageReader){
        Renderable backGroundImage = imageReader.readImage("assets/DARK_BG2_small.jpeg",
                false);
        GameObject backGround = new GameObject(Vector2.ZERO , windowDimensions, backGroundImage);
        gameObjects().addGameObject(backGround, Layer.BACKGROUND);
    }

    /**builds the three world boundaries
     * left wall, right wall, and the ceiling
     * these prevent the ball from leaving the screen
     * @param windowDimensions size of the window
     */
    private void buildWalls(Vector2 windowDimensions) {
        //we do want them to be thick enough so the ball does not pass through the walls adn roof
        GameObject leftWall =  new GameObject(Vector2.ZERO,
                new Vector2(WALL_THICKNESS, windowDimensions.y()), null);
        GameObject rightWall = new GameObject(new Vector2(windowDimensions.x()-WALL_THICKNESS,0),
                new Vector2(8, windowDimensions.y()), null);
        GameObject topWall = new GameObject(Vector2.ZERO,
                new Vector2(windowDimensions.x(),ROOF_THICKNESS), null);

        gameObjects().addGameObject(rightWall);
        gameObjects().addGameObject(leftWall);
        gameObjects().addGameObject(topWall);
    }

    /**
     * creates the main ball of the game and places it at the center
     * with an initial downward velocity.
     * @param imageReader used to load the ball sprite
     * @param soundReader used to load the ball collision sound
     */
    private void createBall(ImageReader imageReader,SoundReader soundReader){
        Renderable ballImage = imageReader.readImage("assets/ball.png" , true);
        Sound collisionSound = soundReader.readSound("assets/blop.wav");
        ball = new Ball(Vector2.ZERO , new Vector2(BALL_SIZE,BALL_SIZE),
                ballImage, collisionSound);
        ball.setCenter(windowDimensions.mult(0.5F));
        ball.setVelocity(new Vector2(0, BALL_SPEED));
        gameObjects().addGameObject(ball);
    }

    /**
     * creates the user-controlled paddle at the bottom of the screen
     * @param imageReader loads the paddle image from assets
     */
    private void createPaddle(ImageReader imageReader){
        Renderable paddleImage = imageReader.readImage("assets/paddle.png",true);
        GameObject userPaddle = new Paddle(Vector2.ZERO, new Vector2(PADDLE_WIDTH,PADDLE_HEIGHT),
                paddleImage, inputListener, windowDimensions);
        gameObjects().addGameObject(userPaddle);
        userPaddle.setCenter(new Vector2(windowDimensions.x()/2 ,windowDimensions.y()-30));
    }

    /**
     * A function that creates a brick
     * while creating one brick we also send it a strategy she implements
     * later, when the ball collides with each brick
     * the strategy she owns is released
     * @param imageReader loads brick images
     * @param soundReader loads sound effects needed by some strategies
     */
    private void createBrick(ImageReader imageReader, SoundReader soundReader){
        Renderable brickImage = imageReader.readImage("assets/brick.png",true);
        //the size will change depending on how much bricks the user wants in a row
        float brickWidth = windowDimensions.x() / bricksPerRow;

        //On factory object that is able to choose a strategy
        FactoryOfStrategies strategyFactory = new FactoryOfStrategies(gameObjects(),
                imageReader, soundReader, inputListener, windowDimensions,
                livesCounter, hearts, brickCounter, brickArr);

        //bricks creation one by one, each one gets a strategy assigned
        //adding each break to an array we will use later
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < bricksPerRow; col++) {
                CollisionStrategy strategy = strategyFactory.chooseStrategy();
                Vector2 brickTopLeft = new Vector2(col * brickWidth, row * BRICK_HEIGHT);
                Brick brick = new Brick(brickTopLeft, new Vector2(brickWidth, BRICK_HEIGHT), brickImage,
                        strategy, row ,col);
                brickArr[row][col] = brick;
                gameObjects().addGameObject(brick);
                brickCounter.increment();
            }
        }
    }

    /**
     * creates the heart-based lives display (icons + numeric text)
     * and adds it to the UI layer.
     * @param imageReader loads the heart image
     */
    private void createHearts(ImageReader imageReader){
        Renderable heartImage = imageReader.readImage("assets/heart.png", true);
        hearts = new Hearts(DEFAULT_LIVES, heartImage, gameObjects());
        gameObjects().addGameObject(hearts.getNumericDisplay(), Layer.UI);
    }

    /**
     * @param imageReader An object used to load images from assets
     * @param soundReader An object used to load and manage sound assets for game events
     * @param inputListener An object used to "listen" and handle user input(mainly to control the paddle)
     * @param windowController An object that manages the game window properties and dimensions
     * this function calls the creating methods and controls the ball's characteristics
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        brickCounter = new Counter(0);
        this.windowController = windowController;
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowDimensions = windowController.getWindowDimensions();
        this.inputListener = inputListener;

        createBackground(imageReader);
        createBall(imageReader, soundReader);
        createPaddle(imageReader);
        createHearts(imageReader);
        createBrick(imageReader, soundReader);
        buildWalls(windowDimensions);

        float ballVelX = BALL_SPEED;
        float ballVelY = BALL_SPEED;
        Random rand  = new Random();
        if(rand.nextBoolean()){
            ballVelX *=-1;
        }
        if(rand.nextBoolean()){
            ballVelY *=-1;
        }

        ball.setVelocity(new Vector2(ballVelX,ballVelY));
    }

    /** runs once every frame.
     * checks for win/lose conditions,
     * listens for the user pressing W for instant win,
     * removes out-of-bounds puck balls
     * and opens a dialog if the game ends.
     * @param deltaTime time since last frame
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(inputListener.isKeyPressed(KeyEvent.VK_W)){
            if(windowController.openYesNoDialog("You win! Play again?")){
                windowController.resetGame();
            }
            else {
                windowController.closeWindow();
            }
        }
        removeOutOfBoundsBalls();
        checkForGameEnd();
    }

    /**
     * removes puck balls that fall off the screen (top or bottom).
     * the main ball is not removed here, only pucks created by strategies.
     */

    private void removeOutOfBoundsBalls() {
        for (GameObject obj : gameObjects()) {
            if (("PuckBall".equals(obj.getTag()))) {
                //check if a ball got out of the screen (fell down)
                float y = obj.getCenter().y();
                if (y < 0 || y > windowDimensions.y()) {
                    gameObjects().removeGameObject(obj);
                }
            }
        }
    }


    /**
     * This function checks if the game ended by checking :
     * if the ball passed the floor and the lives ended, the Player lost
     * also, each time the green ball passes the floor, the lives counter is decreased by 1
     * if the last brick was hit by the ball , the player won.
     *in both cases, it asks the player if he wants to play again!
     */
    private void checkForGameEnd() {
        float ballHeight = ball.getCenter().y();
        String prompt = "";

        if (ballHeight > windowDimensions.y()) {
            hearts.loseLife();

            if (hearts.getLives() == 0) {
                prompt = "You Lose!";
            } else {
                ball.setCenter(windowDimensions.mult(0.5F));
            }
        }

        if (brickCounter.value() <= 0) {
            prompt = "You Win!";
        }

        if (!prompt.isEmpty()) {
            prompt += " Play again?";
            if (windowController.openYesNoDialog(prompt)) {
                windowController.resetGame();
            } else {
                windowController.closeWindow();
            }
        }
    }

    /**
     * the entry point of the program.
     * creates a BrickerGameManager instance based on the command-line arguments
     * and starts the game loop.
     * otherwise, the game starts with default settings.
     * @param args command-line arguments used to optionally configure the board size
     */
    public static void main(String[] args) {
    GameManager game;

    if (args.length == 3) {
        int bricksPerRow = Integer.parseInt(args[0]);
        int rows = Integer.parseInt(args[1]);

        game = new BrickerGameManager("Bouncing Ball",
                new Vector2(WIDTH, HEIGHT), rows, bricksPerRow);
    } else {
        game = new BrickerGameManager("Bouncing Ball",
                new Vector2(WIDTH, HEIGHT));
    }
    game.run();
    }
}