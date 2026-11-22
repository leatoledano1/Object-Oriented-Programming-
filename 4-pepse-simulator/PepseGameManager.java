package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;

import pepse.world.Block;
import pepse.world.EnergyDisplay;
import pepse.world.Terrain;
import pepse.world.avatar.Avatar;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Flora;

import java.util.*;

import static pepse.world.Sky.create;

/**
 * Main game manager for the Pepse game.
 * Responsible for initializing the game world, managing dynamic loading
 * of world sections, and updating game objects.
 */
public class PepseGameManager extends GameManager {
    private static final String LEAF = "Leaf";
    private static final String FRUIT = "Fruit";
    private static final String AVATAR =  "Avatar";
    private static final String TRUNK = "Trunk";

    /** Layers */
    private static final int BACKGROUND_LAYER = Layer.BACKGROUND;
    private static final int NIGHT_LAYER = Layer.BACKGROUND;
    private static final int SUN_LAYER = Layer.BACKGROUND + 2;
    private static final int SUN_HALO_LAYER = Layer.BACKGROUND + 1;

    private static final int TERRAIN_LAYER = Layer.STATIC_OBJECTS;
    private static final int TRUNK_LAYER = Layer.STATIC_OBJECTS;

    private static final int AVATAR_LAYER = Layer.DEFAULT;
    private static final int LEAF_FRUIT_LAYER = Layer.DEFAULT + 3;

    private static final int UI_LAYER = Layer.UI;

    /** Number of screens ahead to load */
    private static final int SCREENS_TO_LOAD = 2;

    /** Number of screens to keep around the avatar */
    private static final int SCREENS_TO_KEEP = 3;
    /**how many screens before*/
    private static final int PRE_SCREEN = -2;

    /** Seed used for world generation */
    private static final int SEED = 0;
    /**The duration of a day cycle*/
    private static final float CYCLE_LEN = 30f;
    /** tha avatar height*/
    private static final float AVATAR_HEIGHT =50f;
    private static final float HALF =0.5f;
    private static final Vector2 ENERGY_BAR_SIZE = new Vector2(20, 20);
    private Terrain terrain;
    private Flora flora;
    private Avatar avatar;
    private WindowController windowController;

    /** Set of currently loaded screen indices */
    private final Set<Integer> loadedScreens = new HashSet<>();
    /** Mapping between screen indices and their game objects */
    private final Map<Integer, List<GameObject>> screenObjects = new HashMap<>();

