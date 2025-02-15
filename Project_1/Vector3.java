package Project_1;

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

    
    Vector3 subtract(Vector3 other) {
        return new Vector3(x - other.x, y - other.y, z - other.z);
    }

    
    Vector3 multiply(double scalar) {
        return new Vector3(x * scalar, y * scalar, z * scalar);
    }


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

    Vector3 reflect(Vector3 normal){
        double dot = this.dot(normal);
        return this.subtract(normal.multiply(2 * dot));
    }

    Vector3 cross(Vector3 other) {
        return new Vector3(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        );
    }
}
