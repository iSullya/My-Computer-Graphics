package First_Raytracer;

public class IntersectionResult {
    Sphere sphere;
    double t;

    IntersectionResult(Sphere sphere, double t){
        this.sphere = sphere;
        this.t = t;
    }
}
