package com.healthfirst.pims.ui.components;

import com.healthfirst.pims.ui.theme.UITheme;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class RoundedPanel extends JPanel {

    private final int cornerRadius;
    private Color backgroundColor;
    private Color borderColor;

    public RoundedPanel(int radius, Color bgColor, Color borderColor) {
        super();
        this.cornerRadius = radius;
        this.backgroundColor = bgColor;
        this.borderColor = borderColor;
        setOpaque(false);
    }

    public RoundedPanel(int radius) {
        this(radius, UITheme.CARD_BG, UITheme.CARD_BORDER);
    }

    @Override
    public void setBackground(Color bg) {
        this.backgroundColor = bg;
        repaint();
    }

    public void setBorderColor(Color border) {
        this.borderColor = border;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth() - 1;
        int height = getHeight() - 1;

        g2.setColor(backgroundColor != null ? backgroundColor : getBackground());
        g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);

        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, width, height, cornerRadius, cornerRadius);
        }

        g2.dispose();
    }
}