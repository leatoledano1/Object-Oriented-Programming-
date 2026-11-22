package bricker.brick_strategies;

import danogl.collisions.GameObjectCollection;
import bricker.gameobjects.Brick;
import danogl.GameObject;
import danogl.gui.Sound;
import danogl.util.Counter;

/**
 * A special collision strategy that makes bricks “explode” when one of
 * his neighbor is hit.
 *only collisions with a Ball trigger this effect.
 */
public class ExplodeNeighborsStrategy extends BasicCollisionStrategy implements CollisionStrategy {

    private final Brick[][] brickArray;
    private final Sound explodeSound;

    /**
     * Constructs an ExplodeNeighborsStrategy.
     * @param gameObjects collection of game objects.
     * @param brickArray 2D array representing the brick grid (used for locating neighbors)
     * @param brickCounter counter tracking the number of remaining bricks.
     * @param explodeSound sounds effect to play when neighbors explodes
     */
    public ExplodeNeighborsStrategy(GameObjectCollection gameObjects,Brick[][] brickArray,
                                    Counter brickCounter, Sound explodeSound) {

        super(gameObjects, brickCounter);
        this.brickArray = brickArray;
        this.explodeSound = explodeSound;
    }

    /**
     * handles a collision with a brick.
     * only collisions with the balls
     * @param brick The brick that was hit
     * @param collider The colliding object
     */
    @Override
    public void onCollision(GameObject brick, GameObject collider) {
        if (!"Ball".equals(collider.getTag())&& !"PuckBall".equals(collider.getTag())) {
            return;
        }
        // Remove the brick itself (handled by parent strategy)
        super.onCollision(brick, collider);
        explodeSound.play();
        Brick b = (Brick) brick;
        int row = b.getRow();
        int col = b.getCol();
        brickArray[row][col] = null;
        // Attempt to explode neighbors
        explodeNeighbor(row - 1, col); // Up
        explodeNeighbor(row + 1, col); // Down
        explodeNeighbor(row, col - 1); // Left
        explodeNeighbor(row, col + 1); // Right
    }

    /**
     * Removes a neighbor brick if it exists and updates the counter
     */
    private void explodeNeighbor(int row, int col) {
        if (row < 0 || col < 0 || row >= brickArray.length || col >= brickArray[0].length)
            return;
        Brick neighbor = brickArray[row][col];
        if (neighbor == null) {
            return;
        }
        if(gameObjects.removeGameObject(neighbor)){
            brickArray[row][col] = null;
            brickCounter.decrement();
        }
    }
}
