package com.healthfirst.pims.ui;

import com.healthfirst.pims.dao.UserDAO;
import com.healthfirst.pims.model.User;
import com.healthfirst.pims.ui.admin.AdminDashboard;
import com.healthfirst.pims.ui.components.ActionButton;
import com.healthfirst.pims.ui.components.RoundedPanel;
import com.healthfirst.pims.ui.theme.UITheme;
import com.healthfirst.pims.util.SessionManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblError;
    private ActionButton btnLogin;
    private final UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("HealthFirst PIMS - Portal Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 550);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.CANVAS_BG);
        setLayout(new GridBagLayout()); // Centers the card panel

        // Main Login Card
        RoundedPanel card = new RoundedPanel(16, UITheme.CARD_BG, UITheme.CARD_BORDER);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(420, 460));
        card.setBorder(BorderFactory.createEmptyBorder(30, 35, 30, 35));

        // Header Section
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel lblBadge = new JLabel("+ HealthFirst Pharmacy");
        lblBadge.setFont(UITheme.FONT_BODY_BOLD);
        lblBadge.setForeground(UITheme.PRIMARY);
        lblBadge.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("System Login");
        lblTitle.setFont(UITheme.FONT_HEADER_1);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);
        lblTitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Enter your credentials to access your terminal.");
        lblSubtitle.setFont(UITheme.FONT_BODY);
        lblSubtitle.setForeground(UITheme.TEXT_MUTED);
        lblSubtitle.setAlignmentX(LEFT_ALIGNMENT);

        headerPanel.add(lblBadge);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(lblTitle);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(lblSubtitle);

        // Form Body Section
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(UITheme.FONT_BODY_BOLD);
        lblUsername.setForeground(UITheme.TEXT_PRIMARY);

        txtUsername = new JTextField();
        txtUsername.setFont(UITheme.FONT_BODY);
        txtUsername.setBorder(UITheme.BORDER_INPUT);
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(UITheme.FONT_BODY_BOLD);
        lblPassword.setForeground(UITheme.TEXT_PRIMARY);

        txtPassword = new JPasswordField();
        txtPassword.setFont(UITheme.FONT_BODY);
        txtPassword.setBorder(UITheme.BORDER_INPUT);
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // Inline error feedback label
        lblError = new JLabel(" ");
        lblError.setFont(UITheme.FONT_SMALL);
        lblError.setForeground(Color.decode("#DC2626"));
        lblError.setAlignmentX(LEFT_ALIGNMENT);

        // Handle Enter key for quick login
        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterKeyAdapter);
        txtPassword.addKeyListener(enterKeyAdapter);

        formPanel.add(lblUsername);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(txtUsername);
        formPanel.add(Box.createVerticalStrut(14));
        formPanel.add(lblPassword);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(txtPassword);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(lblError);

        // Action Section
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setOpaque(false);

        btnLogin = ActionButton.primary("Authenticate");
        btnLogin.setPreferredSize(new Dimension(0, 42));
        btnLogin.addActionListener(e -> processLogin());

        JLabel lblHint = new JLabel("Default logins: admin/admin123 | cashier/cash123", SwingConstants.CENTER);
        lblHint.setFont(UITheme.FONT_SMALL);
        lblHint.setForeground(UITheme.TEXT_MUTED);
        lblHint.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        actionPanel.add(btnLogin, BorderLayout.NORTH);
        actionPanel.add(lblHint, BorderLayout.SOUTH);

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(formPanel, BorderLayout.CENTER);
        card.add(actionPanel, BorderLayout.SOUTH);

        add(card, new GridBagConstraints());
    }

    private void processLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Both username and password are required.");
            return;
        }

        try {
            User authenticatedUser = userDAO.authenticate(username, password);

            if (authenticatedUser != null) {
                lblError.setText(" ");
                SessionManager.setCurrentUser(authenticatedUser);

                // Role-based view routing
                if (authenticatedUser.isAdmin()) {
                    AdminDashboard adminDashboard = new AdminDashboard();
                    adminDashboard.setVisible(true);
                    this.dispose();
                    
                } else if (authenticatedUser.isCashier()) {
                    CashierDashboard cashierDashboard = new CashierDashboard();
                    cashierDashboard.setVisible(true);
                    this.dispose();
                    
                }
                
                this.dispose();
            } else {
                lblError.setText("Invalid username or password. Please try again.");
                txtPassword.setText("");
            }
        } catch (SQLException ex) {
            lblError.setText("Database connection failure. Verify MySQL service.");
            ex.printStackTrace();
        }
    }
}