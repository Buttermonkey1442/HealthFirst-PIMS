package com.healthfirst.pims.ui.admin;

import com.healthfirst.pims.dao.SupplierDAO;
import com.healthfirst.pims.model.Supplier;
import com.healthfirst.pims.ui.components.ActionButton;
import com.healthfirst.pims.ui.components.RoundedPanel;
import com.healthfirst.pims.ui.theme.UITheme;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class SupplierPanel extends JPanel {

    private final SupplierDAO supplierDAO = new SupplierDAO();

    // Form inputs
    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtContactPerson;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextArea txtAddress;

    // Search & Table
    private JTextField txtSearch;
    private JTable tblSuppliers;
    private DefaultTableModel modelSuppliers;

    // Actions
    private ActionButton btnSave;
    private ActionButton btnUpdate;
    private ActionButton btnDelete;
    private ActionButton btnClear;

    public SupplierPanel() {
        initComponents();
        loadSupplierData("");
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

        JLabel lblTitle = new JLabel("Supplier Details");
        lblTitle.setFont(UITheme.FONT_HEADER_2);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JPanel formFields = new JPanel(new GridBagLayout());
        formFields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // ID (Read-only)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formFields.add(createLabel("Supplier ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtId = new JTextField();
        txtId.setEditable(false);
        txtId.setFont(UITheme.FONT_BODY);
        txtId.setBorder(UITheme.BORDER_INPUT);
        formFields.add(txtId, gbc);

        // Company Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formFields.add(createLabel("Company Name *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtName = new JTextField();
        txtName.setFont(UITheme.FONT_BODY);
        txtName.setBorder(UITheme.BORDER_INPUT);
        formFields.add(txtName, gbc);

        // Contact Person
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        formFields.add(createLabel("Contact Person *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtContactPerson = new JTextField();
        txtContactPerson.setFont(UITheme.FONT_BODY);
        txtContactPerson.setBorder(UITheme.BORDER_INPUT);
        formFields.add(txtContactPerson, gbc);

        // Phone Number
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        formFields.add(createLabel("Phone Number *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtPhone = new JTextField();
        txtPhone.setFont(UITheme.FONT_BODY);
        txtPhone.setBorder(UITheme.BORDER_INPUT);
        formFields.add(txtPhone, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        formFields.add(createLabel("Email Address *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtEmail = new JTextField();
        txtEmail.setFont(UITheme.FONT_BODY);
        txtEmail.setBorder(UITheme.BORDER_INPUT);
        formFields.add(txtEmail, gbc);

        // Physical Address
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formFields.add(createLabel("Physical Address:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtAddress = new JTextArea(3, 15);
        txtAddress.setFont(UITheme.FONT_BODY);
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);
        txtAddress.setBorder(UITheme.BORDER_INPUT);
        formFields.add(new JScrollPane(txtAddress), gbc);

        // Action Toolbar
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        actionPanel.setOpaque(false);

        btnSave = ActionButton.primary("Save New");
        btnSave.setPreferredSize(new Dimension(105, 34));
        btnSave.addActionListener(e -> handleSaveSupplier());

        btnUpdate = ActionButton.primary("Update");
        btnUpdate.setPreferredSize(new Dimension(95, 34));
        btnUpdate.setEnabled(false);
        btnUpdate.addActionListener(e -> handleUpdateSupplier());

        btnDelete = ActionButton.danger("Delete");
        btnDelete.setPreferredSize(new Dimension(90, 34));
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> handleDeleteSupplier());

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

        // Search Bar Header
        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.setFont(UITheme.FONT_BODY);
        txtSearch.setBorder(UITheme.BORDER_INPUT);
        txtSearch.setToolTipText("Search by company name or contact person...");

        ActionButton btnSearch = ActionButton.primary("Search");
        btnSearch.setPreferredSize(new Dimension(85, 34));
        btnSearch.addActionListener(e -> loadSupplierData(txtSearch.getText().trim()));

        ActionButton btnReset = ActionButton.secondary("Reset");
        btnReset.setPreferredSize(new Dimension(80, 34));
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            loadSupplierData("");
        });

        searchBar.add(new JLabel("Search: "), BorderLayout.WEST);
        searchBar.add(txtSearch, BorderLayout.CENTER);

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnGroup.setOpaque(false);
        btnGroup.add(btnSearch);
        btnGroup.add(btnReset);
        searchBar.add(btnGroup, BorderLayout.EAST);

        // Supplier Table
        String[] columns = {"ID", "Company Name", "Contact Person", "Phone", "Email", "Address"};
        modelSuppliers = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblSuppliers = new JTable(modelSuppliers);
        tblSuppliers.setFont(UITheme.FONT_BODY);
        tblSuppliers.setRowHeight(26);
        tblSuppliers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblSuppliers.getTableHeader().setFont(UITheme.FONT_BODY_BOLD);

        tblSuppliers.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblSuppliers.getSelectedRow() != -1) {
                populateFormFromTable(tblSuppliers.getSelectedRow());
            }
        });

        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblSuppliers), BorderLayout.CENTER);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_BODY_BOLD);
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        return lbl;
    }

    private void loadSupplierData(String keyword) {
        modelSuppliers.setRowCount(0);
        try {
            List<Supplier> suppliers = keyword.isEmpty()
                    ? supplierDAO.getAllSuppliers()
                    : supplierDAO.searchSuppliers(keyword);

            for (Supplier s : suppliers) {
                modelSuppliers.addRow(new Object[]{
                    s.getSupplierId(),
                    s.getName(),
                    s.getContactPerson(),
                    s.getPhone(),
                    s.getEmail(),
                    s.getAddress()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load suppliers: " + ex.getMessage(),
                                          "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFormFromTable(int row) {
        txtId.setText(modelSuppliers.getValueAt(row, 0).toString());
        txtName.setText(modelSuppliers.getValueAt(row, 1).toString());
        txtContactPerson.setText(modelSuppliers.getValueAt(row, 2).toString());
        txtPhone.setText(modelSuppliers.getValueAt(row, 3).toString());
        txtEmail.setText(modelSuppliers.getValueAt(row, 4).toString());
        txtAddress.setText(modelSuppliers.getValueAt(row, 5).toString());

        btnSave.setEnabled(false);
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    private void handleSaveSupplier() {
        if (!validateInputs()) return;

        Supplier s = new Supplier(
            txtName.getText().trim(),
            txtContactPerson.getText().trim(),
            txtPhone.getText().trim(),
            txtEmail.getText().trim(),
            txtAddress.getText().trim()
        );

        try {
            if (supplierDAO.addSupplier(s)) {
                JOptionPane.showMessageDialog(this, "Supplier registered successfully.",
                                              "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadSupplierData("");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving supplier: " + ex.getMessage(),
                                          "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateSupplier() {
        if (txtId.getText().isEmpty()) return;
        if (!validateInputs()) return;

        Supplier s = new Supplier(
            Integer.parseInt(txtId.getText().trim()),
            txtName.getText().trim(),
            txtContactPerson.getText().trim(),
            txtPhone.getText().trim(),
            txtEmail.getText().trim(),
            txtAddress.getText().trim()
        );

        try {
            if (supplierDAO.updateSupplier(s)) {
                JOptionPane.showMessageDialog(this, "Supplier details updated successfully.",
                                              "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadSupplierData("");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating supplier: " + ex.getMessage(),
                                          "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteSupplier() {
        if (txtId.getText().isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete supplier '" + txtName.getText().trim() + "'?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        int supplierId = Integer.parseInt(txtId.getText().trim());
        try {
            if (supplierDAO.deleteSupplier(supplierId)) {
                JOptionPane.showMessageDialog(this, "Supplier deleted successfully.",
                                              "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadSupplierData("");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                                          "Deletion Constraint", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateInputs() {
        if (txtName.getText().trim().isEmpty() ||
            txtContactPerson.getText().trim().isEmpty() ||
            txtPhone.getText().trim().isEmpty() ||
            txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All starred fields (*) are mandatory.",
                                          "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String email = txtEmail.getText().trim();
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            JOptionPane.showMessageDialog(this, "Please provide a valid email address.",
                                          "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtContactPerson.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        tblSuppliers.clearSelection();

        btnSave.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }
}