package Project_1;


public class Camera {
    private Vector3 origin;            // Camera's position
    private Vector3 lowerLeftCorner;   // Lower-left corner of the image plane
    private Vector3 horizontal;        // Horizontal span of the image plane
    private Vector3 vertical;          // Vertical span of the image plane

    /*
      lookFrom    The camera's position.
      lookAt      The point the camera is aimed at
      vup         The "up" vector 
      vfov        Vertical field-of-view in degrees
      aspectRatio The aspect ratio (width/height)
     */
    public Camera(Vector3 lookFrom, Vector3 lookAt, Vector3 vup, double vfov, double aspectRatio) {
        // Convert field-of-view from degrees to radians.
        double theta = Math.toRadians(vfov);
        // Compute half-height of the viewport.
        double halfHeight = Math.tan(theta / 2);
        // Compute half-width using the aspect ratio.
        double halfWidth = aspectRatio * halfHeight;
        
        // Create an orthonormal basis:
        // w: points from lookFrom to lookAt (but reversed)
        Vector3 w = lookFrom.subtract(lookAt).normalize();
        // u: right vector = vup cross w
        Vector3 u = vup.cross(w).normalize();
        // v: true up vector = w cross u
        Vector3 v = w.cross(u);
        
        // Set the camera's origin.
        origin = lookFrom;
        // Calculate the lower-left corner of the image plane.
        lowerLeftCorner = origin.subtract(u.multiply(halfWidth))
                                  .subtract(v.multiply(halfHeight))
                                  .subtract(w);
        // Horizontal and vertical spans.
        horizontal = u.multiply(2 * halfWidth);
        vertical   = v.multiply(2 * halfHeight);
    }

    public Ray getRay(double px, double py) {
        // Compute the target point on the image plane.
        Vector3 pointOnPlane = lowerLeftCorner.add(horizontal.multiply(px))
                                              .add(vertical.multiply(py));
        // The ray direction is the vector from the camera's origin to the point.
        return new Ray(origin, pointOnPlane.subtract(origin).normalize());
    }
}