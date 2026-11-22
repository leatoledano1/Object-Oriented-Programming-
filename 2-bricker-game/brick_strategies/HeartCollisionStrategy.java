package bricker.brick_strategies;

import bricker.gameobjects.ExtraHeart;
import bricker.gameobjects.Hearts;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.util.Counter;
import danogl.util.Vector2;

import static bricker.main.BrickerGameManager.HEART_DIM;

/**
 * a collision strategy that causes a brick to drop a falling heart when hit
 * by the main Ball. The heart can be collected by the MAIN paddle to gain a life.
 */
public class HeartCollisionStrategy implements CollisionStrategy {
    private final GameObjectCollection gameObjects;
    private final ImageReader imageReader;
    private final Hearts livesDisplay;
    private final Vector2 windowDimensions;
    private Counter brickCounter;

    /**
     * Constructs a new AddPucksStrategy.
     *
     * @param gameObjects the game's object collection
     * @param imageReader used to load puck images
     * @param livesDisplay num of lives
     * @param windowDimensions = game window dimensions
     * @param brickCounter global brick counter to update when the brick disappears
     */
    public HeartCollisionStrategy(GameObjectCollection gameObjects, ImageReader imageReader,
                                  Hearts livesDisplay, Vector2 windowDimensions, Counter brickCounter) {
        this.gameObjects = gameObjects;
        this.imageReader = imageReader;
        this.livesDisplay = livesDisplay;
        this.windowDimensions = windowDimensions;
        this.brickCounter = brickCounter;
    }

    /**
     * triggered when a brick using this strategy collides with another object
     * @param brick the brick that was hit.
     * @param collider the object that collided with the brick.
     */
    @Override
    public void onCollision(GameObject brick, GameObject collider) {
        if (!"Ball".equals(collider.getTag())&& !"PuckBall".equals(collider.getTag())) {
            return;
        }
        if(gameObjects.removeGameObject(brick)){
            brickCounter.decrement();
        };
        // Always spawn exactly one falling heart
        spawnHeart(brick.getCenter());

    }

    /**
     * creates a falling heart at given position and adds it to the game
     *
     * @param position the location where the heart should appear
     */    private void spawnHeart(Vector2 position) {
        ExtraHeart heart = new ExtraHeart( position, HEART_DIM,
                imageReader.readImage("assets/heart.png", true),
                gameObjects, livesDisplay, windowDimensions);
        gameObjects.addGameObject(heart);
    }
}
