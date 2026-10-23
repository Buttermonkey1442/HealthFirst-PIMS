package com.healthfirst.pims.ui;

import com.healthfirst.pims.dao.MedicineDAO;
import com.healthfirst.pims.model.CartItem;
import com.healthfirst.pims.model.Medicine;
import com.healthfirst.pims.model.User;
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
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

public class CashierDashboard extends JFrame {

    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final List<CartItem> cartItems = new ArrayList<>();

    // Stock Table Components
    private JTextField txtSearch;
    private JTable tblMedicines;
    private DefaultTableModel modelMedicines;
    private JSpinner spinQuantity;

    // Cart Table Components
    private JTable tblCart;
    private DefaultTableModel modelCart;
    private JLabel lblSubtotalVal;
    private JLabel lblTaxVal;
    private JLabel lblTotalVal;

    public CashierDashboard() {
        initComponents();
        loadMedicineData("");
    }

    private void initComponents() {
        User user = SessionManager.getCurrentUser();
        String operatorName = (user != null) ? user.getFullName() : "Active Cashier";

        setTitle("HealthFirst PIMS - Point of Sale (POS) Terminal [" + operatorName + "]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.CANVAS_BG);
        setLayout(new BorderLayout());

        add(buildHeaderPanel(operatorName), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildStockPanel(), buildCartPanel());
        splitPane.setResizeWeight(0.55);
        splitPane.setDividerSize(6);
        splitPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel buildHeaderPanel(String operatorName) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.SIDEBAR_BG);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel lblBrand = new JLabel("+ HealthFirst PIMS | POS Terminal");
        lblBrand.setFont(UITheme.FONT_HEADER_2);
        lblBrand.setForeground(UITheme.TEXT_WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        JLabel lblUser = new JLabel("Cashier: " + operatorName);
        lblUser.setFont(UITheme.FONT_BODY);
        lblUser.setForeground(UITheme.TEXT_WHITE);

        ActionButton btnLogout = ActionButton.danger("Logout");
        btnLogout.setPreferredSize(new Dimension(85, 32));
        btnLogout.addActionListener(e -> handleLogout());

        userPanel.add(lblUser);
        userPanel.add(btnLogout);

        header.add(lblBrand, BorderLayout.WEST);
        header.add(userPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel buildStockPanel() {
        RoundedPanel panel = new RoundedPanel(12, UITheme.CARD_BG, UITheme.CARD_BORDER);
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.setFont(UITheme.FONT_BODY);
        txtSearch.setBorder(UITheme.BORDER_INPUT);
        txtSearch.setToolTipText("Search medicine by name or manufacturer...");

        ActionButton btnSearch = ActionButton.primary("Search");
        btnSearch.setPreferredSize(new Dimension(90, 35));
        btnSearch.addActionListener(e -> loadMedicineData(txtSearch.getText()));

        ActionButton btnReset = ActionButton.secondary("Reset");
        btnReset.setPreferredSize(new Dimension(80, 35));
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            loadMedicineData("");
        });

        searchBar.add(new JLabel("Lookup: "), BorderLayout.WEST);
        searchBar.add(txtSearch, BorderLayout.CENTER);

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnGroup.setOpaque(false);
        btnGroup.add(btnSearch);
        btnGroup.add(btnReset);
        searchBar.add(btnGroup, BorderLayout.EAST);

        String[] columns = {"ID", "Medicine Name", "Type", "Price (ZAR)", "Stock Available", "Expiry"};
        modelMedicines = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblMedicines = new JTable(modelMedicines);
        tblMedicines.setFont(UITheme.FONT_BODY);
        tblMedicines.setRowHeight(28);
        tblMedicines.getTableHeader().setFont(UITheme.FONT_BODY_BOLD);

        JPanel actionControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionControls.setOpaque(false);

        JLabel lblQty = new JLabel("Quantity:");
        lblQty.setFont(UITheme.FONT_BODY_BOLD);

        spinQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spinQuantity.setPreferredSize(new Dimension(70, 34));

        ActionButton btnAddToCart = ActionButton.primary("Add To Cart");
        btnAddToCart.setPreferredSize(new Dimension(130, 34));
        btnAddToCart.addActionListener(e -> handleAddToCart());

        actionControls.add(lblQty);
        actionControls.add(spinQuantity);
        actionControls.add(btnAddToCart);

        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblMedicines), BorderLayout.CENTER);
        panel.add(actionControls, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildCartPanel() {
        RoundedPanel panel = new RoundedPanel(12, UITheme.CARD_BG, UITheme.CARD_BORDER);
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("Current Customer Order");
        lblTitle.setFont(UITheme.FONT_HEADER_3);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        String[] cartColumns = {"ID", "Item", "Unit Price", "Qty", "Total"};
        modelCart = new DefaultTableModel(cartColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblCart = new JTable(modelCart);
        tblCart.setFont(UITheme.FONT_BODY);
        tblCart.setRowHeight(28);
        tblCart.getTableHeader().setFont(UITheme.FONT_BODY_BOLD);

        JPanel cartActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        cartActions.setOpaque(false);

        ActionButton btnRemove = ActionButton.secondary("Remove Selected");
        btnRemove.setPreferredSize(new Dimension(140, 32));
        btnRemove.addActionListener(e -> handleRemoveSelectedItem());

        ActionButton btnClear = ActionButton.danger("Clear Cart"); // Clear cart requirement[cite: 5]
        btnClear.setPreferredSize(new Dimension(110, 32));
        btnClear.addActionListener(e -> handleClearCart());

        cartActions.add(btnRemove);
        cartActions.add(btnClear);

        JPanel summaryCard = new JPanel(new GridLayout(4, 2, 5, 6));
        summaryCard.setOpaque(false);
        summaryCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.CARD_BORDER),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblSub = new JLabel("Subtotal:");
        lblSub.setFont(UITheme.FONT_BODY);
        lblSubtotalVal = new JLabel("R 0.00");
        lblSubtotalVal.setFont(UITheme.FONT_BODY_BOLD);

        JLabel lblTax = new JLabel("VAT (15% included):");
        lblTax.setFont(UITheme.FONT_BODY);
        lblTaxVal = new JLabel("R 0.00");
        lblTaxVal.setFont(UITheme.FONT_BODY_BOLD);

        JLabel lblTot = new JLabel("Total Payable:");
        lblTot.setFont(UITheme.FONT_HEADER_2);
        lblTotalVal = new JLabel("R 0.00");
        lblTotalVal.setFont(UITheme.FONT_HEADER_2);
        lblTotalVal.setForeground(UITheme.PRIMARY);

        ActionButton btnCheckout = ActionButton.primary("Proceed to Checkout");
        btnCheckout.setPreferredSize(new Dimension(0, 40));
        btnCheckout.addActionListener(e -> {
            if (cartItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "The cart is empty. Add items before proceeding.", 
                                              "Empty Cart", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, 
                "Cart verified with " + cartItems.size() + " items.\nReady for Commit 6 transaction execution.", 
                "Checkout Staging", JOptionPane.INFORMATION_MESSAGE);
        });

        summaryCard.add(lblSub);
        summaryCard.add(lblSubtotalVal);
        summaryCard.add(lblTax);
        summaryCard.add(lblTaxVal);
        summaryCard.add(lblTot);
        summaryCard.add(lblTotalVal);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setOpaque(false);
        bottomContainer.add(cartActions, BorderLayout.NORTH);
        bottomContainer.add(summaryCard, BorderLayout.CENTER);
        bottomContainer.add(btnCheckout, BorderLayout.SOUTH);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblCart), BorderLayout.CENTER);
        panel.add(bottomContainer, BorderLayout.SOUTH);

