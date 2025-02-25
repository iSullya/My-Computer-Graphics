package Assignment_4;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Main class for rendering a simple raytraced scene.
 */
public class RayTracer {
    public static void main(String[] args) {
        // Define the image dimensions
        int width = 600, height = 600;

        // Create a blank image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Define the spheres in the scene
        Sphere[] spheres = {
            new Sphere(new Vector3(0, -1, 3), 1, Color.RED, 500, 0.2),    // Red sphere in the center
            new Sphere(new Vector3(2, 0, 4), 1, Color.BLUE, 500, 0.3),    // Blue sphere to the right
            new Sphere(new Vector3(-2, 0, 4), 1, Color.GREEN, 10, 0.4),   // Green sphere to the left
            new Sphere(new Vector3(0, -5001, 0), 5000, Color.YELLOW, 1000, 0.5)  // Yellow sphere that kinda represent a floor  
        };

        Light[] lights = {
            new Light(new Vector3(2, 1, 0), 0.6),                      // Point light
            new Light(new Vector3(1, 4, 4), 0.2, Light.LightType.DIRECTIONAL), // Directional light
            new Light(0.2, Light.LightType.AMBIENT)                      // Ambient light
        };

        // Set the camera's position
        Vector3 camera = new Vector3(0, 0, 0);

        int samples = 25; // Increase sampling for more crisp image. Number of rays per pixel 

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color finalColor = superSampling(x, y, width, height, samples, camera, spheres, lights);
                image.setRGB(x, y, finalColor.getRGB());
            }
        }


        // Save the rendered image to a file
        try {
            ImageIO.write(image, "png", new File("Assignment_4/Results/Assignment_4_result.png"));
            System.out.println("Saved as result_with_specular_ref.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Color superSampling(int x, int y, int width, int height, int samples, Vector3 camera, Sphere[] spheres, Light[] lights) {
        double rSum = 0, gSum = 0, bSum = 0;  // accumulated values for red, green, blue
        int sqrtSamples = (int) Math.sqrt(samples); // To distribute rays evenly -grid size
    
        for (int sx = 0; sx < sqrtSamples; sx++) {
            for (int sy = 0; sy < sqrtSamples; sy++) {
                double px = (x + (sx + Math.random()) / sqrtSamples - width / 2.0) / width;
                double py = -(y + (sy + Math.random()) / sqrtSamples - height / 2.0) / height;
                // double px = (x + sx / sqrtSamples - width / 2.0) / width;
                // double py = -(y + sy / sqrtSamples - height / 2.0) / height;
    
                Vector3 direction = new Vector3(px, py, 1).normalize();
                Color sampleColor = traceRay(camera, direction, spheres, lights, 3);
    
                rSum += sampleColor.getRed();
                gSum += sampleColor.getGreen();
                bSum += sampleColor.getBlue();
            }
        }
    
        // Average colors
        int rAvg = (int) Math.round(rSum / samples);
        int gAvg = (int) Math.round(gSum / samples);
        int bAvg = (int) Math.round(bSum / samples);
    
        // Clamp values to [0, 255]
        return new Color(
            Math.min(255, Math.max(0, rAvg)),
            Math.min(255, Math.max(0, gAvg)),
            Math.min(255, Math.max(0, bAvg))
        );
    }

    static double computeLighting(Vector3 point, Vector3 normal, Light[] lights, Sphere[] spheres, double specular, Vector3 viewDir) {
        double intensity = 0.0;
        double epsilon = 0.001; // offset to avoid self-intersection

        for (Light light : lights) {
            if (light.type == Light.LightType.AMBIENT) {
                intensity += light.intensity;
            } else {
                Vector3 lightDir;
                double tMax;

                if (light.type == Light.LightType.POINT) {
                    lightDir = light.position.subtract(point).normalize();
                    tMax = light.position.subtract(point).length();
                } else { // DIRECTIONAL
                    // Use the fixed direction of the light (pointing from the light source)
                    lightDir = light.direction;
                    tMax = Double.POSITIVE_INFINITY;
                }

                // Offset shadow ray origin to avoid self-intersection
                Vector3 shadowRayOrigin = point.add(normal.multiply(epsilon));
                Object[] shadowData = closestIntersection(shadowRayOrigin, lightDir, spheres, 0, tMax);

                //check if the point is in the shadow |I AM THE SHADOW|
                if (shadowData[0] != null) continue; 

                // Compute diffuse reflection
                double nDotL = normal.dot(lightDir);
                if (nDotL > 0) {
                    intensity += light.intensity * nDotL / (normal.length() * lightDir.length());
                }

                // Compute specular reflection
                if (specular != -1) {
                    Vector3 reflectDir = normal.multiply(2 * nDotL).subtract(lightDir).normalize();
                    double rDotV = reflectDir.dot(viewDir);

                    if (rDotV > 0) {
                        intensity += light.intensity * Math.pow(rDotV / (reflectDir.length() * viewDir.length()), specular);
                    }
                }
            }
        }

        // Clamp intensity to [0, 1]
        intensity = Math.min(intensity, 1.0);
        intensity = Math.max(intensity, 0);

        return intensity;
    }

    static Color traceRay(Vector3 origin, Vector3 direction, Sphere[] spheres, Light[] lights, int recursionDepth) {
        // base case: stop after 3 boumces 
        // if (recursionDepth <= 0) {
        //     return Color.BLACK;  // zero light contribution
        // }
        Object[] intersectionData = closestIntersection(origin, direction, spheres, 0.001, Double.MAX_VALUE);

        Sphere closestSphere = (Sphere) intersectionData[0];
        double closestT = (double) intersectionData[1];

        if (closestSphere == null) return Color.BLACK;  //bakckground

        // Compute intersection point and normal
        Vector3 intersection = origin.add(direction.multiply(closestT));
        Vector3 normal = intersection.subtract(closestSphere.center).normalize();

        // Compute view direction (from intersection to camera)
        Vector3 viewDir = origin.subtract(intersection).normalize();

        // Compute lighting
        double intensity = computeLighting(intersection, normal, lights, spheres, closestSphere.specular, viewDir);

        Color localColor = multiplyColor(closestSphere.color, intensity);

        // check if sphere is not reflective
        if (closestSphere.reflective <= 0 || recursionDepth <= 0) {
            return localColor;
        }
        
        // compute reflection direction
        Vector3 reflectDir = direction.reflect(normal).normalize();
        // 1e-3 to avoid self intersection
        Vector3 reflectedRayOriging = intersection.add(normal.multiply(0.001)); 
        // recursivly trace reflected ray
        Color reflectedColor = traceRay(reflectedRayOriging, reflectDir, spheres, lights, recursionDepth -1);

        // blend local color (og) and reflected one
        return blendColors(localColor, closestSphere.reflective, reflectedColor);
    }

    static Color multiplyColor(Color color, double intensity){
        int r = (int) (color.getRed() * intensity);
        int g = (int) (color.getGreen() * intensity);
        int b = (int) (color.getBlue() * intensity);
        return new Color(
            Math.min(255, Math.max(0, r)),
            Math.min(255, Math.max(0, g)),
            Math.min(255, Math.max(0, b))
        );
    }

    static Color blendColors(Color locaColor, double reflectivity, Color reflectedColor){
        double r = locaColor.getRed() * (1 - reflectivity) + reflectedColor.getRed() * reflectivity;
        double g = locaColor.getGreen() * (1 - reflectivity) + reflectedColor.getGreen() * reflectivity;
        double b = locaColor.getBlue() * (1 - reflectivity) + reflectedColor.getBlue() * reflectivity;

        return new Color(
            (int) Math.min(255, Math.max(0, r)),
            (int) Math.min(255, Math.max(0, g)),
            (int) Math.min(255, Math.max(0, b))
        );
    }

    static Object[] closestIntersection(Vector3 origin, Vector3 direction, Sphere[] spheres,
    double tMin, double tMax){
        Sphere closestSphere = null;
        double closestT = Double.MAX_VALUE;

        for (Sphere sphere : spheres) {
            Double t = sphere.intersect(origin, direction);

            // Update the closest intersection
            if (t != null && t > tMin && t < tMax && t < closestT) {
                closestT = t;
                closestSphere = sphere;
            }
        }
        return new Object[]{closestSphere, closestT};
    }
}