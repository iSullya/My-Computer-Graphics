# Visual Bezier Curve Editor (Java Swing)

A Java Swing application for creating and editing Bézier curves with up to 10 control points. Adjust curve order and manipulate points via mouse interactions in a simple GUI.

## Classes

- **VisualBezierEditor**: Sets up the main window (800×600) and top control panel with a dropdown to select curve order. Reinitializes points on selection change.
- **ControlPoint**: Represents a draggable point with coordinates and color, includes hit-testing.
- **BezierPanel**: Handles drawing and interaction. Initializes default points, listens for mouse events, and renders the control polygon and curve using De Casteljau’s algorithm.

## Features

- Select curve order (3–9 points) from dropdown
- Click to add (initial setup), drag to move, right-click to remove points
- Real-time curve rendering with smooth resolution

## Quick Start

1. **Compile**: `javac *.java`
2. **Run**: `java VisualBezierEditor`

## Configuration

- **POINT_RADIUS** in `BezierPanel` (default 8)
- **SEGMENTS** in `BezierPanel` (default 200)

