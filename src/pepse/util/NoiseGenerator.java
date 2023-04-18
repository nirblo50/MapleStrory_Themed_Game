package pepse.util;

import java.util.Random;

/**
 * This class represents a noise generator. It has two constructors: one that takes in a double seed value,
 * and another that generates a random seed value using a Gaussian distribution. The class has a method
 * called {@link #noise(double, double, double)} that takes in three double values representing the x, y,
 * and z coordinates, and returns a double value representing the noise at those coordinates. It also has
 * a method called {@link #smoothNoise(double, double, double)} that takes in the same three coordinates
 * and returns a smoothed version of the noise at those coordinates. The class has a method called
 * {@link #setSeed(double)} which allows the user to set the seed value, and a method called
 * {@link #getSeed()} which returns the current seed value.
 * link for source - <a href="https://gist.github.com/alksily/7a85a1898e65c936f861ee93516e397d">...</a>
 **/
public class NoiseGenerator {
    private double seed;
    private long default_size;
    private int[] p;

    /**
     * Constructs a new noise generator with the given seed value.
     *
     * @param seed the seed value to use for the noise generator
     */
    public NoiseGenerator(double seed) {
        this.seed = seed;
        init();
    }

    /**
     * Constructs a new noise generator with a random seed value generated using a Gaussian distribution.
     */
    public NoiseGenerator() {
        this.seed = new Random().nextGaussian() * 255;
        init();
    }
    /**
     * Initializes the permutation array used in the noise generation process.
     */
    private void init() {
        // Initialize the permutation array.
        this.p = new int[512];
        int[] permutation = new int[]{151, 160, 137, 91, 90, 15, 131, 13, 201,
                95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99,
                37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26,
                197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88,
                237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74,
                165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111,
                229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40,
                244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76,
                132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159,
                86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250,
                124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207,
                206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170,
                213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155,
                167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113,
                224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242,
                193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235,
                249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184,
                84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236,
                205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66,
                215, 61, 156, 180};
        this.default_size = 35;

        // Populate it
        for (int i = 0; i < 256; i++) {
            p[256 + i] = p[i] = permutation[i];
        }

    }

    /**
     * Sets the seed value for the noise generator.
     *
     * @param seed the seed value to set
     */
    public void setSeed(double seed) {

        this.seed = seed;
    }

    /**
     * Returns the current seed value of the noise generator.
     *
     * @return the current seed value
     */
    public double getSeed() {
        return this.seed;
    }

    /**
     * Calculates and returns the noise at the given x, y, and z coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the noise at the given coordinates
     */
    public double noise(double x, double y, double z) {
        double value = 0.0;
        double size = default_size;
        double initialSize = size;

        while (size >= 1) {
            value += smoothNoise((x / size), (y / size), (z / size)) * size;
            size /= 2.0;
        }

        return value / initialSize;
    }

    /**
     * Calculates and returns a smoothed version of the noise at the given x, y, and z coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the smoothed noise at the given coordinates
     */
    public double smoothNoise(double x, double y, double z) {
        // Offset each coordinate by the seed value
        x += this.seed;
        y += this.seed;
        x += this.seed;

        int X = (int) Math.floor(x) & 255; // FIND UNIT CUBE THAT
        int Y = (int) Math.floor(y) & 255; // CONTAINS POINT.
        int Z = (int) Math.floor(z) & 255;

        x -= Math.floor(x); // FIND RELATIVE X,Y,Z
        y -= Math.floor(y); // OF POINT IN CUBE.
        z -= Math.floor(z);

        double u = fade(x); // COMPUTE FADE CURVES
        double v = fade(y); // FOR EACH OF X,Y,Z.
        double w = fade(z);

        int A = p[X] + Y;
        int AA = p[A] + Z;
        int AB = p[A + 1] + Z; // HASH COORDINATES OF
        int B = p[X + 1] + Y;
        int BA = p[B] + Z;
        int BB = p[B + 1] + Z; // THE 8 CUBE CORNERS,

        return lerp(w, lerp(v, lerp(u, grad(p[AA], x, y, z),    // AND ADD
                                grad(p[BA], x - 1, y, z)), // BLENDED
                        lerp(u, grad(p[AB], x, y - 1, z),    // RESULTS
                                grad(p[BB], x - 1, y - 1, z))),// FROM 8
                lerp(v, lerp(u, grad(p[AA + 1], x, y, z - 1),    // CORNERS
                                grad(p[BA + 1], x - 1, y, z - 1)), // OF CUBE
                        lerp(u, grad(p[AB + 1], x, y - 1, z - 1),
                                grad(p[BB + 1], x - 1, y - 1, z - 1))));
    }

    /**
     * Calculates and returns the fade curve for the given value.
     *
     * @param t the value to calculate the fade curve for
     * @return the fade curve for the given value
     */
    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    /**
     * Performs linear interpolation between the given values.
     *
     * @param t the interpolation factor
     * @param a the first value
     * @param b the second value
     * @return the interpolated value
     */
    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    /**
     * Calculates and returns the gradient for the given hash code and coordinates.
     *
     * @param hash the hash code
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the gradient for the given hash code and coordinates
     */
    private double grad(int hash, double x, double y, double z) {
        int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE
        double u = h < 8 ? x : y, // INTO 12 GRADIENT DIRECTIONS.
                v = h < 4 ? y : h == 12 || h == 14 ? x : z;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
}