    /**initializes the game, including the world ,avatar, UI,
     * camera,and day-night cycle+
     * @param imageReader reader for loading images
     * @param soundReader reader for loading sounds
     * @param inputListener listener for user input
     * @param windowController controller for the game window
     */
    @Override
    public void initializeGame(ImageReader imageReader,SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.windowController = windowController;

        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TERRAIN_LAYER, true);
        gameObjects().layers().shouldLayersCollide(TERRAIN_LAYER, TERRAIN_LAYER, false);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, LEAF_FRUIT_LAYER, true);

        Vector2 dims = windowController.getWindowDimensions();
        gameObjects().addGameObject(create(dims), BACKGROUND_LAYER);
        gameObjects().addGameObject(Night.create(dims, CYCLE_LEN), NIGHT_LAYER);

        createSun( dims);

        terrain = new Terrain(dims, SEED);
        flora = new Flora(terrain, SEED);
        gameObjects().addGameObject(flora, BACKGROUND_LAYER);

        avatar = createAvatar(dims, imageReader, inputListener);

        setCamera(new Camera(avatar,
                dims.mult(HALF).subtract(avatar.getTopLeftCorner()),
                dims,dims));

        //helper func to create energy
        createEnergy();
        updateWorldScreens();
    }

    /**creates the avatar and places it on the terrain
     * @param dims the window dimensions
     * @param imageReader used to load avatar  animations
     * @param inputListener used to read user input
     * @return the created Avatar object
     */
    private Avatar createAvatar(Vector2 dims, ImageReader imageReader, UserInputListener inputListener) {
        float avatarX = dims.x()* HALF;
        float groundY =(float) Math.floor( terrain.groundHeightAt(avatarX) / Block.SIZE) * Block.SIZE;
        avatar = new Avatar(
                new Vector2(avatarX, groundY - AVATAR_HEIGHT),
                inputListener,imageReader);
        avatar.setTag(AVATAR);
        gameObjects().addGameObject(avatar, AVATAR_LAYER);

        return avatar;
    }

    /**
     * creates the energy display and connect it to avatar.
     * The energy bar is update using a callback from the Avatar.
     */
    private void createEnergy(){
        EnergyDisplay energyDisplay = new EnergyDisplay(ENERGY_BAR_SIZE);
        avatar.setEnergyCallback(energyDisplay::updateEnergy);
        gameObjects().addGameObject(energyDisplay.getGameObject(), UI_LAYER);
    }

    /**creates the sun and sun halo objects and adds them to the game
     * These objects are used to simulate the day cycle
     * @param dims the window dimensions
     */
    private void createSun(Vector2 dims){
        GameObject sun = Sun.create(dims, CYCLE_LEN);
        gameObjects().addGameObject(sun, SUN_LAYER);
        gameObjects().addGameObject(SunHalo.create(sun), SUN_HALO_LAYER);
    }

    /**
     * Updates the game state every frame
     * @param deltaTime time elapsed since last frame
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updateWorldScreens();
        removeScreenObjects();
    }

    /**
     * Loads new world screens around the avatar when needed.
     */
    private void updateWorldScreens() {
        float avatarX = avatar.getCenter().x();
        int screenWidth = (int) windowController.getWindowDimensions().x();
        int currentScreen = (int) Math.floor(avatarX / screenWidth);
        for (int i = PRE_SCREEN; i < SCREENS_TO_LOAD; i++) {
            int screen = currentScreen + i;
            if (loadedScreens.contains(screen)) continue;
            int minX = screen * screenWidth;
            int maxX = minX + screenWidth;
            List<GameObject> content = new ArrayList<>();
            for (Block b : terrain.createInRange(minX, maxX)) {
                gameObjects().addGameObject(b, TERRAIN_LAYER);
                content.add(b);
            }
            for (GameObject obj : flora.createInRange(minX, maxX)) {
                String tag = obj.getTag();
                if (TRUNK.equals(tag)) {
                    gameObjects().addGameObject(obj, TRUNK_LAYER);
                } else if (LEAF.equals(tag) || FRUIT.equals(tag)) {
                    gameObjects().addGameObject( obj, LEAF_FRUIT_LAYER);
                } else {
                    gameObjects().addGameObject(obj, AVATAR_LAYER);
                }
                content.add(obj);
            }
            screenObjects.put(screen, content);
            loadedScreens.add(screen);
        }
    }

    /**
     * Removes world screens that are too far from the avatar.
     */
    private void removeScreenObjects() {
        float avatarX = avatar.getCenter().x();
        int screenWidth = (int) windowController.getWindowDimensions().x();
        int currentScreen = (int) Math.floor(avatarX / screenWidth);
        Iterator<Integer> it = loadedScreens.iterator();

        while (it.hasNext()) {
            int screen = it.next();
            if (screen < currentScreen - (SCREENS_TO_KEEP - 1)
                    || screen > currentScreen + (SCREENS_TO_KEEP - 1)) {
                for (GameObject obj : screenObjects.get(screen)) {
                    String tag = obj.getTag();

                    if (TRUNK.equals(tag)) {
                        gameObjects().removeGameObject(
                                obj, TRUNK_LAYER);
                    } else if (LEAF.equals(tag) || FRUIT.equals(tag)) {
                        gameObjects().removeGameObject(obj, LEAF_FRUIT_LAYER);
                    } else {
                        gameObjects().removeGameObject(obj, AVATAR_LAYER);
                    }
                }
                screenObjects.remove(screen);
                it.remove();
            }
        }
    }

    /**
     * The main func
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
