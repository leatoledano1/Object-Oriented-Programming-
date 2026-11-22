package bricker.brick_strategies;

import bricker.gameobjects.Brick;
import bricker.gameobjects.Hearts;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import java.util.Random;

/**
 * a factory class responsible for creating collision strategies for bricks.
 * the factory knows all the shared game resources (images, sounds, counters, input...)
 * and uses them to build fully configured strategy objects.
 * bricks can either ask for a random strategy or request one by tag.
 */
public class FactoryOfStrategies {
    /** reference to the game's global GameObjectCollection, used for adding and removing objects */
    public final GameObjectCollection gameObjects;
    /** random generator used for choosing collision strategies */
    public final Random random = new Random();
    /** loads images needed by strategies*/
    public final ImageReader imageReader;
    /** loads sounds used by strategies (explosion, collisions) */
    public final SoundReader soundReader;
    /** listener for the keyboard input, needed for strategies that spawn controllable paddles */
    public final UserInputListener userInputListener;
    /** the dimensions of the game window, used for positioning spawned objects */
    public final Vector2 windowDimensions;
    /** counter tracking the player's remaining lives */
    public final Counter livesHeart;
    /** manager responsible for displaying heart icons and numeric lives */
    public final Hearts heartManager;
    /** global brick counter is decreased whenever a brick disappears */
    private final Counter brickCounter;
    /** array that store all bricks in the level, used mainly for explode strategy */
    private final Brick[][] brickArray;

    /**
     * creates a factory that knows how to build every collision strategy.
     *
     * @param gameObjects the game's global object collection
     * @param imageReader used to load images
     * @param soundReader used to load sound effects
     * @param userInputListener used by paddle strategies
     * @param windowDimensions the size of the game window
     * @param livesHeart counter of the player's lives
     * @param heartManager object responsible for visual heart display
     * @param brickCounter global counter of how many bricks remain
     * @param brickArray the 2d array containing all brick references
     */
    public FactoryOfStrategies(GameObjectCollection gameObjects, ImageReader imageReader,
                               SoundReader soundReader, UserInputListener userInputListener,
                               Vector2 windowDimensions, Counter livesHeart,
                               Hearts heartManager, Counter brickCounter, Brick[][] brickArray) {
        this.gameObjects = gameObjects;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        this.userInputListener = userInputListener;
        this.windowDimensions = windowDimensions;
        this.livesHeart = livesHeart;
        this.heartManager = heartManager;
        this.brickCounter = brickCounter;
        this.brickArray = brickArray;
    }

    /**
     * Randomly selects collision strategy based on given probabilities:
     * 50% basic, 10% puck, 10% extra paddle, 10% turbo, 10% extra heart, 10% multiple.
     */
    public CollisionStrategy chooseStrategy() {

        Renderable paddleImage = imageReader.readImage("assets/paddle.png", true);
        Sound explosionSound = soundReader.readSound("assets/explosion.wav");

        //int r = 9;
        int r = random.nextInt(10) + 1;
        if (r <= 5) {
            return new BasicCollisionStrategy(gameObjects, brickCounter);
        }
        if (r == 6) {
            return new AddPucksStrategy(gameObjects, brickCounter, imageReader, soundReader);
        }
        if (r == 7) {
            return new ExtraPaddleStrategy(gameObjects, paddleImage, userInputListener, windowDimensions,
                    brickCounter);
        }
        if (r == 8) {
            return new ExplodeNeighborsStrategy(gameObjects, brickArray, brickCounter, explosionSound);
        }
        if (r == 9) {
            return new HeartCollisionStrategy(gameObjects, imageReader, heartManager, windowDimensions,
                    brickCounter);
        }
        return new MultipleStrategy(gameObjects, brickCounter, this);
    }

    /**
     * builds a strategy directly by tag name.
     * useful when MultipleStrategy wants to request a specific strategy type.
     * if the tag does not match anything known, falls back to a random pick
     * @param tag name of the wanted strategy ("Basic", "AddPuckBallStrategy",...)
     * @return the matching strategy or a random strategy if the tag is not known
     */
    public CollisionStrategy chooseStrategyByTag(String tag) {
        if("Basic".equals(tag)) {
            return new BasicCollisionStrategy(gameObjects, brickCounter);
        }
        if ("ExtraPaddleStrategy".equals(tag)) {
            return new ExtraPaddleStrategy( gameObjects,
                    imageReader.readImage("assets/paddle.png", true),
                    userInputListener, windowDimensions, brickCounter);
        }
        if ("ExtraHeartStrategy".equals(tag)) {
            return new HeartCollisionStrategy(gameObjects, imageReader, heartManager,
                    windowDimensions, brickCounter );
        }
        if ("ExplodeNeighborStrategy".equals(tag)) {
            return new ExplodeNeighborsStrategy(gameObjects,brickArray,
                    brickCounter, soundReader.readSound("assets/explosion.wav"));
        }
        if ("AddPuckBallStrategy".equals(tag)) {
            return new AddPucksStrategy(gameObjects, brickCounter,
                    imageReader,  soundReader );
        }
        if ("MultipleStrategy".equals(tag)) {
            return new MultipleStrategy(gameObjects, brickCounter, this);
        }
        return chooseStrategy();
    }

}
