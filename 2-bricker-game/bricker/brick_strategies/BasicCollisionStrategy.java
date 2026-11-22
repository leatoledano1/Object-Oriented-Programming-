package bricker.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.util.Counter;

/**
 * A basic collision strategy for a brick.
 * this strategy removes the brick when it is hit by the ball (or by a puck ball),
 * and decrements the global brick counter that tracks how many bricks remain
 * does not trigger special effects, this is the simplest behavior.
 */
public class BasicCollisionStrategy  implements CollisionStrategy {
    GameObjectCollection gameObjects;
    Counter brickCounter;
    /**
     * constructor for a BasicCollisionStrategy
     * @param gameObjects the game's global object collection, is used to remove the brick
     * @param brickCounter A counter to how many bricks remain
     */
    public BasicCollisionStrategy(GameObjectCollection gameObjects, Counter brickCounter) {
        this.gameObjects = gameObjects;
        this.brickCounter = brickCounter;
    }

    /**
     * Called whenever a brick collides with another object
     * If the collider is a Ball or a PuckBall, the brick is removed
     * and the brick counter is updated
     * @param brick the brick being collided with.
     * @param collider The object that hit the brick (Ball or PuckBall).
     */
    @Override
    public void onCollision(GameObject brick, GameObject collider) {
        if (collider == null)
            return;

        if ("Ball".equals(collider.getTag()) || "PuckBall".equals(collider.getTag())) {
            if(gameObjects.removeGameObject(brick)){
                brickCounter.decrement();
            }
        }
    }

}

