package bricker.brick_strategies;

import bricker.gameobjects.PuckBall;
import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

import java.util.Random;

import static bricker.main.BrickerGameManager.BALL_DIM;

/**
 * A collision strategy that replaces a brick with two puck balls when hit by a regular Ball
 * when the brick is destroyed, two smaller PuckBall objects are spawned from the brick's center
 * each puck ball receives a random velocity direction and behaves similarly to balls
 * (except for their different color and size)
 */
public class AddPucksStrategy implements CollisionStrategy {
    private final GameObjectCollection gameObjects;
    private final Counter brickCounter;
    private final ImageReader imageReader;
    private final SoundReader soundReader;

    /**
     * constructs a new AddPucksStrategy=
     * @param gameObjects the game's object collection
     * @param brickCounter a counter that tracks how many bricks remain in the level
     * @param imageReader used to load puck images
     * @param soundReader used to load collision sound for pucks
     */
    public AddPucksStrategy(GameObjectCollection gameObjects,
                            Counter brickCounter,  ImageReader imageReader,
                            SoundReader soundReader) {
        this.gameObjects = gameObjects;
        this.brickCounter = brickCounter;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
    }

    /**
     * Handles a PuckBall collision with a brick
     * @param brick The brick that was hit.
     * @param collider The object that collided with the brick.
     */
    @Override
    public void onCollision(GameObject brick, GameObject collider) {
        if (!"Ball".equals(collider.getTag()) && !"PuckBall".equals(collider.getTag())) {
            return;
        }
        //remove brick and update counter.
        if(gameObjects.removeGameObject(brick)){
            brickCounter.decrement();
        }

        //spawn two pucks from the brick's center
        createPuck(brick.getCenter());
        createPuck(brick.getCenter());
    }

    /**
     * creates a PuckBall at the given position, assigns it a random direction,
     * sets its velocity, and adds it to the game
     * @param center The starting position for the puck.
     */
    private void createPuck(Vector2 center) {
        Renderable puckImage = imageReader.readImage("assets/mockBall.png", true);
        Sound sound = soundReader.readSound("assets/blop.wav");

        // Pucks are 75% of the ball size
        Vector2 puckSize = BALL_DIM.mult(0.75f);
        PuckBall puck = new PuckBall(center, puckSize, puckImage, sound);
        Random rand = new Random();
        // Random direction (0 to 2π ensures 360° movement)
        double angle = rand.nextDouble() * Math.PI * 2;
        float velX = (float) Math.cos(angle) * BrickerGameManager.BALL_SPEED;
        float velY = (float) Math.sin(angle) * BrickerGameManager.BALL_SPEED;
        puck.setVelocity(new Vector2(velX, velY));
        gameObjects.addGameObject(puck);
    }
}
