package com.healthfirst.pims.ui.admin;

import com.healthfirst.pims.dao.ReportDAO;
import com.healthfirst.pims.ui.components.ActionButton;
import com.healthfirst.pims.ui.components.RoundedPanel;
import com.healthfirst.pims.ui.theme.UITheme;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.print.PrinterException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ReportsPanel extends JPanel {

    private final ReportDAO reportDAO = new ReportDAO();

    private JComboBox<String> cmbReportType;
    private JLabel lblReportTitle;
    private JLabel lblRecordCount;
    private JTable tblReport;
    private DefaultTableModel modelReport;

    public ReportsPanel() {
        initComponents();
        loadActiveReport();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.CANVAS_BG);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        RoundedPanel container = new RoundedPanel(12, UITheme.CARD_BG, UITheme.CARD_BORDER);
        container.setLayout(new BorderLayout(12, 12));
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel controlBar = new JPanel(new BorderLayout(10, 0));
        controlBar.setOpaque(false);

        JPanel selectorGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        selectorGroup.setOpaque(false);

        JLabel lblSelect = new JLabel("Select Analytical Report:");
        lblSelect.setFont(UITheme.FONT_BODY_BOLD);
        lblSelect.setForeground(UITheme.TEXT_PRIMARY);

        cmbReportType = new JComboBox<>(new String[]{
            "General Sales Report",
            "Item-Wise Sales Report",
            "Low Stock Alert Report",
            "Medicine Expiry Report (Next 1 Month)"
        });
        cmbReportType.setFont(UITheme.FONT_BODY);
        cmbReportType.setPreferredSize(new Dimension(280, 36));
        cmbReportType.addActionListener(e -> loadActiveReport());

        selectorGroup.add(lblSelect);
        selectorGroup.add(cmbReportType);

        JPanel actionGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionGroup.setOpaque(false);

        ActionButton btnRefresh = ActionButton.secondary("Refresh Data");
        btnRefresh.setPreferredSize(new Dimension(120, 36));
        btnRefresh.addActionListener(e -> loadActiveReport());

        ActionButton btnPrint = ActionButton.primary("Print / Export Report");
        btnPrint.setPreferredSize(new Dimension(170, 36));
        btnPrint.addActionListener(e -> handlePrintReport());

        actionGroup.add(btnRefresh);
        actionGroup.add(btnPrint);

        controlBar.add(selectorGroup, BorderLayout.WEST);
        controlBar.add(actionGroup, BorderLayout.EAST);

        JPanel headerInfo = new JPanel(new BorderLayout());
        headerInfo.setOpaque(false);
        headerInfo.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        lblReportTitle = new JLabel("Sales Performance Summary");
        lblReportTitle.setFont(UITheme.FONT_HEADER_2);
        lblReportTitle.setForeground(UITheme.TEXT_PRIMARY);

        lblRecordCount = new JLabel("Showing 0 records");
        lblRecordCount.setFont(UITheme.FONT_SMALL);
        lblRecordCount.setForeground(UITheme.TEXT_MUTED);

        headerInfo.add(lblReportTitle, BorderLayout.WEST);
        headerInfo.add(lblRecordCount, BorderLayout.EAST);

        modelReport = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblReport = new JTable(modelReport);
        tblReport.setFont(UITheme.FONT_BODY);
        tblReport.setRowHeight(28);
        tblReport.getTableHeader().setFont(UITheme.FONT_BODY_BOLD);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setOpaque(false);
        centerPanel.add(headerInfo, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(tblReport), BorderLayout.CENTER);

        container.add(controlBar, BorderLayout.NORTH);
        container.add(centerPanel, BorderLayout.CENTER);

        add(container, BorderLayout.CENTER);
    }

    private void loadActiveReport() {
        int index = cmbReportType.getSelectedIndex();
        modelReport.setRowCount(0);
        modelReport.setColumnCount(0);

        try {
            switch (index) {
                case 0: // General Sales Report
                    lblReportTitle.setText("General Sales Performance Report");
                    setupTableColumns(new String[]{"Sale ID", "Transaction Date", "Serving Cashier", "Item Count", "Total Paid"});
                    populateTable(reportDAO.getSalesReport());
                    break;

                case 1: // Item-Wise Sales Report
                    lblReportTitle.setText("Item-Wise Sales & Revenue Distribution");
                    setupTableColumns(new String[]{"Med ID", "Medicine Name", "Manufacturer", "Type", "Units Dispensed", "Gross Revenue"});
                    populateTable(reportDAO.getItemWiseSalesReport());
                    break;

                case 2: // Low Stock Alert Report
                    lblReportTitle.setText("Low Stock Alert (At or Below Reorder Threshold)");
                    setupTableColumns(new String[]{"Med ID", "Medicine Name", "Manufacturer", "In Stock", "Reorder Level", "Supplier", "Supplier Phone"});
                    populateTable(reportDAO.getLowStockReport());
                    break;

                case 3: // Expiry Report
                    lblReportTitle.setText("Expiring Stock (Within 30-Day Window / Expired)");
                    setupTableColumns(new String[]{"Med ID", "Medicine Name", "Manufacturer", "Expiry Date", "Shelf Life Status", "Quantity Remaining", "Supplier"});
                    populateTable(reportDAO.getExpiryReport());
                    break;
            }
            lblRecordCount.setText("Total Records Found: " + modelReport.getRowCount());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to compile analytical data: " + ex.getMessage(),
                                          "Reporting Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupTableColumns(String[] columns) {
        for (String col : columns) {
            modelReport.addColumn(col);
        }
    }

    private void populateTable(List<Vector<Object>> rows) {
        for (Vector<Object> row : rows) {
            modelReport.addRow(row);
        }
    }

    private void handlePrintReport() {
        if (modelReport.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "The active report has no rows to print.",
                                          "Empty Report", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            MessageFormat header = new MessageFormat("HealthFirst PIMS - " + lblReportTitle.getText());
            MessageFormat footer = new MessageFormat("Page - {0}");
            boolean complete = tblReport.print(JTable.PrintMode.FIT_WIDTH, header, footer);
            if (complete) {
                JOptionPane.showMessageDialog(this, "Report printed or saved successfully.",
                                              "Print Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Printing execution failed: " + ex.getMessage(),
                                          "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}