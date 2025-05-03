package editor;

import javax.swing.*;
import java.awt.*;

public class VisualBezierEditor extends JFrame {
    public VisualBezierEditor() {
        super("Bezier Curve Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create drawing panel
        BezierPanel bezierPanel = new BezierPanel();
        add(bezierPanel, BorderLayout.CENTER);

        // Create control panel
        JPanel controlPanel = new JPanel();
        String[] options = {"Quadratic (3)", "Cubic (4)", "Quartic (5)", "Quintic (6)", "Sextic (7)", "Septic (8)", "Octic (9)"};
        JComboBox<String> curveSelector = new JComboBox<>(options);
        curveSelector.setSelectedIndex(1);
        curveSelector.addActionListener(e -> {
            int n;
            switch (curveSelector.getSelectedIndex()) {
                case 0: n = 3; break;
                case 2: n = 5; break;
                case 3: n = 6; break;
                case 4: n = 7; break;
                case 5: n = 8; break;
                case 6: n = 9; break;
                default: n = 4;
            }
            bezierPanel.initControlPoints(n);
            bezierPanel.repaint();
        });
        controlPanel.add(new JLabel("Select curve order:"));
        controlPanel.add(curveSelector);
        add(controlPanel, BorderLayout.NORTH);

        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VisualBezierEditor());
    }
}
