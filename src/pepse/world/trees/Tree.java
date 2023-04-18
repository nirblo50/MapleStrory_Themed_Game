package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;
import pepse.world.Terrain;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

/**
 * Class for the trees in the game, it has a trunk and leaves
 */
public class Tree {

    /*********** General ***********/
    private static final float ODDS_TO_PLANT_TREE = 0.2f;
    private static final int RANDOM_MAX_BOUND = 100;
    private static final int THRESHOLD = (int) (RANDOM_MAX_BOUND * ODDS_TO_PLANT_TREE);

    /*********** Trunk ***********/
    public static final String TRUNK_TAG = "trunk";
    private static final int MINIMUM_TRUNK_HEIGHT = Block.SIZE * 6;
    private static final int MAXIMUM_TRUNK_HEIGHT = Block.SIZE * 12;
    public static final Color BASE_TRUNK_COLOR = new Color(100, 50, 20);

    /*********** Leaves ***********/
    public static final Color BASE_LEAF_COLOR = new Color(50, 200, 30);

    private final GameObjectCollection gameObjects;
    private static final int LEAVES_SQUARE_SIZE = Block.SIZE * 5;
    private final int random_seed, leavesLayer, trunkLayer;
    private final Terrain terrain;


    public Tree(GameObjectCollection gameObjects, int random_seed, Terrain terrain, int leavesLayer,
                int trunkLayer) {
        this.gameObjects = gameObjects;
        this.random_seed = random_seed;
        this.leavesLayer = leavesLayer;
        this.trunkLayer = trunkLayer;
        this.terrain = terrain;
    }

    public void createInRange(int minX, int maxX) {
        for (int curX = minX; curX <= maxX; curX += 2 * Block.SIZE) {
            if (Tree.shouldPlantTree(random_seed, curX)) {
                float curY = terrain.groundHeightAt(curX);
                CreateTree(new Vector2(curX, curY - Block.SIZE), trunkLayer, leavesLayer, random_seed);
            }
        }
    }

    private void CreateTree(Vector2 groundPos, int trunkLayer, int leavesLayer, int seed) {
        float trunkHeight = getRandomTruckHeight(seed, (int) groundPos.x());
        generateTrunk(groundPos, trunkLayer, trunkHeight);
        generateLeaves(groundPos, leavesLayer, trunkHeight);
    }

    /**
     * Generate the trunk of a tree
     *
     * @param groundPos   the ground position to make the trunk
     * @param trunkLayer  the layer to put the trunk in
     * @param trunkHeight the height of the trunk
     */
    private void generateTrunk(Vector2 groundPos, int trunkLayer, float trunkHeight) {
        for (float curY = groundPos.y(); curY >= groundPos.y() - trunkHeight; curY -= Block.SIZE) {
            Renderable img = new RectangleRenderable(ColorSupplier.approximateColor(BASE_TRUNK_COLOR));
            Block trunk = new Block(new Vector2(groundPos.x(), curY), img);
            trunk.setTag(TRUNK_TAG);
            gameObjects.addGameObject(trunk, trunkLayer);
        }
    }

    /**
     * Generate the leaves around the top of the trunk of the tree
     *
     * @param groundPos   the ground position to make the trunk
     * @param leavesLayer the layer to put the leaves in
     * @param trunkHeight the height of the trunk
     */
    private void generateLeaves(Vector2 groundPos, int leavesLayer, float trunkHeight) {
        Vector2 center = groundPos.subtract(new Vector2(-Block.SIZE / 2f, trunkHeight));
        float startX = center.x() - LEAVES_SQUARE_SIZE / 2f, endX = center.x() + LEAVES_SQUARE_SIZE / 2f;
        float startY = center.y() - LEAVES_SQUARE_SIZE / 2f, endY = center.y() + LEAVES_SQUARE_SIZE / 2f;
        for (float x = startX; x < endX; x += Block.SIZE) {
            for (float y = startY; y < endY; y += Block.SIZE) {
                Renderable img = new RectangleRenderable(ColorSupplier.approximateColor(BASE_LEAF_COLOR));
                Leaf leaf = new Leaf(new Vector2(x, y), img);
                gameObjects.addGameObject(leaf, leavesLayer);
            }
        }
    }


    /**
     * Randomize the decision of planting a tree
     *
     * @param seed the seed for the random
     * @param x    the x position of the tree
     * @return true if should plant a tree, else false
     */
    public static boolean shouldPlantTree(int seed, int x) {
        return new Random(Objects.hash(seed, x)).nextInt(RANDOM_MAX_BOUND) < THRESHOLD;
    }

    /**
     * Randomize the tree's trunk height between MINIMUM_TRUNK_HEIGHT and MAXIMUM_TRUNK_HEIGHT
     *
     * @param seed the seed for the random
     * @param x    the x position of the tree
     * @return the height of the tree
     */
    private static int getRandomTruckHeight(int seed, int x) {
        return new Random(Objects.hash(seed, x)).nextInt(MAXIMUM_TRUNK_HEIGHT - MINIMUM_TRUNK_HEIGHT) +
                MINIMUM_TRUNK_HEIGHT;
    }
}
