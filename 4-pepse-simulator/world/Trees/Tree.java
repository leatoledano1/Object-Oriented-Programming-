package pepse.world.trees;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.utils.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Responsible for creating a single tree, including its trunk, leaves,
 * fruits, and leaf animations.
 */
public class Tree {

    private static final int BLOCK_SIZE = Block.SIZE;

    private static final int MIN_TRUNK_HEIGHT = 3;
    private static final int MAX_ADDITIONAL_TRUNK_HEIGHT = 3;

    private static final float LEAF_PLACEMENT_PROBABILITY = 0.7f;
    private static final int MIN_LEAF_RADIUS = 1;
    private static final int MAX_ADDITIONAL_LEAF_RADIUS = 2;

    private static final float FRUIT_PROBABILITY = 0.3f;
    private static final float MULT_FACTOR = 1.1f;
    private static final Vector2 FRUIT_SIZE = new Vector2(15, 15);

    private static final Color TRUNK_COLOR = new Color(100, 50, 20);
    private static final Color LEAF_COLOR  = new Color(50, 200, 30);

    private static final String TRUNK_TAG = "Trunk";
    private static final String LEAF_TAG  = "Leaf";

    private static final float MIN_SWAY_AMPLITUDE = 10f;
    private static final float MAX_SWAY_AMPLITUDE = 20f;
    private static final float MIN_SWAY_DURATION = 1.5f;
    private static final float MAX_SWAY_DURATION = 3f;

    /** Random generator used for tree variation */
    private final Random rand;

    /** Provides the ground height for a given x-coordinate */
    private final Function<Float, Float> groundHeightProvider;

    /** Callback invoked when a fruit is eaten */
    private final Consumer<Fruit> onFruitEaten;

    /**constructs a Tree generator
     * @param groundHeightProvider function that provides ground height
     * @param onFruitEaten callback for fruit consumption
     * @param seed random seed for tree generation
     */
    public Tree(Function<Float, Float> groundHeightProvider,
                Consumer<Fruit> onFruitEaten,long seed) {
        this.rand = new Random(seed);
        this.groundHeightProvider = groundHeightProvider;
        this.onFruitEaten = onFruitEaten;
    }

    /**
     * Creates a tree at the given x-coordinate
     * @param x horizontal position of the tree
     * @param fruitsOut list to store created fruit metadata
     * @return list of GameObjects composing the tree
     */
    public List<GameObject> createTreeAt(int x, List<Flora.FruitData> fruitsOut) {
        List<GameObject> objects = new ArrayList<>();

        int groundY = (int) Math.floor(groundHeightProvider.apply((float) x) / BLOCK_SIZE)* BLOCK_SIZE;

        int trunkHeight = MIN_TRUNK_HEIGHT + rand.nextInt(MAX_ADDITIONAL_TRUNK_HEIGHT + 1);

        objects.addAll(createTrunk(x, groundY, trunkHeight));
        objects.addAll(createLeavesAndFruits(x, groundY, trunkHeight, fruitsOut));
        return objects;
    }

    /**
     * Creates the trunk blocks of the tree
     * @param x x-coordinate of the trunk
     * @param groundY ground y-coordinate
     * @param heightBlocks trunk height in blocks
     * @return list of trunk GameObjects
     */
    private List<GameObject> createTrunk(int x, int groundY, int heightBlocks) {
        List<GameObject> trunks = new ArrayList<>();

        for (int i = 0; i < heightBlocks; i++) {
            Vector2 pos = new Vector2(x,groundY - (i + 1) * BLOCK_SIZE);

            GameObject trunk = new GameObject(pos, new Vector2(BLOCK_SIZE, BLOCK_SIZE),
                    new RectangleRenderable(ColorSupplier.approximateColor(TRUNK_COLOR)));

            trunk.setTag(TRUNK_TAG);
            trunk.physics().preventIntersectionsFromDirection(Vector2.ZERO);
            trunk.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);

            trunks.add(trunk);
        }
        return trunks;
    }

    /**creates leaves and fruits around the top of the trunk.
     * @param x x-coordinate of the tree
     * @param groundY ground y-coordinate
     * @param trunkHeightBlocks trunk height in blocks
     * @param fruitsOut list to store fruit metadata
     * @return list of leaf and fruit GameObjects
     */
    private List<GameObject> createLeavesAndFruits(int x,int groundY, int trunkHeightBlocks,
                                                   List<Flora.FruitData> fruitsOut) {
        List<GameObject> objects = new ArrayList<>();
        float leafBaseY = groundY - (trunkHeightBlocks + 1) * Block.SIZE;
        int leafRadius = MIN_LEAF_RADIUS + rand.nextInt(MAX_ADDITIONAL_LEAF_RADIUS + 1);

        for (int dx = -leafRadius; dx <= leafRadius; dx++) {
            for (int dy = -leafRadius; dy <= 0; dy++) {

                if (rand.nextFloat() >= LEAF_PLACEMENT_PROBABILITY) continue;

                Vector2 pos = new Vector2(x + dx * Block.SIZE,leafBaseY + dy * Block.SIZE);

                GameObject leaf = new GameObject(pos, new Vector2(Block.SIZE, Block.SIZE),
                        new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR)));
                leaf.setTag(LEAF_TAG);
                animateLeaf(leaf);
                leaf.physics().preventIntersectionsFromDirection(null);
                objects.add(leaf);

                if (rand.nextFloat() < FRUIT_PROBABILITY) {
                    Fruit fruit = new Fruit(pos, FRUIT_SIZE);
                    fruit.setOnEatenCallback(onFruitEaten);
                    objects.add(fruit);
                    fruitsOut.add(new Flora.FruitData(fruit, pos, FRUIT_SIZE));
                }
            }
        }
        return objects;
    }

    /**
     * Adds a swaying animation effect to a leaf
     * @param leaf the leaf GameObject to animate
     */
    private void animateLeaf(GameObject leaf) {
        float delay = rand.nextFloat();
        float amplitude =MIN_SWAY_AMPLITUDE + rand.nextFloat() *(MAX_SWAY_AMPLITUDE - MIN_SWAY_AMPLITUDE);
        float duration = MIN_SWAY_DURATION + rand.nextFloat() * (MAX_SWAY_DURATION - MIN_SWAY_DURATION);
        Vector2 originalSize = leaf.getDimensions();
        Vector2 scaledSize = originalSize.mult(MULT_FACTOR);

        new ScheduledTask(leaf, delay, false, () -> {
            new Transition<>( leaf,
                    leaf.renderer()::setRenderableAngle, -amplitude, amplitude,
                    Transition.CUBIC_INTERPOLATOR_FLOAT, duration,
                    Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                    null );

            new Transition<>(
                    leaf,leaf::setDimensions, originalSize, scaledSize,
                    (a, b, t) -> a.mult(1 - t).add(b.mult(t)),
                    duration,  Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                    null);
        });
    }
}
