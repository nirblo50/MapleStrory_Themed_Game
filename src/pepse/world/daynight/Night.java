package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * The Night class represents the night object in a game. It is a black rectangle that covers the screen
 * and has an opacity that transitions between 0 (fully transparent) and 0.5 (semi-transparent) over
 * a given time period, creating a day-night cycle effect.
 *
 */
public class Night {
    private static final Float MIDNIGHT_OPACITY = 0.5f;
    private static final Float DAY_OPACITY = 0f;
    private static final String NIGHT_TAG = "night";

    /**
     * A private constructor to prevent creating a Night instance
     */
    private Night(){}

    /**
     * Creates a new night object and adds it to the specified game object collection.
     *
     * @param gameObjects the game object collection to add the night object to
     * @param layer the layer to add the night object to in the game object collection
     * @param windowDimensions the dimensions of the game window
     * @param cycleLength the length of the day-night cycle in seconds
     * @return the newly created night object
     */
    public static GameObject create(
            GameObjectCollection gameObjects, int layer, Vector2 windowDimensions, float cycleLength) {
        Renderable nightRenderable = new RectangleRenderable(Color.BLACK);
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions, nightRenderable);
        night.setTag(NIGHT_TAG);
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(night, layer);

        new Transition<>(night, night.renderer()::setOpaqueness,
                DAY_OPACITY,
                MIDNIGHT_OPACITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);

        return night;
    }
}
