package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.function.Consumer;

/**represents a fruit object that can be collected by the avatar.
 * When eaten, the fruit becomes unavailable and can later be respawned.
 */
public class Fruit extends GameObject {
    /** Callback for when the fruit is eaten */
    private Consumer<Fruit> onEaten;
    /** Indicates whether the fruit is currently eatable */
    private boolean available = true;

    private static final String AVATAR_TAG = "Avatar";
    private static final String FRUIT_TAG = "Fruit";

    /**
     * Constructs a new Fruit object
     * @param topLeftCorner initial position of the fruit
     * @param size size of the fruit
     */
    public Fruit(Vector2 topLeftCorner, Vector2 size) {
        super(topLeftCorner, size, new OvalRenderable(Color.RED));
        setTag(FRUIT_TAG);
    }

    /**
     * setter for a callback to be invoked when the fruit is eaten
     * @param callback consumer that handles the eaten fruit
     */
    public void setOnEatenCallback(Consumer<Fruit> callback) {
        this.onEaten = callback;
    }

    /**
     * Respawns the fruit at the given position and size,
     * making it available again
     * @param position new position for the fruit
     * @param size new size of the fruit
     */
    public void respawn(Vector2 position, Vector2 size) {
        setTopLeftCorner(position);
        setDimensions(size);
        available = true;
    }

    /**
     * handles collisions with other game objects.
     * The fruit is eaten when colliding with the avatar
     * @param other the object collided with
     * @param collision collision information
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (!available) return;
        if (!AVATAR_TAG.equals(other.getTag())) return;
        available = false;
        setDimensions(Vector2.ZERO);

        if (onEaten != null) {
            onEaten.accept(this);
        }
    }
}
