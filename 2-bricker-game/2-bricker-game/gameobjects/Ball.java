package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * represents the main ball of the game.
 * the ball moves around the screen, bounces off objects it is supposed to,
 * and is responsible for breaking bricks when collisions occur.
 * the class stores its collision sound and assigns the "Ball" tag
 * so other objects can recognize it during collisions.
 */
public class Ball extends GameObject {
    private final Sound collisionSound;
    private final String BALL = "Ball";

    /**
     * @param topLeftCorner the initial top left position for the ball
     * @param dimensions  The size (width and height) of the ball
     * @param renderable the image of the ball (picture file)
     * @param collisionSound the sound effect played whenever the ball collides with another game object.
     */
    public Ball(Vector2 topLeftCorner, Vector2 dimensions,
                Renderable renderable, Sound collisionSound) {
        super(topLeftCorner, dimensions, renderable);
        this.collisionSound = collisionSound;
        this.setTag(BALL);
    }

    /**
     * called automatically whenever the ball collides with another game object
     * @param other the object the ball collided with.
     * @param collision what happens when the collision happens
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        Vector2 newVel = getVelocity().flipped(collision.getNormal());
        setVelocity(newVel);
        collisionSound.play();
    }
}
