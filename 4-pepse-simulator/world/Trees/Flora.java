package pepse.world.trees;

import danogl.GameObject;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.Terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**The Flora class is responsible for generating and managing vegetation in the world.
 * it includes trees and their fruits, and fruit respawning logic.
 */
public class Flora extends GameObject {
    /** probability of spawning a tree at the given position */
    private static final float TREE_PROBABILITY = 0.1f;
    /** Min blocks gap between trees */
    private static final int MIN_GAP_BLOCKS = 1;
    /** Max blocks gap between trees */
    private static final int MAX_GAP_BLOCKS = 7;
    /** Time in seconds before a fruit respawns after being eaten */
    private static final float FRUIT_RESPAWN_TIME = 30f;
    private static final float DATA_INIT_TIMER = 0f;
    /** Terrain used to determine ground height */
    private final Terrain terrain;
    /** Random generator for flora placement */
    private final Random random;
    /** list of all currently spawned flora objects */
    private final List<GameObject> spawned = new ArrayList<>();
    /** Metadata for all fruits in the world */
    private final List<FruitData> fruits = new ArrayList<>();

    /**constructs a Flora manager.
     * @param terrain the terrain used to place trees
     * @param seed seed for random generation
     */
    public Flora(Terrain terrain, int seed) {
        super(Vector2.ZERO, Vector2.ZERO, null);
        this.terrain = terrain;
        this.random = new Random(seed);
    }

    /**
     * creates flora objects within the given horizontal range
     * @param minX minimum x-coordinate
     * @param maxX maximum x-coordinate
     * @return a list of newly created GameObjects
     */
    public List<GameObject> createInRange(int minX, int maxX) {
        spawned.clear();
        int x = (int) Math.floor((double) minX / Block.SIZE) * Block.SIZE;
        while (x <= maxX) {
            if (random.nextFloat() < TREE_PROBABILITY) {
                Tree tree = new Tree(terrain::groundHeightAt,this::onFruitEaten,random.nextLong());
                spawned.addAll(tree.createTreeAt(x, fruits));
                int gap = MIN_GAP_BLOCKS + random.nextInt(MAX_GAP_BLOCKS - MIN_GAP_BLOCKS + 1);
                x += gap * Block.SIZE;
            } else {
                x += Block.SIZE;
            }
        }

        return new ArrayList<>(spawned);
    }

    /**
     * Updates fruit respawn timer
     * @param deltaTime time passed since last frame
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        for (FruitData data : fruits) {
            if (!data.eaten) continue;
            data.timer += deltaTime;
            if (data.timer >= FRUIT_RESPAWN_TIME) {
                data.fruit.respawn(data.position, data.size);
                data.timer = DATA_INIT_TIMER;
                data.eaten = false;
            }
        }
    }

    /**
     * Marks a fruit as eaten and starts its respawn timer.
     *
     * @param fruit the fruit that was eaten
     */
    private void onFruitEaten(Fruit fruit) {
        for (FruitData data : fruits) {
            if (data.fruit == fruit && !data.eaten) {
                data.eaten = true;
                data.timer = DATA_INIT_TIMER;
                break;
            }
        }
    }

    /**
     * Inner class holding data related to a fruit and its respawn state
     */
    public static class FruitData {
        /** The fruit GameObject */
        public final Fruit fruit;
        /** Original fruit position */
        public final Vector2 position;
        /** Original fruit size */
        public final Vector2 size;
        /** Indicates whether the fruit was eaten */
        public boolean eaten = false;
        /** Timer counting time since the fruit was eaten */
        public float timer = DATA_INIT_TIMER;

        /**constructor
         * @param fruit the fruit object
         * @param position initial position of the fruit
         * @param size initial size of the fruit
         */
        public FruitData(Fruit fruit, Vector2 position, Vector2 size) {
            this.fruit = fruit;
            this.position = position;
            this.size = size;
        }
    }
}
