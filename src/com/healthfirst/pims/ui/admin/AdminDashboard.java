package com.healthfirst.pims.ui.admin;

import com.healthfirst.pims.model.User;
import com.healthfirst.pims.ui.LoginFrame;
import com.healthfirst.pims.ui.components.ActionButton;
import com.healthfirst.pims.ui.theme.UITheme;
import com.healthfirst.pims.util.SessionManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class AdminDashboard extends JFrame {

    private JTabbedPane tabbedPane;

    public AdminDashboard() {
        initComponents();
    }

    private void initComponents() {
        User admin = SessionManager.getCurrentUser();
        String adminName = (admin != null) ? admin.getFullName() : "Administrator";

        setTitle("HealthFirst PIMS - Administrator Console [" + adminName + "]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.CANVAS_BG);
        setLayout(new BorderLayout());

        // Header Banner
        add(buildHeader(adminName), BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BODY_BOLD);
        tabbedPane.setBackground(UITheme.CARD_BG);

        // Required Tabs
        tabbedPane.addTab("Manage Medicines", new MedicinePanel()); 
        tabbedPane.addTab("Manage Suppliers", new SupplierPanel());     
        tabbedPane.addTab("Manage Users", new UserManagementPanel());   

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel buildHeader(String adminName) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.SIDEBAR_BG);
        header.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));

        JLabel lblBrand = new JLabel("+ HealthFirst PIMS | Administrative Management");
        lblBrand.setFont(UITheme.FONT_HEADER_2);
        lblBrand.setForeground(UITheme.TEXT_WHITE);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        JLabel lblUser = new JLabel("Admin: " + adminName);
        lblUser.setFont(UITheme.FONT_BODY);
        lblUser.setForeground(UITheme.TEXT_WHITE);

        ActionButton btnLogout = ActionButton.danger("Logout");
        btnLogout.setPreferredSize(new Dimension(85, 32));
        btnLogout.addActionListener(e -> handleLogout());

        rightPanel.add(lblUser);
        rightPanel.add(btnLogout);

        header.add(lblBrand, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to end this administrative session?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.logout();
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }
}