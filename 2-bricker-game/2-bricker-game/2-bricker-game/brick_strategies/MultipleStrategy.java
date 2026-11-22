package bricker.brick_strategies;

import danogl.collisions.GameObjectCollection;
import danogl.util.Counter;
import danogl.GameObject;

import java.util.Random;

/**
 * a collision strategy that combines several strategies together.
 * when a brick using this strategy is hit, it triggers multiple other
 * strategies at the same time. this allows one brick to behave like any combination of strats
 * a MultipleStrategy can be chosen at most once per bricks
 */
public class MultipleStrategy extends BasicCollisionStrategy implements CollisionStrategy{

    private static final int MAX_STRATEGIES = 3;
    private static final int MAX_MULTIPLE = 1;
    private static final int ALL_STRATEGIES = 5;
    private static final int STRAT_PER_BRICK_N0_MULTIPLE = 2;

    private final FactoryOfStrategies factory;
    private final CollisionStrategy[] strategies;
    private final Random random;
    private int multipleCount;

    /**
     * creates a new MultipleStrategy
     * @param gameObjects the global game object collection
     * @param brickCounter shared counter of remaining bricks
     * @param factory the factory used for doing strategy instances
     */
    public MultipleStrategy(GameObjectCollection gameObjects,
                            Counter brickCounter, FactoryOfStrategies factory) {
        super(gameObjects, brickCounter);
        this.factory = factory;
        this.random = new Random();
        this.strategies = chooseByTheMultipleRules();
    }

    /**
     * chooses a single strategy based on a random option.
     * @param options a numeric value selecting which strategy should be created
     * @return a concrete CollisionStrategy instance
     */
    private CollisionStrategy chooseOneStrategy(int options) {
        CollisionStrategy chosen;
        switch (options) {
        case 1:
            chosen = factory.chooseStrategyByTag("ExtraPaddleStrategy");
            break;
        case 2:
            chosen = factory.chooseStrategyByTag("ExtraHeartStrategy");
            break;
        case 3:
            chosen = factory.chooseStrategyByTag("ExplodeNeighborStrategy");
            break;
        case 4:
            chosen = factory.chooseStrategyByTag("AddPuckBallStrategy");
            break;
        case 5:
            chosen = new MultipleStrategy( gameObjects, brickCounter, factory );
            break;
        default:
            chosen = factory.chooseStrategyByTag("Basic");
        }
        return chosen;
    }

    /**
     * selects several internal strategies for this multiple strategy.
     * includes logic to prevent infinite multiple-strategy recursion.
     * @return an array of chosen strategies
     */
    private CollisionStrategy[] chooseByTheMultipleRules() {
        CollisionStrategy[] strats = new CollisionStrategy[MAX_STRATEGIES];
        int stratCounter = 0;

        for (int i = 0; i < STRAT_PER_BRICK_N0_MULTIPLE; i++) {
            if (stratCounter >= MAX_STRATEGIES)
                break;
            int rand = random.nextInt(ALL_STRATEGIES) + 1;
            if(rand == 5 && multipleCount < MAX_MULTIPLE) {
                stratCounter+=2;
                multipleCount++;
            }
            stratCounter++;
        }
        if( stratCounter > MAX_STRATEGIES) {
            stratCounter =  MAX_STRATEGIES;
        }
        for(int i = 0; i < stratCounter ; i++) {
            int option = random.nextInt(ALL_STRATEGIES-1) + 1;
            strats[i] = chooseOneStrategy(option);
        }
        return strats;
    }

    /**
     * when a collision occurs, simply apply ALL internal strategies,
     * effectively combining their behaviors.
     *
     * @param brick the brick that was hit
     * @param collider the object that collided with it
     */
    @Override
    public void onCollision(GameObject brick, GameObject collider) {
        for (CollisionStrategy strategy : strategies) {
            if (strategy != null) {
                strategy.onCollision(brick, collider);
            }
        }
    }
}
