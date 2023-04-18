package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.GameObjectPhysics;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.gui.rendering.ImageRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.function.Consumer;

public class Cloud {

    private static final float CLOUD_HEIGHT = 120f;
    private static final float CLOUD_WIDTH = 250f;
    public static final String CLOUD_TAG = "cloud";
    private static final String CLOUD_PATH = "assets/cloude.png";
    private static final int XTRAVEL_RATIO = 3;

    /**
     * A private constructor to prevent creating a Cloud instance
     */
    private Cloud() {
    }

    public static GameObject create(GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,
                                    float cycleLength, ImageReader imageReader, Vector2 topLeftCorner,
                                    float startX) {
        Renderable cloudRenderable = imageReader.readImage(CLOUD_PATH, true);
        GameObject cloud = new GameObject(topLeftCorner, new Vector2(CLOUD_WIDTH, CLOUD_HEIGHT),
                cloudRenderable);

        gameObjects.addGameObject(cloud, layer);
        cloud.setTag(CLOUD_TAG);

        cloud.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        cloud.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);

        Consumer<Float> mover = movement -> {
            cloud.setCenter(new Vector2(movement, cloud.getCenter().y()));
        };

        new Transition<>(cloud, mover,
                startX, startX + XTRAVEL_RATIO * windowDimensions.x(), Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength, Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);

        return cloud;
    }
}