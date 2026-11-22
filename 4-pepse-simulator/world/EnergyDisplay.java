package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**displays the avatar's energy as a horizontal green bar on screen
 * The bar changes its own width according to the current energy value.
 */
public class EnergyDisplay {
    /** width of the energy bar */
    private static final float BAR_WIDTH = 120f;
    /** height of the energy bar - doesn't change*/
    private static final float BAR_HEIGHT = 15f;
    /** normalization factor for energy values*/
    private static final float FACTOR = 100f;
    /**Minimum width of the energy bar */
    private static final float MIN_WIDTH = 1f;
    /** The GameObject representing the energy bar */
    private final GameObject bar;;

    /**constructor for a new energy display at the given screen position
     * @param position the position of the bar on the screen
     */
    public EnergyDisplay(Vector2 position) {
        bar=new GameObject(position,new Vector2(BAR_WIDTH, BAR_HEIGHT),
                new RectangleRenderable(Color.GREEN));
        bar.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
    }

    /**updates the energy bar according to the given energy value
     * @param energy the energy value
     */
    public void updateEnergy(float energy) {
        float ratio = energy / FACTOR;
        float width = Math.max(MIN_WIDTH , BAR_WIDTH * ratio);
        bar.setDimensions(new Vector2(width, BAR_HEIGHT));
    }

    /**returns the GameObject of the energy bar
     * @return the energy bar GameObject
     */
    public GameObject getGameObject() {
        return bar;
    }
}
