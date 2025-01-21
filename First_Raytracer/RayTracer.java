package First_Raytracer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import Light.Light;


public class RayTracer {
    public static void main(String[] args) {
        // Define the image dimensions
        int width = 500, height = 500;

        // Create a blank image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Define the spheres in the scene
        Sphere[] spheres = {
            new Sphere(new Vector3(0, -1, 3), 1, Color.RED, 500),    // Red sphere in the center
            new Sphere(new Vector3(2, 0, 4), 1, Color.BLUE, 500),    // Blue sphere to the right
            new Sphere(new Vector3(-2, 0, 4), 1, Color.GREEN, 10),   // Green sphere to the left
            new Sphere(new Vector3(0, -5001, 0), 5000, Color.YELLOW, 1000) // Large yellow sphere (acts as a floor)
        };

        Light[] lights = {
            new Light(new Vector3(2, 1, 0), 0.6),                      // Point light
            new Light(new Vector3(1, 4, 4), 0.2, Light.LightType.DIRECTIONAL), // Directional light
            new Light(0.2, Light.LightType.AMBIENT)                      // Ambient light
        };

        // Set the camera's position
        Vector3 camera = new Vector3(0, 0, 0);

        // Render each pixel in the image
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Map the pixel coordinates to normalized device coordinates
                double px = (x - width / 2.0) / width;
                double py = -(y - height / 2.0) / height;

                // Create a direction vector from the camera through the pixel
                Vector3 direction = new Vector3(px, py, 1).normalize();

                // Trace the ray to find the color at this pixel
                Color color = traceRay(camera, direction, spheres, lights);

                // Set the pixel color in the image
                image.setRGB(x, y, color.getRGB());
            }
        }

        // Save the rendered image to a file
        try {
            ImageIO.write(image, "png", new File("result with specular ref.png"));
            System.out.println("Saved as result.png");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static double computeLighting(Vector3 point, Vector3 normal, Light[] lights, double specular, Vector3 viewDir) {
        double intensity = 0.0;
    
        for (Light light : lights) {
            if (light.type == Light.LightType.AMBIENT) {
                // Ambient light contributes equally to all points
                intensity += light.intensity;
            } else {
                Vector3 lightDir;
                if (light.type == Light.LightType.POINT) {
                    // Compute direction from point to light
                    lightDir = light.position.subtract(point).normalize();
                } else { // DIRECTIONAL
                    // Use the fixed direction of the light
                    lightDir = light.direction;
                }
    
                // Compute diffuse reflection
                double nDotL = normal.dot(lightDir);
                if (nDotL > 0) {
                    intensity += light.intensity * nDotL;
                }

                // Compute specular reflection
                if (specular != -1) {
                    Vector3 reflectDir = normal.multiply(2 * nDotL).subtract(lightDir).normalize();
                    double rDotV = reflectDir.dot(viewDir);

                    if (rDotV > 0) {
                        intensity += light.intensity * Math.pow(rDotV, specular);
                        
                    }
                    
                }
                
            }
            
        }
    
        return intensity;
    }


    static Color traceRay(Vector3 origin, Vector3 direction, Sphere[] spheres, Light[] lights) {
        Sphere closestSphere = null;
        double minT = Double.MAX_VALUE;

        // Check for intersection with each sphere
        for (Sphere sphere : spheres) {
            Double t = sphere.intersect(origin, direction);

            // Update the closest intersection
            if (t != null && t < minT) {
                minT = t;
                closestSphere = sphere;
            }
        }


        if (closestSphere == null) return Color.WHITE;

        // Compute intersection point and normal
        Vector3 intersection = origin.add(direction.multiply(minT));
        Vector3 normal = intersection.subtract(closestSphere.center).normalize();
        

        // Compute lighting
        Vector3 viewDir = direction.multiply(-1).normalize();
        double intensity = computeLighting(intersection, normal, lights, closestSphere.specular, viewDir);

        // Apply lighting to the sphere's color
        int r = (int) (closestSphere.color.getRed() * intensity);
        int g = (int) (closestSphere.color.getGreen() * intensity);
        int b = (int) (closestSphere.color.getBlue() * intensity);

        // Clamp the color values to the range [0, 255]
        r = Math.min(255, Math.max(0, r));
        g = Math.min(255, Math.max(0, g));
        b = Math.min(255, Math.max(0, b));

        return new Color(r, g, b);
    }
}
