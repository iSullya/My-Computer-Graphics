package Project_1;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;



public class RayTracer {
    public static void main(String[] args) {
        // image dimensions
        int width = 1200, height = 675;
        

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        List<Sphere> sphereList = new ArrayList<>();

        // 1) Large ground sphere
        sphereList.add(new Sphere(
            new Vector3(0, -1000, 0), 1000,
            Color.GRAY,   // color
            1000,         // specular
            0.0,          // reflective
            MaterialType.DIFFUSE
        ));

        // 2) Three main spheres (center glass, left diffuse, right metal)
        sphereList.add(new Sphere(
            new Vector3(0, 1, 0), 1.0,
            Color.WHITE, 500, 0.0, MaterialType.DIELECTRIC, 1.5
        ));
        sphereList.add(new Sphere(
            new Vector3(-4, 1, 0), 1.0,
            new Color(153, 76, 38), 500, 0.0, MaterialType.DIFFUSE
        ));
        sphereList.add(new Sphere(
            new Vector3(4, 1, 0), 1.0,
            new Color(200, 200, 200), 500, 1.0, MaterialType.METAL
        ));

        // 3)small random spheres >> grid size based on the book
        Random rand = new Random();
        for (int a = -11; a < 11; a++) {
            for (int b = -11; b < 11; b++) {
                double chooseMat = rand.nextDouble(); // random in [0..1]
                Vector3 center = new Vector3(
                    a + 0.9 * rand.nextDouble(),
                    0.2,
                    b + 0.9 * rand.nextDouble()
                );
                // Avoid placing small spheres too close to the big glass sphere at (0,1,0)
                if (center.subtract(new Vector3(0, 1, 0)).length() > 1.2) {
                    // Decide which material
                    if (chooseMat < 0.7) {
                        // Diffuse
                        Color diffuseColor = new Color(
                            rand.nextInt(256),
                            rand.nextInt(256),
                            rand.nextInt(256)
                        );
                        sphereList.add(new Sphere(
                            center, 0.2,
                            diffuseColor, 10, 0.0,
                            MaterialType.DIFFUSE
                        ));
                    } else if (chooseMat < 0.9) {
                        // Metal
                        Color metalColor = new Color(
                            128 + rand.nextInt(128),
                            128 + rand.nextInt(128),
                            128 + rand.nextInt(128)
                        );
                        // reflective = 1 => perfect mirror
                        sphereList.add(new Sphere(
                            center, 0.2,
                            metalColor, 500, 1.0,
                            MaterialType.METAL
                        ));
                    } else {
                        // Glass
                        sphereList.add(new Sphere(
                            center, 0.2,
                            Color.WHITE, 500, 0.0,
                            MaterialType.DIELECTRIC, 1.5
                        ));
                    }
                }
            }
        }

        // Convert the ArrayList to an array
        Sphere[] spheres = sphereList.toArray(new Sphere[0]);


        Light[] lights = {
            
            new Light(new Vector3(2, 4, 0), 3.0, 5.0, 5.0,
                      new Vector3(1, 0, 0), new Vector3(0, 0, 1)),
            
            new Light(0.1, Light.LightType.AMBIENT)
        };

        // Set the camera's position
        Vector3 lookFrom = new Vector3(13, 2, 3);   // Camera position
        Vector3 lookAt   = new Vector3(0, 0, 0);      // Target point
        Vector3 vup      = new Vector3(0, 1, 0);      // Up vector
        double vfov = 20;                           // Field-of-view in degrees
        double aspectRatio = (double) width / height; // Should be 1200/675
        Camera camera = new Camera(lookFrom, lookAt, vup, vfov, aspectRatio);

        int samples = 16; // Increase sampling for more crisp image. Number of rays per pixel 

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color finalColor = superSampling(x, y, width, height, samples, camera, spheres, lights);
                image.setRGB(x, y, finalColor.getRGB());
            }
        }


        // Save the rendered image to a file
        try {
            ImageIO.write(image, "png", new File("Project_1/Results/Assignment_6_result.png"));
            System.out.println("Saved as result_with_specular_ref.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Color superSampling(int x, int y, int width, int height, int samples, Camera camera, Sphere[] spheres, Light[] lights) {
        double rSum = 0, gSum = 0, bSum = 0;  // accumulated values for red, green, blue
        int sqrtSamples = (int) Math.sqrt(samples); // To distribute rays evenly -grid size
    
        for (int sx = 0; sx < sqrtSamples; sx++) {
            for (int sy = 0; sy < sqrtSamples; sy++) {
                double px = (x + (sx + Math.random()) / sqrtSamples) / (width - 1);
                double py = 1- ((y + (sy + Math.random()) / sqrtSamples) / (height - 1));

                // Generate a ray from the camera.
                Ray rRay = camera.getRay(px, py);
    
                // Vector3 direction = new Vector3(px, py, 1).normalize();
                // Color sampleColor = traceRay(camera, direction, spheres, lights, 3);
                Color sampleColor = traceRay(rRay.origin, rRay.direction, spheres, lights, 3);

    
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

    static double computeLighting(Vector3 point, Vector3 normal, Light[] lights, Sphere[] spheres,
                              double specular, Vector3 viewDir) {
    double intensity = 0.0;
    double epsilon = 0.001;

    for (Light light : lights) {
        if (light.type == Light.LightType.AMBIENT) {
            intensity += light.intensity;
        }
        else if (light.type == Light.LightType.POINT) {
            
            Vector3 lightDir = light.position.subtract(point).normalize();
            double tMax = light.position.subtract(point).length();

            // Shadow check
            Vector3 shadowRayOrigin = point.add(normal.multiply(epsilon));
            Object[] shadowData = closestIntersection(shadowRayOrigin, lightDir, spheres, 0.001, tMax);
            if (shadowData[0] != null) continue; // in shadow

            // Diffuse
            double nDotL = normal.dot(lightDir);
            if (nDotL > 0) {
                intensity += light.intensity * nDotL / (normal.length() * lightDir.length());
            }
            // Specular
            if (specular != -1) {
                Vector3 reflectDir = normal.multiply(2 * nDotL).subtract(lightDir).normalize();
                double rDotV = reflectDir.dot(viewDir);
                if (rDotV > 0) {
                    intensity += light.intensity * Math.pow(rDotV / (reflectDir.length() * viewDir.length()), specular);
                }
            }
        }
        else if (light.type == Light.LightType.DIRECTIONAL) {
            // Existing code for directional
            Vector3 lightDir = light.direction;
            double tMax = Double.POSITIVE_INFINITY;

            // Shadow check
            Vector3 shadowRayOrigin = point.add(normal.multiply(epsilon));
            Object[] shadowData = closestIntersection(shadowRayOrigin, lightDir, spheres, 0.001, tMax);
            if (shadowData[0] != null) continue;

            // Diffuse
            double nDotL = normal.dot(lightDir);
            if (nDotL > 0) {
                intensity += light.intensity * nDotL / (normal.length() * lightDir.length());
            }
            // Specular
            if (specular != -1) {
                Vector3 reflectDir = normal.multiply(2 * nDotL).subtract(lightDir).normalize();
                double rDotV = reflectDir.dot(viewDir);
                if (rDotV > 0) {
                    intensity += light.intensity * Math.pow(rDotV / (reflectDir.length() * viewDir.length()), specular);
                }
            }
        }
        else if (light.type == Light.LightType.AREA) {
            
            int numSamples = 16; 
            double areaContribution = 0.0;

            for (int i = 0; i < numSamples; i++) {
                // Pick a random point on the rectangle [(-0.5..0.5), (-0.5..0.5)]
                double rx = Math.random() - 0.5;
                double ry = Math.random() - 0.5;
                // Convert that to a point on the actual rectangle
                Vector3 samplePoint = light.position
                    .add(light.u.multiply(rx))   // light.u is already scaled by width
                    .add(light.v.multiply(ry));  // light.v is already scaled by height

                Vector3 lightDir = samplePoint.subtract(point).normalize();
                double tMax = samplePoint.subtract(point).length();

                // Shadow check
                Vector3 shadowRayOrigin = point.add(normal.multiply(epsilon));
                Object[] shadowData = closestIntersection(shadowRayOrigin, lightDir, spheres, 0.001, tMax);
                if (shadowData[0] != null) {
                    // in shadow for this sample
                    continue;
                }

                // If not in shadow, compute diffuse + spec
                double nDotL = normal.dot(lightDir);
                if (nDotL > 0) {
                    
                    double dist2 = tMax * tMax;
                    // double sampleIntensity = light.intensity / dist2;
                    double sampleIntensity = light.intensity;

                    areaContribution += sampleIntensity * nDotL / (normal.length() * lightDir.length());

                    // Specular
                    if (specular != -1) {
                        Vector3 reflectDir = normal.multiply(2 * nDotL).subtract(lightDir).normalize();
                        double rDotV = reflectDir.dot(viewDir);
                        if (rDotV > 0) {
                            areaContribution += sampleIntensity
                                * Math.pow(rDotV / (reflectDir.length() * viewDir.length()), specular);
                        }
                    }
                }
            }
            // Average over all samples
            areaContribution /= numSamples;
            // Add to total intensity
            intensity += areaContribution;
        }
    }

    // clamp intensity to [0,1]
    intensity = Math.min(intensity, 1.0);
    intensity = Math.max(intensity, 0.0);

    return intensity;
}


    public static Color traceRay(Vector3 origin, Vector3 direction, Sphere[] spheres, Light[] lights, int recursionDepth) {
        // Find the closest sphere intersection
        Object[] intersectionData = closestIntersection(origin, direction, spheres, 0.001, Double.MAX_VALUE);
        Sphere closestSphere = (Sphere) intersectionData[0];
        double closestT = (double) intersectionData[1];
    
        // If no intersection, return the background color (a gradient based on ray direction)
        if (closestSphere == null) {
            return getBackgroundColor(direction);
        }
    
        // Compute the intersection point and the normal at that point
        Vector3 intersection = origin.add(direction.multiply(closestT));
        Vector3 normal = intersection.subtract(closestSphere.center).normalize();
        // Compute the view direction (from intersection back to camera)
        Vector3 viewDir = origin.subtract(intersection).normalize();
    
        // Compute direct lighting at the intersection using your existing lighting model
        double intensity = computeLighting(intersection, normal, lights, spheres, closestSphere.specular, viewDir);
        // Local color is the sphere's color scaled by the computed light intensity
        Color localColor = multiplyColor(closestSphere.color, intensity);
    
        // Material handling
        if (closestSphere.materialType == MaterialType.DIFFUSE) {
            // For diffuse, if there's some reflectivity, blend with a reflection ray.
            if (closestSphere.reflective > 0 && recursionDepth > 0) {
                Vector3 reflectDir = direction.reflect(normal).normalize();
                Vector3 reflectOrigin = intersection.add(normal.multiply(0.001)); // offset to avoid self-intersection
                Color reflectedColor = traceRay(reflectOrigin, reflectDir, spheres, lights, recursionDepth - 1);
                return blendColors(localColor, closestSphere.reflective, reflectedColor);
            }
            // Otherwise, return the local diffuse color.
            return localColor;
        }
        else if (closestSphere.materialType == MaterialType.METAL) {
            // For metal, we always reflect (if recursion depth remains).
            if (recursionDepth <= 0) return localColor;
            Vector3 reflectDir = direction.reflect(normal).normalize();
            Vector3 reflectOrigin = intersection.add(normal.multiply(0.001));
            Color reflectedColor = traceRay(reflectOrigin, reflectDir, spheres, lights, recursionDepth - 1);
            // Typically, metal is mostly reflective.
            return blendColors(localColor, 1.0, reflectedColor);
        }
        else if (closestSphere.materialType == MaterialType.DIELECTRIC) {
            // For dielectric (glass), handle refraction and reflection using Snell's law.
            if (recursionDepth <= 0) return localColor;
            
            double n1 = 1.0; // Refractive index of air
            double n2 = closestSphere.refractiveIndex; // Refractive index of the sphere
            // Compute the cosine of the incident angle
            double cosi = direction.normalize().multiply(-1).dot(normal);
            Vector3 n = normal;
            // If the ray is inside the object, invert the normal and swap indices.
            if (cosi < 0) {
                cosi = -cosi;
                n = normal.multiply(-1);
                double temp = n1; n1 = n2; n2 = temp;
            }
            double eta = n1 / n2;
            double k = 1 - eta * eta * (1 - cosi * cosi);
            
            // If total internal reflection occurs
            if (k < 0) {
                Vector3 reflectDir = direction.reflect(normal).normalize();
                Vector3 reflectOrigin = intersection.add(n.multiply(0.001));
                return traceRay(reflectOrigin, reflectDir, spheres, lights, recursionDepth - 1);
            } else {
                // Compute both reflection and refraction directions.
                Vector3 reflectDir = direction.reflect(normal).normalize();
                Vector3 refractDir = direction.multiply(eta)
                                              .add(n.multiply(eta * cosi - Math.sqrt(k)))
                                              .normalize();
                // Use Schlick's approximation to determine reflection probability.
                double reflectProb = schlick(cosi, n1, n2);
                if (Math.random() < reflectProb) {
                    Vector3 reflectOrigin = intersection.add(n.multiply(0.001));
                    return traceRay(reflectOrigin, reflectDir, spheres, lights, recursionDepth - 1);
                } else {
                    Vector3 refractOrigin = intersection.add(refractDir.multiply(0.001));
                    return traceRay(refractOrigin, refractDir, spheres, lights, recursionDepth - 1);
                }
            }
        }
    
        // if none of the material cases match, return the local color.
        return localColor;
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

    static Color getBackgroundColor(Vector3 direction) {
        // Normalize the direction so y is between -1 and 1
        Vector3 unitDir = direction.normalize();
        // Map y from [-1..1] to [0..1]
        double t = 0.5 * (unitDir.y + 1.0);
        
        
        int r = (int)((1.0 - t) * 255 + t * 127); 
        int g = (int)((1.0 - t) * 255 + t * 178); 
        int b = (int)((1.0 - t) * 255 + t * 255);
        return new Color(r, g, b);
    }

    
    //Schlick's approximation for reflectance.
    static double schlick(double cosine, double n1, double n2) {
        double r0 = (n1 - n2) / (n1 + n2);
        r0 = r0 * r0;
        return r0 + (1 - r0) * Math.pow((1 - cosine), 5);
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