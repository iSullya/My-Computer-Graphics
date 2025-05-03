package editor;

import java.awt.*;
import java.awt.geom.Point2D;

public class ControlPoint {
    private double x;
    private double y;
    private final Color color;

    public ControlPoint(double x, double y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public Color getColor() { return color; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    public boolean contains(double px, double py, double radius) {
        return Point2D.distance(x, y, px, py) <= radius;
    }
}
