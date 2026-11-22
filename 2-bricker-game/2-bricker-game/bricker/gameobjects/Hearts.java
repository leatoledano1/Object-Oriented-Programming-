package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * This class acts as a Manager class.
 * it only manages the visual display of the player's lives (hearts + numeric text).
 */
public class Hearts {
    /** maximum number of lives to display */
    public static final int MAX_LIVES = 4;
    /** size (radius) of each heart icon */
    public static final float HEART_SIZE = 30;
    /** size of the numeric text displaying the lives */
    public static final float TEXT_SIZE = 30;
    /** horizontal distance between heart icons */
    public static final float DISTANCE_BETWEEN_EACH = 10;
    /** vertical offset from the top of the screen for placing the hearts */
    public static final float DIST_FROM_TOP = 5;


    private int lives;
    private final GameObject[] hearts;
    private final GameObject textObj;
    private final TextRenderable text;
    private final Renderable heartImage;

    /**
     * Creates a visual lives display that consists of heart icons and a numeric counter.
     * @param startingLives Initial number of lives to display
     * @param heartImage Renderable image of a heart icon
     * @param gameObjects The game's object collection (UI objects will be added to it)
     *
     * the function updates the visual layer with those objects
     */
    public Hearts(int startingLives, Renderable heartImage,
                  GameObjectCollection gameObjects) {
        this.lives = startingLives;
        this.heartImage = heartImage;
        hearts = new GameObject[MAX_LIVES];

        //create heart icons
        for (int i = 0; i < MAX_LIVES; i++) {
            Vector2 position = new Vector2(DIST_FROM_TOP + i * (HEART_SIZE + DISTANCE_BETWEEN_EACH),
                    DIST_FROM_TOP);
            Renderable heartPic;
            if (i < lives) {
                heartPic = heartImage;
            } else {
                heartPic = null;
            }
            hearts[i] = new GameObject(position, new Vector2(HEART_SIZE, HEART_SIZE), heartPic);
            gameObjects.addGameObject(hearts[i], Layer.UI);
        }
        //creates numeric text display
        text = new TextRenderable(Integer.toString(lives));
        text.setColor(Color.green);

        textObj = new GameObject(new Vector2(DIST_FROM_TOP, DIST_FROM_TOP + HEART_SIZE + 10),
                new Vector2(TEXT_SIZE, TEXT_SIZE), text);
        gameObjects.addGameObject(textObj, Layer.UI);
        updateUI();
    }

    /**
     * Returns the numeric text object
     * */
    public GameObject getNumericDisplay() {
        return textObj;
    }

    /**
     * Returns current number of lives.
     */
    public int getLives() {
        return lives;
    }


    /** Decreases life count by 1 (down to 0)
     * */
    public void loseLife() {
        if (lives > 0) {
            lives--;
            updateUI();
        }
    }

    /**
     * Increases life count by 1 (up to MAX_LIVES)
     * */
    public void gainLife() {
        if (lives < MAX_LIVES) {
            lives++;
            updateUI();
        }
    }

    /**
     * Updates heart icons and numeric text color and value
      */
    private void updateUI() {
        for (int i = 0; i < MAX_LIVES; i++) {
            Renderable heartPic;
            if (i < lives) {
                heartPic = heartImage;
            } else {
                heartPic = null;
            }
            hearts[i].renderer().setRenderable(heartPic);
        }
        text.setString(Integer.toString(lives));
        if (lives >= 3) {
            text.setColor(Color.green);
        }
        else if (lives == 2) {
            text.setColor(Color.yellow);
        }
        else {
            text.setColor(Color.red);
        }
    }
}
