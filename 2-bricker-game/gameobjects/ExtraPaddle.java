package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * represents an additional temporary paddle that appears when
 * a brick with an ExtraPaddle strategy is hit
 * This paddle behaves exactly like the original Paddle,
 * but disappears after being hit by the green ball a fixed number of times (for now 4)
 */
public class ExtraPaddle extends Paddle {
    GameObjectCollection gameObjects;
    private int hitsCounter;
    private static final int MAX_HITS = 4;
    private final String EXTRA_PADDLE= "ExtraPaddle";

    /**
     * Constructs an ExtraPaddle instance.
     * same as paddle beside a counter and a game object
     * @param topLeftCorner Initial top-left position of the paddle.
     * @param dimensions Paddle size (same as main paddle).
     * @param renderable image used to draw the paddle.
     * @param inputListener handles player input (so this paddle also moves like the main one).
     * @param windowDimensions the window dimensions — used to keep within screen bounds.
     * @param gameObjects reference to the main game object collection,
     *                    so this paddle can remove itself after enough hits.
     */
    public ExtraPaddle(Vector2 topLeftCorner, Vector2 dimensions,
                  Renderable renderable, UserInputListener inputListener,
                  Vector2 windowDimensions, GameObjectCollection gameObjects) {
        super(topLeftCorner, dimensions, renderable,  inputListener, windowDimensions);
        this.hitsCounter = 0;
        this.gameObjects = gameObjects;
        this.setTag(EXTRA_PADDLE);
    }

    /**
     * handles collisions
     * the extra paddle counts how many times it has been hit by the (real) ball.
     * after MAX_HITS collisions, it removes itself from the game.
     * @param other The object that collided with this paddle
     * @param collision info about the collision
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        if(other.getTag().equals("Ball") || other.getTag().equals("PuckBall")) {
            hitsCounter++;
        }
        if(hitsCounter == MAX_HITS){
            gameObjects.removeGameObject(this);
        }
    }


    /**
     * This function updates the ExtraPaddle on the screen according to the user input
     * on the KeyBoard (acting exactly the same as Paddle)
     * The function also ensures the paddle stays within the screen boundaries.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }
}