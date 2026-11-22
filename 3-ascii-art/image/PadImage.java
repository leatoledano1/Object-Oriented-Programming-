package image;

import java.awt.Color;

/**
 *a class for padding an image with white pixels so that
 * its width and height are transformed to powers of two
 * The original image is centered inside the padded image
 * symmetrically
 */
public class PadImage {
    /** Color used for padding (white). */
    private static final Color PADDING_COLOR = Color.WHITE;

    /**
     * pads the given image so that its width and height are powers of two
     * if a dimension is already a power of two it is left unchanged
     * @param src the original image
     * @return a new Image instance whose size is padded to powers of two
     */
    public static Image padToPowerOfTwo(Image src) {
        int origHeight = src.getHeight();
        int origWidth = src.getWidth();
        int newHeight = nextPowerOfTwo(origHeight);
        int newWidth = nextPowerOfTwo(origWidth);
        //no padding needed
        if (newHeight == origHeight && newWidth == origWidth) {
            return src;
        }
        //create new pixel array filled with white for padding
        Color[][] paddedPixels = new Color[newHeight][newWidth];
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                paddedPixels[i][j] = PADDING_COLOR;
            }
        }
        //center the original image inside the padded one
        int topOffset = (newHeight - origHeight) / 2;
        int leftOffset = (newWidth - origWidth) / 2;
        for (int i = 0; i < origHeight; i++) {
            for (int j = 0; j < origWidth; j++) {
                paddedPixels[i + topOffset][j + leftOffset] = src.getPixel(i, j);
            }
        }
        return new Image(paddedPixels, newWidth, newHeight);
    }

    /**
     * returns the smallest power of two that is bigger than n
     * @param n positive integer
     * @return next power of two greater than or equal to n
     */
    public static int nextPowerOfTwo(int n) {
        int p = 1;
        while (p < n) {
            p = p * 2;
        }
        return p;
    }
}
