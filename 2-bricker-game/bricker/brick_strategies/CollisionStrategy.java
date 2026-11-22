package bricker.brick_strategies;

import danogl.GameObject;

/**
 * an interface that all the collision strats
 * implement
 * each of them are obligated to implement the onCollision func.
 */
public interface CollisionStrategy {
    /**
     * called whenever a brick collides with another game object.
     * @param one the brick involved in the collision
     * @param two the object that hit the brick
     */
    void onCollision(GameObject one, GameObject two);
}
