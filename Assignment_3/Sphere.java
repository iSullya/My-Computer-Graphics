package Assignment_3;

import java.awt.Color;

/**
 * Represents a sphere in the 3D scene.
 */
public class Sphere {
    Vector3 center;  // Center of the sphere
    double radius;   // Radius of the sphere
    Color color;     // Color of the sphere
    double specular; // shininess factor
    double reflective; // reflection factor

    // Constructor to initialize the sphere
    Sphere(Vector3 center, double radius, Color color, double specular, double reflective) {
        this.center = center;
        this.radius = radius;
        this.color = color;
        this.specular = specular;
        this.reflective = reflective;
    }

    /**
     * Determines if a ray intersects with this sphere.
     *
     * @param origin    The origin of the ray.
     * @param direction The direction of the ray.
     * @return The distance to the intersection point, or null if no intersection.
     */
    Double intersect(Vector3 origin, Vector3 direction) {
        Vector3 oc = origin.subtract(center);

        // Quadratic coefficients
        double a = direction.dot(direction);
        double b = 2.0 * oc.dot(direction);
        double c = oc.dot(oc) - radius * radius;

        // Compute the discriminant
        double discriminant = b * b - 4 * a * c;

        // If the discriminant is negative, there's no intersection
        if (discriminant < 0) {
            return null;
        }

        // Compute the two possible intersection points
        double t1 = (-b - Math.sqrt(discriminant)) / (2.0 * a);
        double t2 = (-b + Math.sqrt(discriminant)) / (2.0 * a);

        // Return the nearest positive intersection
        if (t1 > 0) return t1;
        if (t2 > 0) return t2;

        return null;
    }
}
