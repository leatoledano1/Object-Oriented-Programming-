package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/** Sun class is responsible for creating the sun in the game
 * The sun moves in a circular path to simulate the day cycle
 */
public class Sun {
    /** tag assigned to the sun */
    private static final String SUN_TAG = "Sun";
    /** The sun size */
    private static final Vector2 SUN_SIZE = new Vector2(50, 50);
    /**factor for the cycle center according to window dim*/
    private static final float HALF = 0.5f;
    private static final Vector2 INITIAL_POS = new Vector2(300, 0);
    private static final float INITIAL_VALUE =  0f;
    private static final float FINAL_VALUE =  365f;

    /** Creates a sun GameObject that moves in a circular path over time
     * @param windowDimensions the dimensions of the game window
     * @param cycleLength the duration of a full day cycle
     * @return a GameObject representing the sun
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        GameObject sun = new GameObject(Vector2.ZERO, SUN_SIZE,new OvalRenderable(Color.YELLOW));

        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);
        Vector2 cycleCenter = new Vector2(windowDimensions.x()*HALF,windowDimensions.y()*HALF);

        Vector2 initialSunCenter = cycleCenter.add(INITIAL_POS);
        sun.setCenter(initialSunCenter);
        new Transition<>(sun,
                (Float angle) -> sun.setCenter(initialSunCenter.subtract(cycleCenter)
                        .rotated(angle).add(cycleCenter)),
                INITIAL_VALUE,FINAL_VALUE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,null );

        return sun;
    }
}
