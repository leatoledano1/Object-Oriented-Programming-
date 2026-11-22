package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.Color;

/**a class responsible for creating the night effect in the game.
 * The night is implemented as a semi-transparent layer that fades
 * in and out over time.
 */
public class Night {
    /** Tag assigned to the night GameObject */
    private static final String NIGHT_TAG = "Night";
    /** Maximum opacity value reached at midnight */
    private static final float MIDNIGHT_OPACITY = 0.5f;
    /**how transparent is the color */
    private static final float INITIAL_VALUE = 0f;
    private static final Color TRANSPARENT = new Color(0, 0, 0, 1f);

    /**
     * Creates a night GameObject that gradually changes its opacity
     * to simulate a day-night cycle
     * @param windowDimensions the dimensions of the game window
     * @param cycleLength the duration of a full day-night cycle
     * @return a GameObject representing the night overlay
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        RectangleRenderable renderable = new RectangleRenderable(TRANSPARENT);
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions, renderable);
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag(NIGHT_TAG);

        new Transition<>(night, night.renderer()::setOpaqueness,INITIAL_VALUE,MIDNIGHT_OPACITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT,cycleLength,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,null);

        return night;
    }
}
