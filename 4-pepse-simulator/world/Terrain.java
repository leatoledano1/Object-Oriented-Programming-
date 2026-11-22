package pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.utils.NoiseGenerator;
import pepse.utils.ColorSupplier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**The Terrain class is responsible for generating the ground of the game world
 * NOT a GameObject, it serves as a calculator for the height of the ground at all x position
 * and creates the corresponding Block game objects.
 * The terrain height is based on:
 * 1. A fixed base height (2/3 window height)
 * 2. A NoiseGenerator object that chooses semi-random number
 */
public class Terrain {
    private static final int SCALE_FACTOR = 7;
    private static final int TERRAIN_DEPTH = 20;

    private final float STARTING_GROUND_HEIGHT =2f/3f;
    private final int BLOCK_SIZE= Block.SIZE;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);

    private final float groundHeightAtX0;
    private final Vector2 windowDimensions ;

    /**a Noise generator used to create smooth height changes along the terrain*/
    private final NoiseGenerator noise;

    /**
     * constructor for a Terrain generator for the given window size and random seed
     * @param windowDimensions The dimensions of the game window
     * @param seed a value used by the NoiseGenerator
     */
    public Terrain(Vector2 windowDimensions, int seed){
        this.windowDimensions = windowDimensions;
        this.groundHeightAtX0 = STARTING_GROUND_HEIGHT * (int) windowDimensions.y();
        this.noise=new NoiseGenerator(seed,(int) groundHeightAtX0);
    }

    /**
     * Calculates the height of the ground at a given position on the x axe.
     * the height is computed as the base ground height plus a semi-random NoiseGenerator.
     * @param x The x position to calculate the ground height (Y).
     * @return The y position of the ground at x.
     */
    public float groundHeightAt(float x){
        float noiseValue = (float) noise.noise(x, BLOCK_SIZE * SCALE_FACTOR);
        return groundHeightAtX0 + noiseValue;
    }

    /**creates terrain blocks within the given y range
     * @param minX The minimum x coordinate of the range to generate terrain for.
     * @param maxX The maximum x coordinate of the range to generate terrain for.
     * @return a list of Block objects representing the terrain in the given range
     */
    public List<Block> createInRange(int minX, int maxX) {
        List<Block> blocks = new ArrayList<>();
        int startX = (int) Math.floor((double) minX / Block.SIZE) * Block.SIZE;
        int endX = (int) Math.ceil((double) maxX / Block.SIZE) * Block.SIZE;

        for (int x = startX; x <= endX; x += Block.SIZE) {
            int groundY = (int) Math.floor(groundHeightAt(x) / Block.SIZE) * Block.SIZE;

            for (int j = 0; j < TERRAIN_DEPTH; j++) {
                Renderable blockRenderable =
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                int y = groundY + (j * Block.SIZE);
                Vector2 topLeftCorner = new Vector2(x, y);
                Block block = new Block(topLeftCorner, blockRenderable);
                blocks.add(block);
            }
        }
        return blocks;
    }
}
