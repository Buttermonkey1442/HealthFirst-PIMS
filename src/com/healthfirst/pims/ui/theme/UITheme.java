package com.healthfirst.pims.ui.theme;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

public final class UITheme {

    private UITheme() {}

    public static final Color SIDEBAR_BG     = Color.decode("#0F172A"); // Deep Slate Navy
    public static final Color SIDEBAR_HOVER  = Color.decode("#1E293B"); // Slate Lighter
    public static final Color SIDEBAR_ACTIVE = Color.decode("#0D9488"); // Teal Accent
    
    public static final Color CANVAS_BG      = Color.decode("#F8FAFC"); // Clean Off-White
    public static final Color CARD_BG        = Color.decode("#FFFFFF"); // Pure White
    public static final Color CARD_BORDER    = Color.decode("#E2E8F0"); // Light Border Slate
    
    public static final Color PRIMARY        = Color.decode("#0D9488"); // Clinical Teal
    public static final Color PRIMARY_HOVER  = Color.decode("#0F766E"); // Darker Teal
    public static final Color SECONDARY      = Color.decode("#64748B"); // Cool Slate
    
    public static final Color SUCCESS_BG     = Color.decode("#DEF7EC");
    public static final Color SUCCESS_TEXT   = Color.decode("#03543F"); // In stock / normal
    public static final Color WARNING_BG     = Color.decode("#FEF08A");
    public static final Color WARNING_TEXT   = Color.decode("#713F12"); // Reorder warning
    public static final Color DANGER_BG      = Color.decode("#FDE8E8");
    public static final Color DANGER_TEXT    = Color.decode("#9B1C1C"); // Expired / critical stock

    public static final Color TEXT_PRIMARY   = Color.decode("#0F172A");
    public static final Color TEXT_MUTED     = Color.decode("#64748B");
    public static final Color TEXT_WHITE     = Color.decode("#FFFFFF");

    public static final Font FONT_HEADER_1   = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER_2   = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_HEADER_3   = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY       = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD  = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL      = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BADGE      = new Font("Segoe UI", Font.BOLD, 11);

    public static final Border BORDER_INPUT  = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
    );
}