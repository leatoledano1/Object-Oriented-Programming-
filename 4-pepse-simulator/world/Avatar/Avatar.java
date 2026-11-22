package pepse.world.avatar;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;
import java.util.function.Consumer;

/**
 * Represents the player-controlled avatar in the game.
 * The avatar can move, jump, perform a double jump, and manage energy.
 * Its behavior is controlled by user input and collisions with the world.
 */
public class Avatar extends GameObject {
    /** Avatar size */
    private static final Vector2 AVATAR_SIZE = new Vector2(50, 50);

    /**tag for fruit*/
    private static final String FRUIT = "Fruit";
    /**tag for block*/
    private static final String BLOCK  = "Block";
    /**tag for avatar*/
    private static final String AVATAR = "Avatar";

    /**movement and physics constants */
    private static final float MOVE_SPEED = 400f;
    private static final float JUMP_SPEED = -500f;
    private static final float GRAVITY = 600f;

    /** Energy related constants */
    private static final int MAX_ENERGY = 100;
    private static final int MOVE_COST = 2;
    private static final int JUMP_COST = 20;
    private static final int DOUBLE_JUMP_COST = 50;
    private static final int IDLE_GAIN = 1;
    private static final int FRUIT_GAIN = 10;

    /** listener for user keyboard input */
    private final UserInputListener inputListener;

    /** current energy level */
    private int energy = MAX_ENERGY;

    /**used to detect single key press events */
    private boolean jumpPressedLastFrame = false;

    /**indicates whether a double jump was already used */
    private boolean hasDoubleJumped = false;

    /** Avatar animations */
    private final AnimationRenderable idleAnimation;
    private final AnimationRenderable runAnimation;
    private final AnimationRenderable jumpAnimation;

    /**Idle animation assets*/
    private static final String IDLE_0 = "assets/idle_0.png";
    private static final String IDLE_1 = "assets/idle_1.png";
    private static final String IDLE_2 = "assets/idle_2.png";
    private static final String IDLE_3 = "assets/idle_3.png";

    /**Run animation assets*/
    private static final String RUN_0 = "assets/run_0.png";
    private static final String RUN_1 = "assets/run_1.png";
    private static final String RUN_2 = "assets/run_2.png";
    private static final String RUN_3 = "assets/run_3.png";
    private static final String RUN_4 = "assets/run_4.png";
    private static final String RUN_5 = "assets/run_5.png";

    /**Jump animation assets*/
    private static final String JUMP_0 = "assets/jump_0.png";
    private static final String JUMP_1 = "assets/jump_1.png";
    private static final String JUMP_2 = "assets/jump_2.png";
    private static final String JUMP_3 = "assets/jump_3.png";

    /** Callback for updating the energy display */
    private Consumer<Integer> energyDisplayCallback;
    /**time between each avatar photo for each state  */
    private static final float TIME_BETWEEN_PHOTOS = 0.2F;

    private static final int ZERO = 0;

    /**constructs a new Avatar object
     * @param topLeftCorner initial position of the avatar
     * @param inputListener listener for user input
     * @param imageReader reader used to load animation images
     */
    public Avatar(Vector2 topLeftCorner,UserInputListener inputListener, ImageReader imageReader) {
        super(topLeftCorner, AVATAR_SIZE, null);
        this.inputListener = inputListener;

        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);

        idleAnimation = new AnimationRenderable(
                new String[]{ IDLE_0, IDLE_1, IDLE_2, IDLE_3 },
                imageReader, true, TIME_BETWEEN_PHOTOS);

        runAnimation = new AnimationRenderable(
                new String[]{ RUN_0, RUN_1, RUN_2, RUN_3, RUN_4, RUN_5 },
                imageReader, true, TIME_BETWEEN_PHOTOS);

        jumpAnimation = new AnimationRenderable(
                new String[]{ JUMP_0, JUMP_1, JUMP_2, JUMP_3 },
                imageReader, true, TIME_BETWEEN_PHOTOS);

        renderer().setRenderable(idleAnimation);
        setTag(AVATAR);
    }

    /**
     * decreases the avatar's energy by the given cost
     * @param cost amount of energy to decrease
     */
    private void decreaseEnergy(int cost) {
        energy = Math.max(ZERO, energy - cost);
    }

    /**handles horizontal movement based on user input
     * @return true if the avatar is currently moving horizontally
     */
    private boolean runningAvatar() {
        boolean onGround = getVelocity().y() == 0;
        float sidesVel = ZERO;

        if (energy >= MOVE_COST) {
            if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
                sidesVel = -MOVE_SPEED;
                renderer().setRenderable(runAnimation);
                renderer().setIsFlippedHorizontally(true);
                if(onGround) {
                    decreaseEnergy(MOVE_COST);
                }
            }
            else if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
                sidesVel = MOVE_SPEED;
                renderer().setRenderable(runAnimation);
                renderer().setIsFlippedHorizontally(false);
                if(onGround) {
                    decreaseEnergy(MOVE_COST);
                }
            }
        }

        transform().setVelocityX(sidesVel);
        return sidesVel != 0;
    }

    /**handles jumping and double jumping logic
     * @return true if a jump or double jump was performed
     */
    private boolean jumpingAvatar() {
        boolean jumpPressed = inputListener.isKeyPressed(KeyEvent.VK_SPACE);

        if (jumpPressed && !jumpPressedLastFrame) {

            if (getVelocity().y() == 0 && energy >= JUMP_COST) {
                transform().setVelocityY(JUMP_SPEED);
                renderer().setRenderable(jumpAnimation);
                decreaseEnergy(JUMP_COST);
                return true;
            }

            if (getVelocity().y() != ZERO && !hasDoubleJumped && energy >= DOUBLE_JUMP_COST) {
                transform().setVelocityY(JUMP_SPEED);
                renderer().setRenderable(jumpAnimation);
                decreaseEnergy(DOUBLE_JUMP_COST);
                hasDoubleJumped = true;
                return true;
            }
        }
        return false;
    }

    /**sets a callback for displaying the avatar's energy
     * @param callback consumer that receives the current energy value
     */
    public void setEnergyCallback(Consumer<Integer> callback) {
        this.energyDisplayCallback = callback;
        callback.accept(energy);
    }

    /**
     * Increases the avatar's energy by the given amount
     * @param amount amount of energy to add
     */
    private void addEnergy(int amount) {
        energy = Math.min(MAX_ENERGY, energy + amount);
        if (energyDisplayCallback != null) {
            energyDisplayCallback.accept(energy);
        }
    }

    /**
     * Updates the avatar state every frame,
     * @param delta time elapsed since last frame
     */
    @Override
    public void update(float delta) {
        super.update(delta);

        boolean isRunning = runningAvatar();
        boolean isJumping = jumpingAvatar();
        boolean isIdle = !isRunning && !isJumping;

        if (energy <= ZERO) {
            transform().setVelocity(Vector2.ZERO);
            renderer().setRenderable(idleAnimation);
        }

        if (isIdle && getVelocity().isZero()) {
            renderer().setRenderable(idleAnimation);
            addEnergy(IDLE_GAIN);
        }

        jumpPressedLastFrame = inputListener.isKeyPressed(KeyEvent.VK_SPACE);
    }

    /**
     * Handles collisions with other game objects.
     *
     * @param other the object the avatar collided with
     * @param collision collision information
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);

        if (other.getTag().equals(BLOCK)) {
            transform().setVelocityY(ZERO);
            hasDoubleJumped = false;
        }

        if (other.getTag().equals(FRUIT)) {
            addEnergy(FRUIT_GAIN);
        }
    }
}
