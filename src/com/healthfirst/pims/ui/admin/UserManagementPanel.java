package com.healthfirst.pims.ui.admin;

import com.healthfirst.pims.dao.UserDAO;
import com.healthfirst.pims.model.User;
import com.healthfirst.pims.ui.components.ActionButton;
import com.healthfirst.pims.ui.components.RoundedPanel;
import com.healthfirst.pims.ui.theme.UITheme;
import com.healthfirst.pims.util.SessionManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class UserManagementPanel extends JPanel {

    private final UserDAO userDAO = new UserDAO();

    // Form Components
    private JTextField txtUserId;
    private JTextField txtFullName;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;

    // Table Components
    private JTable tblUsers;
    private DefaultTableModel modelUsers;

    // Actions
    private ActionButton btnSave;
    private ActionButton btnUpdate;
    private ActionButton btnDelete;
    private ActionButton btnClear;

    public UserManagementPanel() {
        initComponents();
        loadUserData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.CANVAS_BG);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildFormPanel(), buildTablePanel());
        splitPane.setResizeWeight(0.38);
        splitPane.setDividerSize(6);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel buildFormPanel() {
        RoundedPanel panel = new RoundedPanel(12, UITheme.CARD_BG, UITheme.CARD_BORDER);
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("User Account Details");
        lblTitle.setFont(UITheme.FONT_HEADER_2);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JPanel formFields = new JPanel(new GridBagLayout());
        formFields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // User ID
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        formFields.add(createLabel("User ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtUserId = new JTextField();
        txtUserId.setEditable(false);
        txtUserId.setFont(UITheme.FONT_BODY);
        txtUserId.setBorder(UITheme.BORDER_INPUT);
        formFields.add(txtUserId, gbc);

        // Full Name
        gbc.gridx = 0; gbc.gridy = 1;
        formFields.add(createLabel("Full Name *:"), gbc);
        gbc.gridx = 1;
        txtFullName = new JTextField();
        txtFullName.setFont(UITheme.FONT_BODY);
        txtFullName.setBorder(UITheme.BORDER_INPUT);
        formFields.add(txtFullName, gbc);

        // Username
        gbc.gridx = 0; gbc.gridy = 2;
        formFields.add(createLabel("Username *:"), gbc);
        gbc.gridx = 1;
        txtUsername = new JTextField();
        txtUsername.setFont(UITheme.FONT_BODY);
        txtUsername.setBorder(UITheme.BORDER_INPUT);
        formFields.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 3;
        formFields.add(createLabel("Password *:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField();
        txtPassword.setFont(UITheme.FONT_BODY);
        txtPassword.setBorder(UITheme.BORDER_INPUT);
        formFields.add(txtPassword, gbc);

        // Role
        gbc.gridx = 0; gbc.gridy = 4;
        formFields.add(createLabel("Account Role *:"), gbc);
        gbc.gridx = 1;
        cmbRole = new JComboBox<>(new String[]{"Cashier", "Admin"});
        cmbRole.setFont(UITheme.FONT_BODY);
        formFields.add(cmbRole, gbc);

        // Action Toolbar
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        actionPanel.setOpaque(false);

        btnSave = ActionButton.primary("Save New");
        btnSave.setPreferredSize(new Dimension(105, 34));
        btnSave.addActionListener(e -> handleSaveUser());

        btnUpdate = ActionButton.primary("Update");
        btnUpdate.setPreferredSize(new Dimension(95, 34));
        btnUpdate.setEnabled(false);
        btnUpdate.addActionListener(e -> handleUpdateUser());

        btnDelete = ActionButton.danger("Delete");
        btnDelete.setPreferredSize(new Dimension(90, 34));
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> handleDeleteUser());

        btnClear = ActionButton.secondary("Clear");
        btnClear.setPreferredSize(new Dimension(80, 34));
        btnClear.addActionListener(e -> clearForm());

        actionPanel.add(btnSave);
        actionPanel.add(btnUpdate);
        actionPanel.add(btnDelete);
        actionPanel.add(btnClear);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(formFields, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildTablePanel() {
        RoundedPanel panel = new RoundedPanel(12, UITheme.CARD_BG, UITheme.CARD_BORDER);
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblHeader = new JLabel("System Accounts & Role Directory");
        lblHeader.setFont(UITheme.FONT_HEADER_3);
        lblHeader.setForeground(UITheme.TEXT_PRIMARY);

        String[] columns = {"ID", "Full Name", "Username", "Role", "Password Hash / Enc"};
        modelUsers = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblUsers = new JTable(modelUsers);
        tblUsers.setFont(UITheme.FONT_BODY);
        tblUsers.setRowHeight(28);
        tblUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblUsers.getTableHeader().setFont(UITheme.FONT_BODY_BOLD);

        tblUsers.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblUsers.getSelectedRow() != -1) {
                populateFormFromTable(tblUsers.getSelectedRow());
            }
        });

        panel.add(lblHeader, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblUsers), BorderLayout.CENTER);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_BODY_BOLD);
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        return lbl;
    }

    private void loadUserData() {
        modelUsers.setRowCount(0);
        try {
            List<User> list = userDAO.getAllUsers();
            for (User u : list) {
                modelUsers.addRow(new Object[]{
                    u.getUserId(),
                    u.getFullName(),
                    u.getUsername(),
                    u.getRole(),
                    "••••••••"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load user accounts: " + ex.getMessage(),
                                          "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFormFromTable(int row) {
        int userId = (int) modelUsers.getValueAt(row, 0);
        txtUserId.setText(String.valueOf(userId));
        txtFullName.setText(modelUsers.getValueAt(row, 1).toString());
        txtUsername.setText(modelUsers.getValueAt(row, 2).toString());
        cmbRole.setSelectedItem(modelUsers.getValueAt(row, 3).toString());
        txtPassword.setText("");

        btnSave.setEnabled(false);
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    private void handleSaveUser() {
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = (String) cmbRole.getSelectedItem();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required to register a user.",
                                          "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User newUser = new User(0, username, password, role, fullName);
        try {
            if (userDAO.addUser(newUser)) {
                JOptionPane.showMessageDialog(this, "Account provisioned successfully.",
                                              "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadUserData();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                                          "Account Creation Failure", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateUser() {
        if (txtUserId.getText().isEmpty()) return;

        int userId = Integer.parseInt(txtUserId.getText().trim());
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = (String) cmbRole.getSelectedItem();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all fields including updated password.",
                                          "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User updatedUser = new User(userId, username, password, role, fullName);
        try {
            if (userDAO.updateUser(updatedUser)) {
                JOptionPane.showMessageDialog(this, "Account updated successfully.",
                                              "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadUserData();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                                          "Update Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteUser() {
        if (txtUserId.getText().isEmpty()) return;

        int targetUserId = Integer.parseInt(txtUserId.getText().trim());
        User currentUser = SessionManager.getCurrentUser();

        if (currentUser != null && currentUser.getUserId() == targetUserId) {
            JOptionPane.showMessageDialog(this, "You cannot delete your own active administrator account.",
                                          "Action Prohibited", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete user account '" + txtUsername.getText().trim() + "'?",
            "Confirm Account Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (userDAO.deleteUser(targetUserId)) {
                JOptionPane.showMessageDialog(this, "User deleted successfully.",
                                              "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadUserData();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                                          "Referential Constraint", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtUserId.setText("");
        txtFullName.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        if (cmbRole.getItemCount() > 0) cmbRole.setSelectedIndex(0);
        tblUsers.clearSelection();

        btnSave.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }
}