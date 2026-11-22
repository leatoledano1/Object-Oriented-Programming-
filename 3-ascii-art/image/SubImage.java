package image;

import java.awt.Color;

/**
 *represents a sub-image of the original image,
 * and provides a static method to divide an Image into
 * equally sized sub-images
 * A SubImage stores:
 *  - its own pixel matrix
 *  - width and height
 * The divide() method splits a padded image into
 * resolution×resolution sub-images of equal size.
 */
public class SubImage {

    private final Color[][] pixels;
    private final int width;
    private final int height;
    //stores brightness after first calculate
    private Double cachedBrightness = null;

    private static final double RED_FACTOR = 0.2126;
    private static final double GREEN_FACTOR = 0.7152;
    private static final double BLUE_FACTOR = 0.0722;
    private static final double MAX_RGB = 255.0;

    /**
     * creates a sub-image from a given pixel matrix
     * @param pixels 2D array of Color objects
     */
    public SubImage(Color[][] pixels) {
        this.pixels = pixels;
        this.height = pixels.length;
        this.width = pixels[0].length;
    }

    /** returns the width (number of columns) of this sub-image.
     * the width is determined by the number of pixels in each row
     * @return width of the sub-image in pixel
     */
    public int getWidth() {
        return width;
    }

    /**returns the height (number of rows) of this sub-image.
     * the height is determined by the number of pixel rows
     * @return height of the sub-image in pixel
     */
    public int getHeight() {
        return height;
    }

    /**returns the Color of the pixel at the given coordinates.
     * coordinates are given relative to this sub-image, where:
     *  - x is the row index
     *  - y is the column index
     * @param x row index inside the sub-image
     * @param y column index inside the sub-image
     * @return the Color of the requested pixel
     */
    public Color getPixel(int x, int y) {
        return pixels[x][y];
    }


    /**
     * Computes average brightness (0–1) of the sub-image.
     * Uses a cache to avoid recomputing brightness (requirement 1.5.2).
     *
     * @return brightness value in range 0–1.
     */
    public double computeBrightness() {
        // If brightness was computed before → return cached value
        if (cachedBrightness != null) {
            return cachedBrightness;
        }

        double sum = 0;
        int total = width * height;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color c = pixels[i][j];
                double greyPixel = RED_FACTOR * c.getRed()
                        + GREEN_FACTOR * c.getGreen()
                        + BLUE_FACTOR * c.getBlue();
                sum += greyPixel / MAX_RGB;
            }
        }

        // Save result so future calls are O(1)
        cachedBrightness = sum / total;
        return cachedBrightness;
    }

    /** the most important function in SubImage
     * splits a padded image into resolution×resolution sub-images.
     * Assumes the image dimensions are divisible by resolution
     * (guaranteed because of the padding to power of twos)
     * @param img is an already padded Image object
     * @param resolution number of blocks per dimension
     * @return 2D array of SubImage objects
     */
    public static SubImage[][] divide(Image img, int resolution) {
        int totalWidth = img.getWidth();
        int totalHeight = img.getHeight();

        int blockWidth = totalWidth / resolution;
        int blockHeight = totalHeight / resolution;

        SubImage[][] block = new SubImage[resolution][resolution];

        for (int row = 0; row < resolution; row++) {
            for (int col = 0; col < resolution; col++) {

                Color[][] subPixels = new Color[blockHeight][blockWidth];

                for (int i = 0; i < blockHeight; i++) {
                    for (int j = 0; j < blockWidth; j++) {

                        int origX = row * blockHeight + i;
                        int origY = col * blockWidth + j;

                        subPixels[i][j] = img.getPixel(origX, origY);
                    }
                }

                block[row][col] = new SubImage(subPixels);
            }
        }

        return block;
    }
}
