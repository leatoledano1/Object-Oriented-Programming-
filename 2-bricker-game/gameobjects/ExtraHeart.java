package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * represents a heart looking objects that gets released from a
 * brick with a special collision strategy
 * only collides with the paddle gifting the user an extra life
 */
public class ExtraHeart extends GameObject {
    private final Hearts lifeManager;
    private final GameObjectCollection gameObjects;
    private final Vector2 windowDimensions;

    /**constructor for the extra heart
     *
     * @param topLeft appearance position
     * @param dimensions heart size
     * @param renderable heart pic-same as regular heart
     * @param gameObjects a reference to the collection so the heart can remove
     *                    himself from the object after collision
     * @param lifeManager a reference to the actual lives class
     * @param windowDimensions the window dims
     */
    public ExtraHeart(Vector2 topLeft, Vector2 dimensions,Renderable renderable,
                      GameObjectCollection gameObjects, Hearts lifeManager,
                      Vector2 windowDimensions) {
        super(topLeft, dimensions, renderable);
        this.windowDimensions = windowDimensions;
        this.lifeManager = lifeManager;
        this.gameObjects = gameObjects;
        setVelocity(new Vector2(0, 100));
    }

    /**
     * The extra heart should Only collide with the paddle
     * @param other is the  object the heart collides with
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        return "Paddle".equals(other.getTag());
    }

    /**
     * Handles collisions
     * the heart should only react to collisions with the paddle.
     * When collected, it grants exactly one life and removes itself from the game.
     * @param other the colliding object
     * @param collision what's happening
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        if ("Paddle".equals(other.getTag())) {
            lifeManager.gainLife();
            gameObjects.removeGameObject(this);
        }
    }

    /**
     * updates the heart each frame
     * if the heart falls below the bottom of the screen without being collected
     * it removes itself from the game
     * @param deltaTime time passed since the last frame.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (getTopLeftCorner().y() > windowDimensions.y()) {
            gameObjects.removeGameObject(this);
        }
    }
}