        return panel;
    }

    private void loadMedicineData(String query) {
        modelMedicines.setRowCount(0);
        try {
            List<Medicine> list = query.isEmpty() 
                    ? medicineDAO.getAllMedicines() 
                    : medicineDAO.searchMedicines(query);

            for (Medicine m : list) {
                modelMedicines.addRow(new Object[]{
                    m.getMedicineId(),
                    m.getName(),
                    m.getMedicineType(),
                    String.format("%.2f", m.getPrice()),
                    m.getQuantityInStock(),
                    m.getExpiryDate().toString()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load medicine stock: " + ex.getMessage(),
                                          "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAddToCart() {
        int selectedRow = tblMedicines.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item from the stock table first.", 
                                          "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int medicineId = (int) modelMedicines.getValueAt(selectedRow, 0);
        String name = (String) modelMedicines.getValueAt(selectedRow, 1);
        double price = Double.parseDouble((String) modelMedicines.getValueAt(selectedRow, 3));
        int availableStock = (int) modelMedicines.getValueAt(selectedRow, 4);
        int qtyRequested = (int) spinQuantity.getValue();

        // Calculate quantity already in cart
        int existingInCart = 0;
        for (CartItem ci : cartItems) {
            if (ci.getMedicineId() == medicineId) {
                existingInCart += ci.getQuantity();
            }
        }

        if (existingInCart + qtyRequested > availableStock) {
            JOptionPane.showMessageDialog(this, 
                "Cannot add " + qtyRequested + " unit(s). Current stock is " + availableStock 
                + " (" + existingInCart + " already in cart).", 
                "Stock Insufficient", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Add or update cart item
        boolean found = false;
        for (CartItem ci : cartItems) {
            if (ci.getMedicineId() == medicineId) {
                ci.setQuantity(ci.getQuantity() + qtyRequested);
                found = true;
                break;
            }
        }

        if (!found) {
            cartItems.add(new CartItem(medicineId, name, price, qtyRequested));
        }

        refreshCartUI();
    }

    private void handleRemoveSelectedItem() {
        int selectedRow = tblCart.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an item in the cart to remove.", 
                                          "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        cartItems.remove(selectedRow);
        refreshCartUI();
    }

    private void handleClearCart() {
        if (cartItems.isEmpty()) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to clear all items in the current cart?", 
                "Clear Cart Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            cartItems.clear();
            refreshCartUI();
        }
    }

    private void refreshCartUI() {
        modelCart.setRowCount(0);
        double subtotal = 0.0;

        for (CartItem item : cartItems) {
            modelCart.addRow(new Object[]{
                item.getMedicineId(),
                item.getMedicineName(),
                String.format("R %.2f", item.getPrice()),
                item.getQuantity(),
                String.format("R %.2f", item.getSubtotal())
            });
            subtotal += item.getSubtotal();
        }

        double vat = subtotal * 0.15;
        double grandTotal = subtotal;

        lblSubtotalVal.setText(String.format("R %.2f", (subtotal - vat)));
        lblTaxVal.setText(String.format("R %.2f", vat));
        lblTotalVal.setText(String.format("R %.2f", grandTotal));
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to end this cashier session?", 
                                                   "Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            SessionManager.logout();
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }
}