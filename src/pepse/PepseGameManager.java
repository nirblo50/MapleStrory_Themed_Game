package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.*;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.util.NumericEnergyCounter;
import pepse.world.*;
import pepse.world.Monsters.MonsterFactory;
import pepse.world.daynight.Cloud;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;

import java.awt.*;
import java.util.function.Consumer;

public class PepseGameManager extends GameManager {

    /************ Game Settings Constants ***************/
    public static final String WINDOWS_NAME = "Pepse Game";
    private static final int BOARD_HEIGHT = 690;
    private static final int BOARD_WIDTH = 1140;
    private static final int RANDOM_SEED = 1234567;
    private static final String BACKGROUND_MUSIC_PATH = "assets/mapleStory.wav";

    /************** avatar properties ***************/
    public static final int AVATAR_LAYER = Layer.DEFAULT;
    public static final int PARACHUTE_LAYER = Layer.BACKGROUND;

    /************** day/night properties ***************/
    public static final int SUN_LAYER = Layer.BACKGROUND;
    public static final int SKY_LAYER = Layer.BACKGROUND;
    public static final int NIGHT_LAYER = Layer.FOREGROUND;

    private static final float NIGHT_CYCLE_LEN = 48;
    private static final float SUNSET_CYCLE = NIGHT_CYCLE_LEN * 2;
    private static final Color HALO_COLOR = new Color(255, 255, 0, 20);

    /************** clouds properties ***************/
    public static final int CLOUD_LAYER = SUN_LAYER + 1;
    private static final int CLOUD1_START = 650;
    private static final int CLOUD1_CYCLE_LEN = 20;
    private static final int CLOUD2_CYCLE_LEN = 25;
    private static final int CLOUD2_START = 20;
    private static final Vector2 START_POSITION_CLOUD_1 = new Vector2(0f, 100f);
    private static final Vector2 START_POSITION_CLOUD_2 = new Vector2(0f, -70f);

    /************** Trees properties ***************/
    public static final int TRUNK_LAYER = Layer.STATIC_OBJECTS + 1;
    public static final int LEAVES_LAYER = 1;

    /************** Terrain properties ***************/
    public static final int TOP_TERRAIN_LAYER = Layer.STATIC_OBJECTS;
    public static final int BOTTOM_TERRAIN_LAYER = Layer.STATIC_OBJECTS - 1;

    /************** Monsters properties ***************/
    public static final int MONSTERS_LAYER = 2;

    /************** energy text properties ***************/
    public static final int NUMERIC_ENERGY_LAYER = Layer.UI;
    private static final int PADDING = 30;
    private static final int EPSILON = 3;
    private static final float TEXT_PADDING = 0.1f;
    private static final Vector2 ENERGY_TEXT_SIZE = new Vector2(30f, 30f);

    /************ Class attributes ***********/
    private int worldLeftEnd, worldRightEnd;
    private Vector2 windowDimensions;
    private Terrain terrain;
    private Avatar avatar;
    private NumericEnergyCounter energyCounter;
    private MonsterFactory monsterFactory;
    private Tree tree;

    /**
     * The constructor of Pepse Game Manager
     *
     * @param windowTitle      The name of the window
     * @param windowDimensions the windows dimension
     */
    public PepseGameManager(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
    }


    /**
     * Initializes the PepseGameManager
     *
     * @param imageReader      the image reader to use for loading images
     * @param soundReader      the sound reader to use for loading sounds
     * @param inputListener    the user input listener to use for handling user input
     * @param windowController the window controller to use for controlling the game window
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {

        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        Sound backgroundSound = soundReader.readSound(BACKGROUND_MUSIC_PATH);
        backgroundSound.playLooped();
        this.windowDimensions = windowController.getWindowDimensions();

        worldLeftEnd = (int) (-windowDimensions.x());
        worldRightEnd = (int) (windowDimensions.x() * 2f);

        skyCreator(imageReader);
        terrainCreator(worldLeftEnd, worldRightEnd);
        tree = new Tree(gameObjects(), RANDOM_SEED, terrain, LEAVES_LAYER, TRUNK_LAYER);
        tree.createInRange(worldLeftEnd + EPSILON * Block.SIZE, worldRightEnd - EPSILON * Block.SIZE);
        createAvatar(inputListener, imageReader);
        numericEnergyCreator();
        monsterFactory = new MonsterFactory(imageReader, RANDOM_SEED);
        initialMonstersCreator(worldLeftEnd, worldRightEnd, (int) windowDimensions.x());
        applyLayersCollisions();
    }

    /**
     * Creates the monsters in all the current game world
     */
    private void initialMonstersCreator(int start, int end, int jumps) {
        for (int x = start; x < end; x += jumps)
            createSingleMonster(x, x + jumps);
    }

    /**
     * Creates a new monster inside a given range
     */
    private void createSingleMonster(int start, int end) {
        for (int x = start + Block.SIZE; x < end - Block.SIZE * EPSILON; x += Block.SIZE) {
            if (!Tree.shouldPlantTree(RANDOM_SEED, x)) {
                int y = (int) terrain.groundHeightAt(x);
                gameObjects().addGameObject(
                        monsterFactory.createRandomMonster(x, new Vector2(x, y)), MONSTERS_LAYER);
                return;
            }
        }
    }

