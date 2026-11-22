package image_char_matching;

import java.util.*;
/**
 * SubImgCharMatcher class maps characters to brightness values in the range 0–1.
 * brightness is computed from the boolean-matrix of each char.
 * the brightness values are normalized, so that the darkest char gets 0
 * and the brightest gets 1. all the values in between scale linearly
 */
public class SubImgCharMatcher {
    /** maps normalized brightness(key) ,set of chars having that brightness(value) */
    private final TreeMap<Double, TreeSet<Character>> brightnessMap;
    /** maps each character(key) ,(value=) its normalized brightness(0–1) */
    private final HashMap<Character, Double> reverseMap;
    /** stores raw brightness (before normalize). used to avoid recalculating */
    private final HashMap<Character, Double> rawBrightness;
    private static final double DEFAULT_NORM  = 0.5;

    /**
     * constructor for matcher from given charset.
     * brightness for every char is computed and stored in rawBrightness,
     * then a single normalization is done for the whole group
     * @param charset initial set of chars to include in the matcher
     */
    public SubImgCharMatcher(char[] charset) {
        brightnessMap = new TreeMap<>();
        reverseMap = new HashMap<>();
        rawBrightness = new HashMap<>();

        //compute raw brightness for each char once
        for (char c : charset) {
            if (!rawBrightness.containsKey(c)) {
                boolean[][] raster = CharConverter.convertToBoolArray(c);
                double raw = computeBrightness(raster);
                rawBrightness.put(c, raw);
                reverseMap.put(c, raw); // will be normalized later
            }
        }

        // at this point brightnessMap contains raw values and normalize everything
        normalizeBrightnessValues();
    }

    /**
     * returns the character whose normalized brightness is closest to the given brightness
     * uses floorEntry/ceilingEntry from TreeMap to find nearest neighbor
     * @param brightness normalized brightness value (0–1)
     * @return closest matching character. if empty, return space
     */
    public char getCharByImageBrightness(double brightness) {
        if (brightnessMap.isEmpty()) return ' ';

        Map.Entry<Double, TreeSet<Character>> lower = brightnessMap.floorEntry(brightness);
        Map.Entry<Double, TreeSet<Character>> higher = brightnessMap.ceilingEntry(brightness);

        // we can assume that if one of them doesn't exist
        //the other one must exist so we can do:
        if (lower == null) return higher.getValue().first();
        if (higher == null) return lower.getValue().first();

        double diffLow = Math.abs(lower.getKey() - brightness);
        double diffHigh = Math.abs(higher.getKey() - brightness);

        return (diffLow <= diffHigh)
                ? lower.getValue().first()
                : higher.getValue().first();
    }

    /**
     * adds a new char to the matcher,
     * calculates its raw brightness, stores it, and triggers a full normalization
     * @param c the character to add. if it already exists — nothing happens.
     */
    public void addChar(char c) {
        if (rawBrightness.containsKey(c)) return;
        // compute raw brightness once
        boolean[][] raster = CharConverter.convertToBoolArray(c);
        double raw = computeBrightness(raster);
        rawBrightness.put(c, raw);
        reverseMap.put(c, raw); // will be normalized

        normalizeBrightnessValues();
    }

    /**
     * removes a character completely from all mappings
     * afterward we normalize whatever stays.
     * @param c character to remove
     */
    public void removeChar(char c) {
        if (!rawBrightness.containsKey(c)) return;

        rawBrightness.remove(c);
        reverseMap.remove(c);

        // remove from brightnessMap (but since keys change after normalize, we rebuild anyway)
        normalizeBrightnessValues();
    }

    /**
     * computes raw brightness (before normalization)
     * simply counts percentage of white pixels in raster.
     */
    private double computeBrightness(boolean[][] raster) {
        int height = raster.length;
        int width = raster[0].length;
        int total = height * width;
        int white = 0;

        for (boolean[] row : raster) {
            for (boolean pixel : row) {
                if (pixel) white++;
            }
        }
        return (double) white / total;
    }

    /** @return sorted set of all chars currently in the matcher
     */
    public TreeSet<Character> getCharSet() {
        return new TreeSet<>(reverseMap.keySet());
    }

    /**
     * normalize all brightness values from rawBrightness to a double
     * from 0 to 1,
     * rebuild brightnessMap so keys are normalized brightnesses
     * if all brightnesses identical, everything gets 0.5.
     */
    private void normalizeBrightnessValues() {
        brightnessMap.clear();

        if (rawBrightness.isEmpty()) return;

        double min = Collections.min(rawBrightness.values());
        double max = Collections.max(rawBrightness.values());

        // avoid division by zero if all are equal
        if (min == max) {
            TreeSet<Character> all = new TreeSet<>(rawBrightness.keySet());
            brightnessMap.put(DEFAULT_NORM, all);
            for (char c : all) reverseMap.put(c, DEFAULT_NORM);
            return;
        }

        for (var entry : rawBrightness.entrySet()) {
            char c = entry.getKey();
            double raw = entry.getValue();
            double norm = (raw - min) / (max - min);

            reverseMap.put(c, norm);
            brightnessMap.computeIfAbsent(norm, k -> new TreeSet<>()).add(c);
        }
    }
}
