package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents the sky object in the game.
 * has fixed color for the sky.
 */
public class Sky {
    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");

    /**
     * A private constructor to prevent creating a Sky instance
     */
    private Sky() {
    }

    /**
     * Creates a new GameObject representing the sky.
     *
     * @param gameObjects      the collection of game objects to add the sky to
     * @param windowDimensions the dimensions of the window
     * @param skyLayer         the layer to add the sky to
     * @return the new sky GameObject
     */
    public static GameObject create(GameObjectCollection gameObjects, Vector2 windowDimensions, int skyLayer) {

        GameObject sky = new GameObject(
                Vector2.ZERO, windowDimensions, new RectangleRenderable(BASIC_SKY_COLOR));
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sky, skyLayer);
        sky.setTag("sky");

        return sky;
    }
}
