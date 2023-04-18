package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.Terrain;

import java.util.Random;

/**
 * The Leaf class represents a leaf object in a game. It is a rectangular-shaped object that falls from
 * a tree and is affected by wind. It also has a fading effect when it reaches the ground.
 */
public class Leaf extends GameObject {
    private static final int FADEOUT_TIME = 4;
    public static final String LEAF_TAG = "leaf";
    private static final float LEAF_FALLING_SPEED = 70;
    private static final float LEAF_WIND_SPEED = 25f;
    private static final float EPSILON_WAIT_TIME = 0.01f;
    private static final int DIE_TIME_BOUND = 15;
    private static final int MIN_DIE_TIME = 5;
    private static final float WIND_TRANSITION_TIME = 1f;
    private static final float EPSILON_TIME_FOR_REVIVE = 0.01f;
    private static final float OPAQUENESS_AFTER_DIE = 1f;
    private static final int LEAF_DROP_BOUND = 60;
    private static final int MIN_DROP_LIFETIME = 5;
    private static final int WIND_CYCLE_LENGTH = 2;
    private static final float WIND_ANGLE = 7f;
    private static final Vector2 LEAF_START_SIZE = new Vector2(Block.SIZE * 1.2f, Block.SIZE * 0.9f);
    private static final Vector2 LEAF_END_SIZE = new Vector2(Block.SIZE, Block.SIZE + 1.1f);
    private static final int MAX_TIME_TO_WAIT_TO_START = 2;

    private final Vector2 leaf_original_position;
    private Transition<Float> horizontalTransition;
    private Transition<Float> rotationTransition;
    private Transition<Vector2> sizeTransition;


    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Leaf(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, new Vector2(Block.SIZE, Block.SIZE), renderable);
        this.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        setTag(LEAF_TAG);
        this.leaf_original_position = topLeftCorner;
        applyLeafDropper();
        applyWind();

    }

    private void stopLeaf() {
        this.removeComponent(horizontalTransition);
        this.removeComponent(rotationTransition);
        this.removeComponent(sizeTransition);
        this.transform().setVelocity(0, 0);
    }

    /**
     * Make things happen when object started a collision
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (horizontalTransition != null && other.getTag().equals(Terrain.TERRAIN_TAG))
            new ScheduledTask(this, EPSILON_WAIT_TIME, false, this::stopLeaf);
    }

    /**
     * Causes the leaf to start falling and sets up a task to return the leaf to its original position after
     * a certain amount of time has passed.
     *
     * @see #returnToLife()
     */
    private void startFalling() {
        this.renderer().fadeOut(FADEOUT_TIME);
        int die_time = new Random().nextInt(DIE_TIME_BOUND) + MIN_DIE_TIME;
        this.transform().setVelocityY(LEAF_FALLING_SPEED);
        this.horizontalTransition = new Transition<>(this, this.transform()::setVelocityX,
                -LEAF_WIND_SPEED,
                LEAF_WIND_SPEED,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                WIND_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
        new ScheduledTask(this, die_time, false, this::returnToLife);
    }

    /**
     * Returns the leaf to its original position and re-applies the leaf dropper and wind effects to it.
     *
     * @see #applyLeafDropper()
     * @see #applyWind()
     */
    private void returnToLife() {
        new ScheduledTask(this, EPSILON_TIME_FOR_REVIVE,
                false, ()->this.renderer().setOpaqueness(OPAQUENESS_AFTER_DIE));
        this.setTopLeftCorner(leaf_original_position);
        this.transform().setVelocity(0,0);
        applyLeafDropper();
        applyWind();
    }

    /**
     * Applies the leaf dropper effect to the leaf. This causes the leaf to fall from the tree after a
     * certain amount of time has passed.
     *
     * @see #startFalling()
     */
    private void applyLeafDropper() {

        int lifeTime = new Random().nextInt(LEAF_DROP_BOUND) + MIN_DROP_LIFETIME;
        new ScheduledTask(this, lifeTime, false, this::startFalling);
    }

    /**
     * Activates the effect of wind on the leaf
     */
    private void applyWind() {

        Runnable run = () -> {
            // transition for rotating the leaf
            this.rotationTransition = new Transition<>(this, this.renderer()::setRenderableAngle,
                    -WIND_ANGLE,
                    WIND_ANGLE,
                    Transition.LINEAR_INTERPOLATOR_FLOAT,
                    WIND_CYCLE_LENGTH,
                    Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);

            // transition for changing the leaf's size
            this.sizeTransition = new Transition<>(this, this::setDimensions,
                    LEAF_START_SIZE,
                    LEAF_END_SIZE,
                    Transition.LINEAR_INTERPOLATOR_VECTOR,
                    WIND_CYCLE_LENGTH,
                    Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
        };
        float timeToStart = new Random().nextFloat() * MAX_TIME_TO_WAIT_TO_START;
        new ScheduledTask(this, timeToStart, false, run);
    }
}
