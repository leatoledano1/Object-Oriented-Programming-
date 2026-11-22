package ascii_art;

import image.Image;
import image.SubImage;
import image_char_matching.SubImgCharMatcher;

/**run the ASCII-art algorithm on a padded image
 * splits the image in sub-images, calculates brightness,
 * and maps each brightness value to a char
 */
public class AsciiArtAlgorithm {
    private final Image img;
    private final int resolution;
    private final SubImgCharMatcher matcher;
    private final boolean reverseMode;

    /**constructor for the algorithm runner
     * @param img padded image to process
     * @param resolution number of blocks per dimension
     * @param matcher character to brightness matcher
     * @param reverseMode a boolean param for reverse mode activation
     */
    public AsciiArtAlgorithm(Image img, int resolution, SubImgCharMatcher matcher, boolean reverseMode) {
        this.img = img;
        this.resolution = resolution;
        this.matcher = matcher;
        this.reverseMode = reverseMode;
    }

    /**handles the ASCII-art generation
     * @return array of chars representing the ASCII art
     */
    public char[][] run() {
        SubImage[][] blocks = SubImage.divide(img, resolution);

        int rows = resolution;
        int cols = resolution;

        char[][] result = new char[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double brightness = blocks[r][c].computeBrightness();

                if(reverseMode){
                    brightness = 1-brightness;
                }
                char mapped = matcher.getCharByImageBrightness(brightness);
                result[r][c] = mapped;
            }
        }

        return result;
    }
    }

