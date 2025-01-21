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

    
    double dot(Vector3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    
    double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    
    Vector3 normalize() {
        double len = length();
        return new Vector3(x / len, y / len, z / len);
    }
}
