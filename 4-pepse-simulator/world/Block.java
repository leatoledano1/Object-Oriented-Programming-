package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * The Block class represents a single terrain block in the game world
 * a bock is a squared GameObject that will be a part of the terrain,
 * Blocks do not move, are affected by collisions,
 * and are immovable objects
 */
public class Block extends GameObject {
    /**the size of a block */
    public static final int SIZE = 30;

    /**
     * constructor for a new Block at the given top-left position, using the given color renderable.
     * @param topLeftCorner The top-left corner position of the block in window coordinates
     * @param renderable The visual representation of the block for now it's just a Color
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        setTag("Block");
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }
}
