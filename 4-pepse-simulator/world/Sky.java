package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**Sky class responsible for creating the sky background of the game.
 * the sky is a static GameObject rendered in camera coordinates so it
 * always fills the screen.
 */
public class Sky {
    /** default sky color */
    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");
    /** Tag assigned to the sky GameObject */
    private static final String SKY_TAG = "sky";

    /** Creates and returns a sky GameObject that covers the entire window
     * @param windowDimensions the dimensions of our game window
     * @return a GameObject representing the sky
     */
    public static GameObject create(Vector2 windowDimensions) {
        GameObject sky = new GameObject(Vector2.ZERO, windowDimensions,
                new RectangleRenderable(BASIC_SKY_COLOR));
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sky.setTag(SKY_TAG);
        return sky;
    }
}
