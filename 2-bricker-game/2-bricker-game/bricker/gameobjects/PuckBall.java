package bricker.gameobjects;

import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * The puck ball objects class, is basically a Ball (beside looks)
 * only is different in his collision strategies so this class helps us
 * difference them. (different tags)
 */
public class PuckBall extends Ball {
    private final String PUCK_BALL_TAG = "PuckBall" ;

    /**
     * @param topLeftCorner the initial top left position for the ball
     * @param dimensions  The size (width and height) of the ball
     * @param renderable the image of the ball (picture file)
     * @param collisionSound the sound effect played whenever the ball collides with another game object.
     */
    public PuckBall(Vector2 topLeftCorner, Vector2 dimensions,
                    Renderable renderable, Sound collisionSound) {
        super(topLeftCorner, dimensions, renderable, collisionSound);
        this.setTag(PUCK_BALL_TAG);
    }
}
