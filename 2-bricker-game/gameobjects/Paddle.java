package bricker.gameobjects;

import danogl.GameObject;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

/**
 * represents the main paddle controlled by the player
 * the paddle listens to keyboard input (left and right arrows),
 * moves accordingly, and makes sure it never leaves the screen boundaries.
 * this class only handles movement and  position updates , not collision logic.
 */
public class Paddle extends GameObject {
    //the paddle speed for the user
    private static final float MOVEMENT_SPEED = 350;

    UserInputListener inputListener;
    private final Vector2 windowDimensions;
    private final String PADDLE =  "Paddle";

    /**
     * Constructs a Paddle instance.
     * @param topLeftCorner Initial top-left position of the paddle
     * @param dimensions Paddle size
     * @param renderable for the paddle image
     * @param inputListener handles player input
     * @param windowDimensions the window dimensions — used to keep within screen bounds
     */
    public Paddle(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener, Vector2 windowDimensions) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
        this.windowDimensions =  windowDimensions;
        this.setTag(PADDLE);
    }

    /**
     * This function updates the Paddle on the screen according to the user input
     * on the KeyBoard
     * The function also ensures the paddle stays within the screen boundaries.
     * @param deltaTime time passed since the last frame.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        //when the game starts the paddle stays stable
        Vector2 movementDir = Vector2.ZERO;
        //go left
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT)){
            movementDir = movementDir.add(Vector2.LEFT.mult(MOVEMENT_SPEED));
        }
        //go right
        if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT)){
            movementDir = movementDir.add(Vector2.RIGHT.mult(MOVEMENT_SPEED));
        }
        //apply the movement
        setVelocity(movementDir);
        float halfWidth = getDimensions().x() / 2;
        float x = getCenter().x();

        //Prevent leaving left and right boundary
        if (x < halfWidth) {
            setCenter(new Vector2(halfWidth, getCenter().y()));
        }
        if (x > windowDimensions.x() - halfWidth) {
            setCenter(new Vector2(windowDimensions.x() - halfWidth, getCenter().y()));
        }
    }
}
