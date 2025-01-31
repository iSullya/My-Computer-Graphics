package Assignment_1;

/**
 * Represents a 3D vector with basic vector operations.
 */
public class Vector3 {
    double x, y, z;

    // Constructor to initialize the vector
    Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Subtracts another vector from this vector.
     *
     * @param other The vector to subtract.
     * @return The resulting vector.
     */
    Vector3 subtract(Vector3 other) {
        return new Vector3(x - other.x, y - other.y, z - other.z);
    }

    /**
     * Multiplies this vector by a scalar.
     *
     * @param scalar The scalar to multiply by.
     * @return The resulting vector.
     */
    Vector3 multiply(double scalar) {
        return new Vector3(x * scalar, y * scalar, z * scalar);
    }

    /**
     * Adds another vector to this vector.
     *
     * @param other The vector to add.
     * @return The resulting vector.
     */
    Vector3 add(Vector3 other) {
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }

    /**
     * Computes the dot product of this vector with another vector.
     *
     * @param other The other vector.
     * @return The dot product.
     */
    double dot(Vector3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    /**
     * Computes the length (magnitude) of this vector.
     *
     * @return The vector's length.
     */
    double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * Normalizes this vector (makes its length equal to 1).
     *
     * @return The normalized vector.
     */
    Vector3 normalize() {
        double len = length();
        return new Vector3(x / len, y / len, z / len);
    }
}
