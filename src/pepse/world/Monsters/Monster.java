package pepse.world.Monsters;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Terrain;
import pepse.world.trees.Tree;

import java.util.Random;

public class Monster extends GameObject {
    private static final int GRAVITY = 500;
    private static final int MAX_TIME_UNTIL_JUMP = 8;
    private static final int JUMP_SPEED = 300;
    private static final int MOVEMENT_SPEED = 200;
    private static final Random random = new Random();
    public static final float WAIT_TIME = 0.3f;
    public final Renderable rightImg;
    public final Renderable leftImg;
    private int direction;
    private boolean isOnAir = true;

    public Monster(Vector2 topLeftCorner, Vector2 dimensions, Renderable lefImg, Renderable rightImg) {
        super(topLeftCorner, dimensions, null);
        transform().setAccelerationY(GRAVITY);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        this.leftImg = lefImg;
        this.rightImg = rightImg;
        this.direction = random.nextBoolean() ? MOVEMENT_SPEED : -MOVEMENT_SPEED;
        updateRenderable();
        applyMovement();
    }

    private void applyMovement() {
        this.transform().setVelocityX(this.direction);
        new ScheduledTask(this, random.nextInt(MAX_TIME_UNTIL_JUMP), true, () -> {
            if (!this.isOnAir) {
                this.transform().setVelocityY(-JUMP_SPEED);
                this.isOnAir = true;
            }
            new ScheduledTask(this, WAIT_TIME, false, () -> this.transform().setVelocityX(this.direction));
        }
        );
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (other.getTag().equals(Tree.TRUNK_TAG)) {
            direction = -direction;
            this.transform().setVelocityX(direction);
            updateRenderable();
        }
        if (other.getTag().equals(Terrain.TERRAIN_TAG))
            isOnAir = false;
    }

    private void updateRenderable() {
        if (direction < 0)
            this.renderer().setRenderable(leftImg);
        else
            this.renderer().setRenderable(rightImg);

    }
}

