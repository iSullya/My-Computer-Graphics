package Project_1;

public class Light {
    enum LightType {POINT, DIRECTIONAL, AMBIENT, AREA}

    LightType type;
    Vector3 position;       // for POINT light/Area
    Vector3 direction;      // for DIRECTIONAL light
    double intensity;

    // For area lights
    public double width;   // horizontal size
    public double height;  // vertical size
    public Vector3 u;      // local right vector for area plane
    public Vector3 v;      // local up vector for area plane

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

    // AREA light constructor 
    public Light(Vector3 position, double intensity, double width, double height,
                 Vector3 u, Vector3 v) {
        this.type = LightType.AREA;
        this.position = position;
        this.intensity = intensity;
        this.width = width;
        this.height = height;
        this.u = u.normalize().multiply(width);  // scaled "right" vector
        this.v = v.normalize().multiply(height); // scaled "up" vector
    }


}
