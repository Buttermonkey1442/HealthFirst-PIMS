package com.healthfirst.pims.ui.components;

import com.healthfirst.pims.ui.theme.UITheme;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

public class StatusBadgeRenderer extends JLabel implements TableCellRenderer {

    private Color badgeBackground = UITheme.SUCCESS_BG;
    private Color badgeForeground = UITheme.SUCCESS_TEXT;

    public StatusBadgeRenderer() {
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.CENTER);
        setFont(UITheme.FONT_BADGE);
        setBorder(new EmptyBorder(4, 12, 4, 12));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        if (value == null) {
            setText("");
            return this;
        }

        String status = value.toString().toUpperCase();
        setText(status);

        if (status.contains("LOW") || status.contains("ALERT")) {
            badgeBackground = UITheme.WARNING_BG;
            badgeForeground = UITheme.WARNING_TEXT;
        } else if (status.contains("EXPIRED") || status.contains("CRITICAL") || status.contains("SOON")) {
            badgeBackground = UITheme.DANGER_BG;
            badgeForeground = UITheme.DANGER_TEXT;
        } else {
            badgeBackground = UITheme.SUCCESS_BG;
            badgeForeground = UITheme.SUCCESS_TEXT;
        }

        setForeground(badgeForeground);
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw rounded badge pill inside the cell
        g2.setColor(badgeBackground);
        g2.fillRoundRect(8, 4, getWidth() - 16, getHeight() - 8, 12, 12);

        super.paintComponent(g);
        g2.dispose();
    }
}