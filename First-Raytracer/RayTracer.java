import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;


public class RayTracer {
    public static void main(String[] args) {
        // Define the image dimensions
        int width = 500, height = 500;

        // Create a blank image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Define the spheres in the scene
        Sphere[] spheres = {
            new Sphere(new Vector3(0, -1, 3), 1, Color.RED),    // Red sphere in the center
            new Sphere(new Vector3(2, 0, 4), 1, Color.BLUE),    // Blue sphere to the right
            new Sphere(new Vector3(-2, 0, 4), 1, Color.GREEN)   // Green sphere to the left
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
                Color color = traceRay(camera, direction, spheres);

                // Set the pixel color in the image
                image.setRGB(x, y, color.getRGB());
            }
        }

        // Save the rendered image to a file
        try {
            ImageIO.write(image, "png", new File("First-Raytracer\\result.png"));
            System.out.println("Saved as result.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static Color traceRay(Vector3 origin, Vector3 direction, Sphere[] spheres) {
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

        // Return the color of the closest sphere, or white if no intersection
        return closestSphere != null ? closestSphere.color : Color.WHITE;
    }
}
