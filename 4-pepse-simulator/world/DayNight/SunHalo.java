package pepse.world.daynight;

import danogl.GameObject;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.Color;

/**
 * Utility class responsible for creating a halo effect around the sun.
 * The halo follows the sun's position to create a glowing effect.
 */
public class SunHalo {

    /** Tag assigned to the sun halo GameObject */
    private static final String SUN_HALO_TAG = "SunHalo";
    private static final Color SUN_HALO_COLOR = new Color(255, 255, 0, 20);
    private static final float SIZE_FACTOR = 1.5f;

    /**
     * Creates a halo GameObject that follows the given sun.
     *
     * @param sun the sun GameObject to follow
     * @return a GameObject representing the sun halo
     */
    public static GameObject create(GameObject sun) {
        // sun halo size
        Vector2 sunHaloSize = sun.getDimensions().mult(SIZE_FACTOR);
        // yellow halo with transparency
        OvalRenderable renderable = new OvalRenderable(SUN_HALO_COLOR);
        // create sun halo
        GameObject sunHalo = new GameObject(Vector2.ZERO,sunHaloSize,renderable);
        sunHalo.setCoordinateSpace(sun.getCoordinateSpace());
        // tag
        sunHalo.setTag(SUN_HALO_TAG);
        // initial position
        sunHalo.setCenter(sun.getCenter());
        // follow the sun
        sunHalo.addComponent(deltaTime ->sunHalo.setCenter(sun.getCenter()));
        return sunHalo;
    }
}
