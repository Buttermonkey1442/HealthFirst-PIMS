package com.healthfirst.pims.ui.components;

import com.healthfirst.pims.ui.theme.UITheme;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

public class ActionButton extends JButton {

    private final Color defaultBg;
    private final Color hoverBg;
    private Color currentBg;
    private final int radius;

    public ActionButton(String text, Color defaultColor, Color hoverColor, Color textColor, int cornerRadius) {
        super(text);
        this.defaultBg = defaultColor;
        this.hoverBg = hoverColor;
        this.currentBg = defaultColor;
        this.radius = cornerRadius;

        setFont(UITheme.FONT_BODY_BOLD);
        setForeground(textColor);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                currentBg = hoverBg;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                currentBg = defaultBg;
                repaint();
            }
        });
    }

    public static ActionButton primary(String text) {
        return new ActionButton(text, UITheme.PRIMARY, UITheme.PRIMARY_HOVER, UITheme.TEXT_WHITE, 8);
    }

    public static ActionButton danger(String text) {
        return new ActionButton(text, Color.decode("#DC2626"), Color.decode("#B91C1C"), UITheme.TEXT_WHITE, 8);
    }

    public static ActionButton secondary(String text) {
        return new ActionButton(text, UITheme.SECONDARY, Color.decode("#475569"), UITheme.TEXT_WHITE, 8);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(currentBg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        super.paintComponent(g);
        g2.dispose();
    }
}