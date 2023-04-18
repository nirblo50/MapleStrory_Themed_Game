package pepse.world.Monsters;

import danogl.gui.ImageReader;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.Objects;
import java.util.Random;

/**
 * This class it a factory for creating monsters
 */
public class MonsterFactory {
    private static final Vector2 PIG_SIZE = new Vector2(80, 70);
    private static final Vector2 SNAIL_SIZE = new Vector2(80, 70);
    private static final float EPSILON = 30;
    private static final float TIME_BETWEEN_CLIPS = 0.1f;
    private static final String ASSETS_PIG_PIG_LEFT_1 = "assets/pig/pig_left_1.png";
    private static final String ASSETS_PIG_PIG_LEFT_2 = "assets/pig/pig_left_2.png";
    private static final String ASSETS_PIG_PIG_LEFT_3 = "assets/pig/pig_left_3.png";
    private static final String ASSETS_PIG_PIG_RIGHT_1 = "assets/pig/pig_right_1.png";
    private static final String ASSETS_PIG_PIG_RIGHT_2 = "assets/pig/pig_right_2.png";
    private static final String ASSETS_PIG_PIG_RIGHT_3 = "assets/pig/pig_right_3.png";
    private static final String ASSETS_SNAIL_SNAIL_LEFT_1 = "assets/snail/snail_left_1.png";
    private static final String ASSETS_SNAIL_SNAIL_LEFT_2 = "assets/snail/snail_left_2.png";
    private static final String ASSETS_SNAIL_SNAIL_LEFT_3 = "assets/snail/snail_left_3.png";
    private static final String ASSETS_SNAIL_SNAIL_RIGHT_1 = "assets/snail/snail_right_1.png";


    /**************  Attributes  **************/
    private final ImageReader imageReader;
    private final int seed;

    /**
     * Creates a new MonsterFactory factory
     *
     * @param imageReader The imageReader object
     * @param seed        The random seed
     */
    public MonsterFactory(ImageReader imageReader, int seed) {
        this.imageReader = imageReader;
        this.seed = seed;
    }

    /**
     * Creates and returns a new Monster object
     *
     * @param monster       The type of monster to return
     * @param topLeftCorner The top left corner to draw the monster
     * @return Monster object
     */
    public Monster create(Monsters monster, Vector2 topLeftCorner) {
        switch (monster) {
            case pig:
                return createPig(topLeftCorner);
            case snail:
                return createSnail(topLeftCorner);
            default:
                return null;
        }
    }

    /**
     * Creates a new pig monster
     *
     * @param topLeftCorner The top left corner to draw the monster
     * @return pig Monster object
     */
    private Monster createPig(Vector2 topLeftCorner) {
        Renderable leftImg1 = imageReader.readImage(ASSETS_PIG_PIG_LEFT_1, true);
        Renderable leftImg2 = imageReader.readImage(ASSETS_PIG_PIG_LEFT_2, true);
        Renderable leftImg3 = imageReader.readImage(ASSETS_PIG_PIG_LEFT_3, true);
        AnimationRenderable leftImg = new AnimationRenderable(new Renderable[]{leftImg1, leftImg2, leftImg3},
                TIME_BETWEEN_CLIPS);

        Renderable rightImg1 = imageReader.readImage(ASSETS_PIG_PIG_RIGHT_1, true);
        Renderable rightImg2 = imageReader.readImage(ASSETS_PIG_PIG_RIGHT_2, true);
        Renderable rightImg3 = imageReader.readImage(ASSETS_PIG_PIG_RIGHT_3, true);
        AnimationRenderable rightImg = new AnimationRenderable(new Renderable[]{rightImg1, rightImg2,
                rightImg3}, TIME_BETWEEN_CLIPS);

        Vector2 pigPosition = new Vector2(topLeftCorner.x(), topLeftCorner.y() - PIG_SIZE.y() - EPSILON);
        return new Monster(pigPosition, PIG_SIZE, leftImg, rightImg);
    }

    /**
     * Creates a new snail monster
     *
     * @param topLeftCorner The top left corner to draw the monster
     * @return snail Monster object
     */
    private Monster createSnail(Vector2 topLeftCorner) {
        Renderable leftImg1 = imageReader.readImage(ASSETS_SNAIL_SNAIL_LEFT_1, true);
        Renderable leftImg2 = imageReader.readImage(ASSETS_SNAIL_SNAIL_LEFT_2, true);
        Renderable leftImg3 = imageReader.readImage(ASSETS_SNAIL_SNAIL_LEFT_3, true);
        AnimationRenderable leftImg = new AnimationRenderable(new Renderable[]{leftImg1, leftImg2, leftImg3},
                TIME_BETWEEN_CLIPS);

        Renderable rightImg1 = imageReader.readImage(ASSETS_SNAIL_SNAIL_RIGHT_1, true);
        Renderable rightImg2 = imageReader.readImage(MonsterFactory.ASSETS_SNAIL_SNAIL_RIGHT_1, true);
        Renderable rightImg3 = imageReader.readImage(MonsterFactory.ASSETS_SNAIL_SNAIL_RIGHT_1, true);
        AnimationRenderable rightImg = new AnimationRenderable(new Renderable[]{rightImg1, rightImg2,
                rightImg3}, TIME_BETWEEN_CLIPS);

        Vector2 snailPosition = new Vector2(topLeftCorner.x(), topLeftCorner.y() - SNAIL_SIZE.y() - EPSILON);
        return new Monster(snailPosition, SNAIL_SIZE, leftImg, rightImg);
    }

    /**
     * Creates a radom new monster
     *
     * @param x          The x position to make the monster
     * @param topLeftCor The top left corner to draw the monster
     * @return A new random Monster
     */
    public Monster createRandomMonster(int x, Vector2 topLeftCor) {
        Monsters[] monsters = Monsters.values();
        Monsters monster = monsters[new Random(Objects.hash(seed, x)).nextInt(monsters.length)];
        return create(monster, topLeftCor);
    }

}
