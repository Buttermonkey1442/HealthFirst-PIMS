package com.healthfirst.pims.ui.admin;

import com.healthfirst.pims.dao.MedicineDAO;
import com.healthfirst.pims.dao.SupplierDAO;
import com.healthfirst.pims.model.Medicine;
import com.healthfirst.pims.model.Supplier;
import com.healthfirst.pims.ui.components.ActionButton;
import com.healthfirst.pims.ui.components.RoundedPanel;
import com.healthfirst.pims.ui.components.StatusBadgeRenderer;
import com.healthfirst.pims.ui.theme.UITheme;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

public class MedicinePanel extends JPanel {

    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();

    // Form input controls
    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtCompany;
    private JComboBox<String> cmbType;
    private JTextField txtPrice;
    private JSpinner spinStock;
    private JSpinner spinReorderLevel;
    private JTextField txtExpiryDate;
    private JComboBox<SupplierComboItem> cmbSupplier;

    // Search and Table components
    private JTextField txtSearch;
    private JTable tblMedicines;
    private DefaultTableModel modelMedicines;

    // Action buttons
    private ActionButton btnSave;
    private ActionButton btnUpdate;
    private ActionButton btnDelete;
    private ActionButton btnClear;

    public MedicinePanel() {
        initComponents();
        loadSuppliersDropdown();
        loadMedicineData("");
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.CANVAS_BG);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildFormPanel(), buildTablePanel());
        splitPane.setResizeWeight(0.36);
        splitPane.setDividerSize(6);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel buildFormPanel() {
        RoundedPanel panel = new RoundedPanel(12, UITheme.CARD_BG, UITheme.CARD_BORDER);
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Medicine Catalog Details");
        lblTitle.setFont(UITheme.FONT_HEADER_2);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JPanel formFields = new JPanel(new GridBagLayout());
        formFields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Medicine ID (Read-only)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        formFields.add(createLabel("Medicine ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtId = new JTextField();
        txtId.setEditable(false);
        txtId.setFont(UITheme.FONT_BODY);
        txtId.setBorder(UITheme.BORDER_INPUT);
        formFields.add(txtId, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy = 1;
        formFields.add(createLabel("Medicine Name *:"), gbc);
        gbc.gridx = 1;
        txtName = new JTextField();
        txtName.setFont(UITheme.FONT_BODY);
        txtName.setBorder(UITheme.BORDER_INPUT);
        formFields.add(txtName, gbc);

        // Company / Manufacturer
        gbc.gridx = 0; gbc.gridy = 2;
        formFields.add(createLabel("Manufacturer *:"), gbc);
        gbc.gridx = 1;
        txtCompany = new JTextField();
        txtCompany.setFont(UITheme.FONT_BODY);
        txtCompany.setBorder(UITheme.BORDER_INPUT);
        formFields.add(txtCompany, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formFields.add(createLabel("Dosage Type *:"), gbc);
        gbc.gridx = 1;
        cmbType = new JComboBox<>(new String[]{"Tablet", "Capsule", "Syrup", "Injection", "Cream"});
        cmbType.setFont(UITheme.FONT_BODY);
        formFields.add(cmbType, gbc);

        // Unit Price
        gbc.gridx = 0; gbc.gridy = 4;
        formFields.add(createLabel("Unit Price (ZAR) *:"), gbc);
        gbc.gridx = 1;
        txtPrice = new JTextField();
        txtPrice.setFont(UITheme.FONT_BODY);
        txtPrice.setBorder(UITheme.BORDER_INPUT);
        formFields.add(txtPrice, gbc);

        // Stock Quantity
        gbc.gridx = 0; gbc.gridy = 5;
        formFields.add(createLabel("Stock Quantity *:"), gbc);
        gbc.gridx = 1;
        spinStock = new JSpinner(new SpinnerNumberModel(10, 0, 100000, 1));
        spinStock.setFont(UITheme.FONT_BODY);
        formFields.add(spinStock, gbc);

        // Reorder Level Alert
        gbc.gridx = 0; gbc.gridy = 6;
        formFields.add(createLabel("Reorder Level *:"), gbc);
        gbc.gridx = 1;
        spinReorderLevel = new JSpinner(new SpinnerNumberModel(15, 1, 10000, 1));
        spinReorderLevel.setFont(UITheme.FONT_BODY);
        formFields.add(spinReorderLevel, gbc);

        // Expiry Date (YYYY-MM-DD)
        gbc.gridx = 0; gbc.gridy = 7;
        formFields.add(createLabel("Expiry (YYYY-MM-DD) *:"), gbc);
        gbc.gridx = 1;
        txtExpiryDate = new JTextField();
        txtExpiryDate.setFont(UITheme.FONT_BODY);
        txtExpiryDate.setBorder(UITheme.BORDER_INPUT);
        txtExpiryDate.setToolTipText("Format: YYYY-MM-DD (e.g. 2027-12-31)");
        formFields.add(txtExpiryDate, gbc);

        // Supplier Selection Dropdown
        gbc.gridx = 0; gbc.gridy = 8;
        formFields.add(createLabel("Supplier Vendor *:"), gbc);
        gbc.gridx = 1;
        cmbSupplier = new JComboBox<>();
        cmbSupplier.setFont(UITheme.FONT_BODY);
        formFields.add(cmbSupplier, gbc);

        // Action Toolbar
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
        actionPanel.setOpaque(false);

        btnSave = ActionButton.primary("Save New");
        btnSave.setPreferredSize(new Dimension(100, 34));
        btnSave.addActionListener(e -> handleSaveMedicine());

        btnUpdate = ActionButton.primary("Update");
        btnUpdate.setPreferredSize(new Dimension(90, 34));
        btnUpdate.setEnabled(false);
        btnUpdate.addActionListener(e -> handleUpdateMedicine());

        btnDelete = ActionButton.danger("Delete");
        btnDelete.setPreferredSize(new Dimension(85, 34));
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> handleDeleteMedicine());

        btnClear = ActionButton.secondary("Clear");
        btnClear.setPreferredSize(new Dimension(75, 34));
        btnClear.addActionListener(e -> clearForm());

        actionPanel.add(btnSave);
        actionPanel.add(btnUpdate);
        actionPanel.add(btnDelete);
        actionPanel.add(btnClear);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(new JScrollPane(formFields), BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildTablePanel() {
        RoundedPanel panel = new RoundedPanel(12, UITheme.CARD_BG, UITheme.CARD_BORDER);
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.setFont(UITheme.FONT_BODY);
        txtSearch.setBorder(UITheme.BORDER_INPUT);
        txtSearch.setToolTipText("Filter by medicine name, company, or type...");

        ActionButton btnSearch = ActionButton.primary("Filter");
        btnSearch.setPreferredSize(new Dimension(85, 34));
        btnSearch.addActionListener(e -> loadMedicineData(txtSearch.getText().trim()));

        ActionButton btnReset = ActionButton.secondary("Reset");
        btnReset.setPreferredSize(new Dimension(80, 34));
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            loadMedicineData("");
        });

        searchBar.add(new JLabel("Catalog Search: "), BorderLayout.WEST);
        searchBar.add(txtSearch, BorderLayout.CENTER);

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnGroup.setOpaque(false);
        btnGroup.add(btnSearch);
        btnGroup.add(btnReset);
        searchBar.add(btnGroup, BorderLayout.EAST);

        String[] columns = {"ID", "Name", "Company", "Type", "Price", "Stock", "Reorder", "Expiry", "Supplier", "Health Status"};
        modelMedicines = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblMedicines = new JTable(modelMedicines);
        tblMedicines.setFont(UITheme.FONT_BODY);
        tblMedicines.setRowHeight(28);
        tblMedicines.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblMedicines.getTableHeader().setFont(UITheme.FONT_BODY_BOLD);

        // Apply StatusBadgeRenderer to the "Health Status" column (Index 9)[cite: 5]
        tblMedicines.getColumnModel().getColumn(9).setCellRenderer(new StatusBadgeRenderer());

        tblMedicines.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblMedicines.getSelectedRow() != -1) {
                populateFormFromTable(tblMedicines.getSelectedRow());
            }
        });

        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblMedicines), BorderLayout.CENTER);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_BODY_BOLD);
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        return lbl;
    }

    private void loadSuppliersDropdown() {
        cmbSupplier.removeAllItems();
        try {
            List<Supplier> suppliers = supplierDAO.getAllSuppliers();
            for (Supplier s : suppliers) {
                cmbSupplier.addItem(new SupplierComboItem(s.getSupplierId(), s.getName()));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load suppliers into dropdown: " + ex.getMessage(),
                                          "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMedicineData(String keyword) {
        modelMedicines.setRowCount(0);
        try {
            List<Medicine> list = keyword.isEmpty() 
                    ? medicineDAO.getAllMedicines() 
                    : medicineDAO.searchMedicines(keyword);

            LocalDate today = LocalDate.now();
            LocalDate oneMonthThreshold = today.plusMonths(1);

            for (Medicine m : list) {
                String status = "IN STOCK";
                if (m.getExpiryDate().isBefore(today)) {
                    status = "EXPIRED";
                } else if (m.getExpiryDate().isBefore(oneMonthThreshold)) {
                    status = "EXPIRING SOON";
                } else if (m.getQuantityInStock() <= m.getReorderLevel()) {
                    status = "LOW STOCK";
                }

                modelMedicines.addRow(new Object[]{
                    m.getMedicineId(),
                    m.getName(),
                    m.getCompany(),
                    m.getMedicineType(),
                    String.format("%.2f", m.getPrice()),
                    m.getQuantityInStock(),
                    m.getReorderLevel(),
                    m.getExpiryDate().toString(),
                    m.getSupplierName(),
                    status
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load medicine records: " + ex.getMessage(),
                                          "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFormFromTable(int row) {
        txtId.setText(modelMedicines.getValueAt(row, 0).toString());
        txtName.setText(modelMedicines.getValueAt(row, 1).toString());
        txtCompany.setText(modelMedicines.getValueAt(row, 2).toString());
        cmbType.setSelectedItem(modelMedicines.getValueAt(row, 3).toString());
        txtPrice.setText(modelMedicines.getValueAt(row, 4).toString());
        spinStock.setValue(modelMedicines.getValueAt(row, 5));
        spinReorderLevel.setValue(modelMedicines.getValueAt(row, 6));
        txtExpiryDate.setText(modelMedicines.getValueAt(row, 7).toString());

        String supplierName = (String) modelMedicines.getValueAt(row, 8);
        for (int i = 0; i < cmbSupplier.getItemCount(); i++) {
            if (cmbSupplier.getItemAt(i).name.equalsIgnoreCase(supplierName)) {
                cmbSupplier.setSelectedIndex(i);
                break;
            }
        }

        btnSave.setEnabled(false);
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    private void handleSaveMedicine() {
        Medicine med = parseFormInputs();
        if (med == null) return;

        try {
            if (medicineDAO.addMedicine(med)) {
                JOptionPane.showMessageDialog(this, "Medicine cataloged successfully.",
                                              "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadMedicineData("");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error inserting medicine: " + ex.getMessage(),
                                          "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateMedicine() {
        if (txtId.getText().isEmpty()) return;
        Medicine med = parseFormInputs();
        if (med == null) return;

        med.setMedicineId(Integer.parseInt(txtId.getText().trim()));
        try {
            if (medicineDAO.updateMedicine(med)) {
                JOptionPane.showMessageDialog(this, "Medicine updated successfully.",
                                              "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadMedicineData("");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating medicine: " + ex.getMessage(),
                                          "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteMedicine() {
        if (txtId.getText().isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete '" + txtName.getText().trim() + "' from inventory?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        int medicineId = Integer.parseInt(txtId.getText().trim());
        try {
            if (medicineDAO.deleteMedicine(medicineId)) {
                JOptionPane.showMessageDialog(this, "Medicine removed from catalog.",
                                              "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadMedicineData("");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                                          "Referential Integrity Rule", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Medicine parseFormInputs() {
        String name = txtName.getText().trim();
        String company = txtCompany.getText().trim();
        String type = (String) cmbType.getSelectedItem();
        String priceStr = txtPrice.getText().trim();
        String expiryStr = txtExpiryDate.getText().trim();

        if (name.isEmpty() || company.isEmpty() || priceStr.isEmpty() || expiryStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All starred fields (*) must be completed.",
                                          "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceStr);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be greater than zero.",
                                              "Validation Error", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price format. Example: 45.50",
                                          "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        LocalDate expiryDate;
        try {
            expiryDate = LocalDate.parse(expiryStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.",
                                          "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        SupplierComboItem selectedSupplier = (SupplierComboItem) cmbSupplier.getSelectedItem();
        if (selectedSupplier == null) {
            JOptionPane.showMessageDialog(this, "Please select an authorized supplier.",
                                          "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int stock = (int) spinStock.getValue();
        int reorder = (int) spinReorderLevel.getValue();

        Medicine med = new Medicine();
        med.setName(name);
        med.setCompany(company);
        med.setMedicineType(type);
        med.setPrice(price);
        med.setQuantityInStock(stock);
        med.setReorderLevel(reorder);
        med.setExpiryDate(expiryDate);
        med.setSupplierId(selectedSupplier.id);

        return med;
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtCompany.setText("");
        if (cmbType.getItemCount() > 0) cmbType.setSelectedIndex(0);
        txtPrice.setText("");
        spinStock.setValue(10);
        spinReorderLevel.setValue(15);
        txtExpiryDate.setText("");
        if (cmbSupplier.getItemCount() > 0) cmbSupplier.setSelectedIndex(0);
        tblMedicines.clearSelection();

        btnSave.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private static class SupplierComboItem {
        final int id;
        final String name;

        SupplierComboItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}