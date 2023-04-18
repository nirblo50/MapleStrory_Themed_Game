package pepse.util;

import danogl.GameObject;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * A class representing a GameObject that counts and displays the current energy value in numeric form.
 */
public class NumericEnergyCounter extends GameObject {

    public static final String ENERGY_TEXT = "Energy: ";
    private TextRenderable textRenderable;
    private final Counter energyCounter;

    /**
     * The constructor of the numeric life counter
     * @param topLeftCorner top left corner of the object
     * @param dimensions the dimensions of the object
     * @param energyCounter A counter object of the energy
     */
    public NumericEnergyCounter(Vector2 topLeftCorner, Vector2 dimensions, Counter energyCounter) {
        super(topLeftCorner, dimensions, null);

        this.energyCounter = energyCounter;
        this.textRenderable = new TextRenderable(ENERGY_TEXT + this.energyCounter.value(),null,false,true);
        this.renderer().setRenderable(textRenderable);
    }

    /**
     * Updates every frame the energy counter text.
     * @param deltaTime not relevant
     */
    @Override
    public void update(float deltaTime){
        super.update(deltaTime);

        this.textRenderable = new TextRenderable(ENERGY_TEXT + energyCounter.value(),null,false,true);
        this.renderer().setRenderable(textRenderable);
    }

}
