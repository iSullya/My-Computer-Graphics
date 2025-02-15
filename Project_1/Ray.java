package Project_1;

public class Ray {
    public Vector3 origin;      // Starting point of the ray
    public Vector3 direction;   // Direction in which the ray points

    public Ray(Vector3 origin, Vector3 direction) {
        this.origin = origin;
        this.direction = direction;
    }
}