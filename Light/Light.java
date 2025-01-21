package Light;


import First_Raytracer.Vector3;

public class Light {
    public enum LightType { POINT, DIRECTIONAL, AMBIENT }

    public LightType type;
    public Vector3 position;  // For POINT lights
    public Vector3 direction; // For DIRECTIONAL lights
    public double intensity;  // Light intensity

    // Constructor for POINT lights
    public Light(Vector3 position, double intensity) {
        this.type = LightType.POINT;
        this.position = position;
        this.intensity = intensity;
    }

    // Constructor for DIRECTIONAL lights
    public Light(Vector3 direction, double intensity, LightType type) {
        if (type != LightType.DIRECTIONAL) {
            throw new IllegalArgumentException("Invalid type for directional light");
        }
        this.type = type;
        this.direction = direction.normalize();
        this.intensity = intensity;
    }

    // Constructor for AMBIENT lights
    public Light(double intensity, LightType type) {
        if (type != LightType.AMBIENT) {
            throw new IllegalArgumentException("Invalid type for ambient light");
        }
        this.type = type;
        this.intensity = intensity;
    }
    
}
