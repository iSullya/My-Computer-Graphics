package editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

public class BezierPanel extends JPanel implements MouseListener, MouseMotionListener {
    private java.util.List<ControlPoint> controlPoints = new ArrayList<>();
    private ControlPoint selectedPoint = null;
    private static final int POINT_RADIUS = 8;
    private static final int SEGMENTS = 200;

    public BezierPanel() {
        setBackground(Color.WHITE);
        initControlPoints(4);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void initControlPoints(int n) {
        controlPoints.clear();
        int w = getWidth() > 0 ? getWidth() : 800;
        int h = getHeight() > 0 ? getHeight() : 600;
        for (int i = 0; i < n; i++) {
            double x = (i + 1) * (w / (double)(n + 1));
            double y = h / 2.0 + ((i % 2 == 0) ? -50 : 50);
            controlPoints.add(new ControlPoint(x, y, Color.RED));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw control polygon
        g2.setStroke(new BasicStroke(1));
        g2.setColor(Color.GRAY);
        for (int i = 0; i < controlPoints.size() - 1; i++) {
            ControlPoint p1 = controlPoints.get(i);
            ControlPoint p2 = controlPoints.get(i + 1);
            g2.draw(new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY()));
        }

        // Draw Bézier curve
        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.BLACK);
        Point2D.Double prev = computeBezierPoint(0.0);
        for (int i = 1; i <= SEGMENTS; i++) {
            double t = i / (double) SEGMENTS;
            Point2D.Double curr = computeBezierPoint(t);
            g2.draw(new Line2D.Double(prev, curr));
            prev = curr;
        }

        // Draw control points
        for (ControlPoint p : controlPoints) {
            Ellipse2D.Double circle = new Ellipse2D.Double(
                p.getX() - POINT_RADIUS, p.getY() - POINT_RADIUS,
                POINT_RADIUS * 2, POINT_RADIUS * 2
            );
            g2.setColor(p.getColor());
            g2.fill(circle);
            g2.setColor(Color.BLACK);
            g2.draw(circle);
        }
    }

    private Point2D.Double computeBezierPoint(double t) {
        int n = controlPoints.size();
        double[] bx = new double[n];
        double[] by = new double[n];
        for (int i = 0; i < n; i++) {
            bx[i] = controlPoints.get(i).getX();
            by[i] = controlPoints.get(i).getY();
        }
        for (int r = 1; r < n; r++) {
            for (int i = 0; i < n - r; i++) {
                bx[i] = (1 - t) * bx[i] + t * bx[i + 1];
                by[i] = (1 - t) * by[i] + t * by[i + 1];
            }
        }
        return new Point2D.Double(bx[0], by[0]);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        for (ControlPoint p : controlPoints) {
            if (p.contains(e.getX(), e.getY(), POINT_RADIUS)) {
                selectedPoint = p;
                return;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        selectedPoint = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (selectedPoint != null) {
            selectedPoint.setX(e.getX());
            selectedPoint.setY(e.getY());
            repaint();
        }
    }

    // Unused events
    @Override public void mouseMoved(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