    /**
     * Sets the collision behaviour for all the layers that should collide
     */
    private void applyLayersCollisions() {
        gameObjects().layers().shouldLayersCollide(LEAVES_LAYER, TOP_TERRAIN_LAYER, true);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TRUNK_LAYER, true);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, CLOUD_LAYER, true);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, MONSTERS_LAYER, true);
        gameObjects().layers().shouldLayersCollide(MONSTERS_LAYER, TOP_TERRAIN_LAYER, true);
        gameObjects().layers().shouldLayersCollide(MONSTERS_LAYER, TRUNK_LAYER, true);
    }

    /**
     * Creates the clouds
     */
    private void cloudsCreator(ImageReader imageReader) {
        Cloud.create(gameObjects(), CLOUD_LAYER, windowDimensions, CLOUD1_CYCLE_LEN,
                imageReader, START_POSITION_CLOUD_1, CLOUD1_START);
        Cloud.create(gameObjects(), CLOUD_LAYER, windowDimensions, CLOUD2_CYCLE_LEN,
                imageReader, START_POSITION_CLOUD_2, CLOUD2_START);
    }

    /**
     * Creates the energy text
     */
    private void numericEnergyCreator() {
        energyCounter = new NumericEnergyCounter(
                new Vector2(windowDimensions.x() * TEXT_PADDING, windowDimensions.y() * TEXT_PADDING),
                ENERGY_TEXT_SIZE, avatar.getEnergy());
        gameObjects().addGameObject(energyCounter, NUMERIC_ENERGY_LAYER);
    }

    /**
     * Update the game stats every frame
     *
     * @param deltaTime The time past from last update
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Vector2 curPosition = avatar.getCenter();
        Sun.circleCenter = new Vector2(camera().getCenter().x(), windowDimensions.y());
        energyCounter.setTopLeftCorner(new Vector2(camera().getTopLeftCorner().x() + PADDING,
                camera().getTopLeftCorner().y() + PADDING));

        if (curPosition.x() > worldRightEnd - windowDimensions.x()) {
            createWorld(Direction.right);
            deleteWorld(Direction.left);
            worldRightEnd += windowDimensions.x();
            worldLeftEnd += windowDimensions.x();
        }

        if (curPosition.x() < worldLeftEnd + windowDimensions.x()) {
            createWorld(Direction.left);
            deleteWorld(Direction.right);
            worldRightEnd -= windowDimensions.x();
            worldLeftEnd -= windowDimensions.x();
        }
    }

    /**
     * Creates a new world in the game
     *
     * @param world The world side to create the new world
     */
    private void createWorld(Direction world) {
        int start, end;

        if (world == Direction.right) {     // create right world
            start = worldRightEnd;
            end = (int) (start + windowDimensions.x());
        } else {                            // create left world
            end = worldLeftEnd;
            start = (int) (end - windowDimensions.x());
        }

        this.terrain.createInRange(start, end);
        tree.createInRange(start + EPSILON * Block.SIZE, end - EPSILON * Block.SIZE);
        createSingleMonster(start, end);
    }

    /**
     * Deletes all the object in a given layer and world
     */
    private void deleteObjectsInLayer(Direction world, int layer) {
        Consumer<GameObject> deleteTerrain = (object) -> {
            if (world == Direction.left) {
                if (object.getTopLeftCorner().x() < worldLeftEnd + windowDimensions.x())
                    gameObjects().removeGameObject(object, layer);
            } else if (object.getTopLeftCorner().x() > worldRightEnd - windowDimensions.x())
                gameObjects().removeGameObject(object, layer);
        };
        gameObjects().objectsInLayer(layer).forEach(deleteTerrain);
    }

    /**
     * Deletes all the object in a given world
     */
    private void deleteWorld(Direction world) {
        deleteObjectsInLayer(world, TOP_TERRAIN_LAYER);
        deleteObjectsInLayer(world, BOTTOM_TERRAIN_LAYER);
        deleteObjectsInLayer(world, TRUNK_LAYER);
        deleteObjectsInLayer(world, LEAVES_LAYER);
        deleteObjectsInLayer(world, MONSTERS_LAYER);
    }

    /**
     * Creates the avatar
     */
    private void createAvatar(UserInputListener inputListener, ImageReader imageReader) {
        float initialX = windowDimensions.x() / 2f;
        Vector2 initialPosition = new Vector2(
                initialX, terrain.groundHeightAt(initialX) - Block.SIZE * EPSILON);
        avatar = Avatar.create(gameObjects(), Layer.DEFAULT, initialPosition, inputListener, imageReader);
        Vector2 distance = windowDimensions.mult(0.5f).subtract(avatar.getTopLeftCorner());
        setCamera(new Camera(avatar, distance, windowDimensions, windowDimensions));
    }

    /**
     * Creates a new terrain and adds it to the list of game objects.
     */
    private void terrainCreator(int start, int end) {
        this.terrain = new Terrain(this.gameObjects(), TOP_TERRAIN_LAYER, windowDimensions, RANDOM_SEED);
        terrain.createInRange(start, end);
    }

    /**
     * Creates a new sky and adds it to the list of game objects.
     */
    private void skyCreator(ImageReader imageReader) {
        Sky.create(gameObjects(), windowDimensions, SKY_LAYER);
        Night.create(gameObjects(), NIGHT_LAYER, windowDimensions, NIGHT_CYCLE_LEN);
        GameObject sun = Sun.create(gameObjects(), SUN_LAYER, windowDimensions, SUNSET_CYCLE);
        SunHalo.create(gameObjects(), SUN_LAYER, sun, HALO_COLOR);
        cloudsCreator(imageReader);
    }

    /**
     * The main game loop
     *
     * @param args The user arguments
     */
    public static void main(String[] args) {
        new PepseGameManager(WINDOWS_NAME, new Vector2(BOARD_WIDTH, BOARD_HEIGHT)).run();
    }

    /**
     * This enum represent a direction - in this context it represents the side of a world
     */
    private enum Direction {right, left}
}