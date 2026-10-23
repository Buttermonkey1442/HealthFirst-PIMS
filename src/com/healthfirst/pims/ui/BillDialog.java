package com.healthfirst.pims.ui;

import com.healthfirst.pims.model.CartItem;
import com.healthfirst.pims.ui.components.ActionButton;
import com.healthfirst.pims.ui.theme.UITheme;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.print.PrinterException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class BillDialog extends JDialog {

    private final JEditorPane txtReceipt;
    private final int saleId;

    public BillDialog(Frame parent, int saleId, String cashierName, List<CartItem> items, double totalAmount) {
        super(parent, "Customer Invoice - #" + saleId, true);
        this.saleId = saleId;

        setSize(460, 620);
        setMinimumSize(new Dimension(420, 550));
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        txtReceipt = new JEditorPane();
        txtReceipt.setEditable(false);
        txtReceipt.setContentType("text/html");
        txtReceipt.setText(generateReceiptHtml(saleId, cashierName, items, totalAmount));
        txtReceipt.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(txtReceipt);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        actionPanel.setBackground(UITheme.CANVAS_BG);
        actionPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.CARD_BORDER));

        ActionButton btnPrint = ActionButton.primary("Print / Save Bill");
        btnPrint.setPreferredSize(new Dimension(150, 35));
        btnPrint.addActionListener(e -> handlePrint());

        ActionButton btnClose = ActionButton.secondary("Close");
        btnClose.setPreferredSize(new Dimension(90, 35));
        btnClose.addActionListener(e -> dispose());

        actionPanel.add(btnPrint);
        actionPanel.add(btnClose);

        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private String generateReceiptHtml(int id, String cashier, List<CartItem> items, double grandTotal) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = LocalDateTime.now().format(dtf);
        double vat = grandTotal * 0.15;
        double net = grandTotal - vat;

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family:Segoe UI, sans-serif; font-size:11px; color:#0F172A; padding:10px;'>")
          .append("<div style='text-align:center;'>")
          .append("<h2 style='margin:0; color:#0D9488;'>HEALTHFIRST PHARMACY</h2>")
          .append("<p style='margin:2px; font-size:10px; color:#64748B;'>Registration No: 2026/HF/0984<br>")
          .append("24 Medical Centre Way, Pretoria, South Africa</p>")
          .append("<hr style='border: 0; border-top: 1px dashed #CBD5E1; margin: 10px 0;'>")
          .append("</div>")
          .append("<table style='width:100%; font-size:10px; margin-bottom:8px;'>")
          .append("<tr><td><b>Invoice:</b> #").append(id).append("</td><td style='text-align:right;'><b>Date:</b> ").append(formattedDate).append("</td></tr>")
          .append("<tr><td colspan='2'><b>Cashier:</b> ").append(cashier).append("</td></tr>")
          .append("</table>")
          .append("<table style='width:100%; border-collapse:collapse; font-size:10px;'>")
          .append("<tr style='background-color:#F1F5F9; border-bottom:1px solid #CBD5E1;'>")
          .append("<th style='text-align:left; padding:4px;'>Item</th>")
          .append("<th style='text-align:center; padding:4px;'>Qty</th>")
          .append("<th style='text-align:right; padding:4px;'>Price</th>")
          .append("<th style='text-align:right; padding:4px;'>Total</th>")
          .append("</tr>");

        for (CartItem item : items) {
            sb.append("<tr style='border-bottom:1px solid #F1F5F9;'>")
              .append("<td style='padding:4px;'>").append(item.getMedicineName()).append("</td>")
              .append("<td style='text-align:center; padding:4px;'>").append(item.getQuantity()).append("</td>")
              .append("<td style='text-align:right; padding:4px;'>R ").append(String.format("%.2f", item.getPrice())).append("</td>")
              .append("<td style='text-align:right; padding:4px;'>R ").append(String.format("%.2f", item.getSubtotal())).append("</td>")
              .append("</tr>");
        }

        sb.append("</table>")
          .append("<hr style='border: 0; border-top: 1px dashed #CBD5E1; margin: 10px 0;'>")
          .append("<table style='width:100%; font-size:11px;'>")
          .append("<tr><td style='text-align:right;'>Subtotal (Excl. VAT):</td><td style='text-align:right; width:90px;'><b>R ")
          .append(String.format("%.2f", net)).append("</b></td></tr>")
          .append("<tr><td style='text-align:right;'>VAT (15%):</td><td style='text-align:right;'><b>R ")
          .append(String.format("%.2f", vat)).append("</b></td></tr>")
          .append("<tr><td style='text-align:right; font-size:13px; color:#0D9488;'><b>Grand Total:</b></td>")
          .append("<td style='text-align:right; font-size:13px; color:#0D9488;'><b>R ")
          .append(String.format("%.2f", grandTotal)).append("</b></td></tr>")
          .append("</table>")
          .append("<div style='text-align:center; margin-top:20px; font-size:9px; color:#64748B;'>")
          .append("<p>Thank you for choosing HealthFirst Pharmacy.<br>Items dispensed in accordance with healthcare regulations.</p>")
          .append("</div></body></html>");

        return sb.toString();
    }

    private void handlePrint() {
        try {
            boolean complete = txtReceipt.print();
            if (complete) {
                JOptionPane.showMessageDialog(this, "Bill printed or saved successfully.", 
                                              "Print Confirmation", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Printer communication failed: " + ex.getMessage(), 
                                          "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}