package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;

/**
 * The Terrain class represents the terrain in the game.
 * It is responsible for generating the blocks that make up the terrain and adding them
 * to the game object collection.
 */
public class Terrain {

    /********** Terrain Constants  ***************/
    public static final String TERRAIN_TAG = "terrain";
    private static final double TERRAIN_HEIGHT_RATIO = 0.666;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);

    private static final int TERRAIN_DEPTH = 20;


    /*************** Variables *****************/
    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private int groundHeightAtX0;
    private final NoiseGenerator noiseGenerator;
    private final Vector2 windowsDimensions;

    /**
     * Constructs a new Terrain instance.
     * @param gameObjects The game object collection to which the terrain blocks will be added.
     * @param groundLayer The layer at which the terrain blocks should be added in the game object collection.
     * @param windowDimensions The dimensions of the game window.
     * @param seed The seed for randomization
     */
    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed) {
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.groundHeightAtX0 = 0;
        this.windowsDimensions = windowDimensions;
        this.groundHeightAtX0 = (int) ((int) windowDimensions.y() * TERRAIN_HEIGHT_RATIO);
        this.noiseGenerator = new NoiseGenerator(seed);
    }

    /**
     * Returns the height of the ground at the given x-coordinate.
     * @param x The x-coordinate for which to get the ground height.
     * @return The height of the ground at the given x-coordinate.
     */
    public float groundHeightAt(float x) {
        int blockSize = 30; // size of each block in pixels
        int frequency = 50;
        int numBlocks = (int) (windowsDimensions.x() / blockSize); // number of blocks in the terrain

        double noiseValue = this.noiseGenerator.noise((int) (x / blockSize), numBlocks, frequency);
        // scale the noise value to the desired range
        int terrainHeight = (int) (noiseValue * windowsDimensions.y() / 2.0f);
        float height = this.groundHeightAtX0 - terrainHeight;
        return (float) Math.floor(height / Block.SIZE) * Block.SIZE;
    }

    /**
     * Creates the terrain blocks in the given x-coordinate range.
     * @param minX The minimum x-coordinate for the terrain blocks.
     * @param maxX The maximum x-coordinate for the terrain blocks.
     */
    public void createInRange(int minX, int maxX) {
        for (int curX = minX; curX < maxX; curX += Block.SIZE) {
            float curY = groundHeightAt(curX);
            int layer = groundLayer;
            for (int i = 0; i < TERRAIN_DEPTH; ++i) {
                Renderable renderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                if (i > 1) layer = PepseGameManager.BOTTOM_TERRAIN_LAYER;
                Block block = new Block(new Vector2(curX, curY), renderable);
                block.setTag(TERRAIN_TAG);
                this.gameObjects.addGameObject(block, layer);
                curY += Block.SIZE;
            }
        }
    }
}
