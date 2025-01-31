package Assignment_3;

public class Light {
    enum LightType {POINT, DIRECTIONAL, AMBIENT}

    LightType type;
    Vector3 position;       // for POINT light
    Vector3 direction;      // for DIRECTIONAL light
    double intensity;

    // POINT light constructor
    public Light(Vector3 position, double intensity){
        this.type = LightType.POINT;
        this.position = position;
        this.intensity = intensity;
    }

    // DIRECTIONAL light constructor
    public Light(Vector3 direction, double intensity, LightType type){
        if (type != LightType.DIRECTIONAL) {
            throw new IllegalArgumentException("Invalid type for directional light");
        }
        this.type = type;
        this.direction = direction.normalize();
        this.intensity = intensity;
    }

    //AMBIENT light constructor
    public Light(double intensity, LightType type) {
        if (type != LightType.AMBIENT) {
            throw new IllegalArgumentException("Invalid type for ambient light");
        }
        this.type = type;
        this.intensity = intensity;
    }


}
