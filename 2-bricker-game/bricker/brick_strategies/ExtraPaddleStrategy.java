package bricker.brick_strategies;

import bricker.gameobjects.ExtraPaddle;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

import static bricker.main.BrickerGameManager.PADDLE_SIZE;

/**
 *a collision strategy that spawns an extra paddle
 * the first time the brick is hit by the main Ball.
 * Only one extra paddle is allowed at a time
 * The extra paddle removes itself after 4 green Ball hits
 */
public class ExtraPaddleStrategy implements CollisionStrategy {
    private final GameObjectCollection gameObjects;
    private final Renderable paddleImage;
    private final UserInputListener inputListener;
    private final Vector2 windowDimensions;
    private final Counter brickCounter;

    /**
     * constructor for a new ExtraPaddleStrategy
     * @param gameObjects the game object collection.
     * @param paddleImage renderable image for the extra paddle.
     * @param inputListener the input listener used to control the paddle.
     * @param windowDimensions The dimensions of the game window.
     * @param brickCounter global brick counter to update when the brick disappears
     */
    public ExtraPaddleStrategy(GameObjectCollection gameObjects, Renderable paddleImage,
                               UserInputListener inputListener, Vector2 windowDimensions,
                               Counter brickCounter) {

        this.gameObjects = gameObjects;
        this.paddleImage = paddleImage;
        this.inputListener = inputListener;
        this.windowDimensions = windowDimensions;
        this.brickCounter = brickCounter;
    }

    /**
     * creates and positions a new extra paddle at the requested location.
     *
     * @param center the position where the paddle should appear
     * @return the newly created extra paddle
     */
    private ExtraPaddle createExtra(Vector2 center) {
        ExtraPaddle extraPaddle = new ExtraPaddle(Vector2.ZERO, PADDLE_SIZE, paddleImage,inputListener,
                windowDimensions, gameObjects);
        extraPaddle.setTag("ExtraPaddle");
        extraPaddle.setCenter(center);
        return extraPaddle;
    }

     /**
     * Called when a brick using this strategy is collided into.
     * If the collider is the main Ball, the brick is removed and an extra paddle
     * is created—unless one already exists.
     *
     * @param brick the brick that was hit.
     * @param collider The object that collided with the brick
     */
    @Override
    public void onCollision(GameObject brick, GameObject collider) {
        Vector2 center = new Vector2(windowDimensions.x() /2 , windowDimensions.y() /2);
        // Only main ball spawns an extra paddle
        if (!"Ball".equals(collider.getTag())) {
            return;
        }
        // Remove the brick
        if(gameObjects.removeGameObject(brick)){
            brickCounter.decrement();
        }

        // If extra paddle is already active — do nothing
        for(GameObject gameObject : gameObjects) {
            if("ExtraPaddle".equals(gameObject.getTag())) {
                return;
            }
        }
        // Otherwise add a new one
        ExtraPaddle extra = createExtra(center);
        gameObjects.addGameObject(extra);
    }
}