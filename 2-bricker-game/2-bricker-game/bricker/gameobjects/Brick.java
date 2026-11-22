package bricker.gameobjects;

import bricker.brick_strategies.CollisionStrategy;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * represents a single brick in the game.
 * every brick holds its own collision strategy and knows its position
 * inside the brick grid (row and column).
 * when a brick is hit, it doesn’t decide the behavior by itself—
 * instead it forwards the event to its strategy.
 */
public class Brick extends GameObject {
    private final CollisionStrategy collisionStrategy;
    private final int row;
    private final int col;
    private final String BRICK = "Brick";

    /**
     * creates a new brick
     * @param topLeftCorner the initial position of the brick on screen
     * @param dimensions the size (width and height) of the brick
     * @param renderable the image used to draw the brick
     * @param collisionStrategy the strategy that defines what happens when this brick is hit
     * @param row the row index of the brick in the brick grid
     * @param col the column index of the brick in the brick grid
     */
    public Brick(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                 CollisionStrategy collisionStrategy, int row, int col) {
        super(topLeftCorner, dimensions, renderable);
        this.collisionStrategy = collisionStrategy;
        this.row = row;
        this.col = col;
        this.setTag(BRICK);
    }

    /**
     * getter for the row num
     * @return the row num
     */
    public int getRow() { return row; }

    /**
     * getter for the col num
     * @return the col num
     */
    public int getCol() { return col; }

    /**
     * a getter for the strategy chosen for the brick
     * @return the strategy
     */
    public CollisionStrategy getStrategy() { return collisionStrategy; }

    /**
     * @param other the object that hit the brick
     * @param collision information about the collision (position, normal, etc)
     * when the brick collides with something,
     * it simply forwards the event to its strategy,
     * which decides what actually happens.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        collisionStrategy.onCollision(this, other);
    }
}
