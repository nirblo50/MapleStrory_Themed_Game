package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import danogl.components.Component;

import java.awt.*;

/**
 * The SunHalo class represents a halo effect that follows the sun object in a game. It is an oval-shaped
 * object with a specified color that is centered on the sun object's position.
 */
public class SunHalo {
    private static final String SUN_HALO_TAG = "sun_halo";
    private static final float HALO_SIZE = 250;

    /**
     * A private constructor to prevent creating a SunHalo instance
     */
    private SunHalo() {
    }

    /**
     * Creates a new sun halo object and adds it to the specified game object collection. The sun halo
     * object will follow the position of the specified sun object.
     *
     * @param gameObjects the game object collection to add the sun halo object to
     * @param layer       the layer to add the sun halo object to in the game object collection
     * @param sun         the sun object to follow
     * @param color       the color of the sun halo
     * @return the newly created sun halo object
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer, GameObject sun, Color color) {
        Renderable haloRenderable = new OvalRenderable(color);
        Vector2 haloRadius = new Vector2(HALO_SIZE + Sun.SUN_SIZE, HALO_SIZE + Sun.SUN_SIZE);
        GameObject sunHalo = new GameObject(Vector2.ZERO, haloRadius, haloRenderable);
        sunHalo.setTag(SUN_HALO_TAG);

        Component haloMover = (deltaTime) -> sunHalo.setCenter(sun.getCenter());
        sunHalo.addComponent(haloMover);
        gameObjects.addGameObject(sunHalo, layer);
        return sunHalo;
    }
}
