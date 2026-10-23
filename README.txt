HEALTHFIRST - PHARMACY INVENTORY MANAGEMENT SYSTEM (PIMS)
USER MANUAL & SYSTEM DEPLOYMENT GUIDE
================================================================================

1. SYSTEM OVERVIEW
HealthFirst PIMS is a multi-user Java Swing desktop application connected via 
JDBC to a MySQL relational database. The system enforces strict role-based access 
control (RBAC) across two distinct user profiles: Administrators and Cashiers.

2. DEFAULT LOGIN CREDENTIALS
--------------------------------------------------------------------------------
Role            | Username   | Password   | Permissions[cite: 2, 3]
--------------------------------------------------------------------------------
Administrator   | admin      | admin123   | Full Access: Medicines CRUD, Suppliers 
                |            |            | CRUD, User Accounts CRUD, All Reports
Cashier         | cashier    | cash123    | Point of Sale (POS), Live Stock Lookup,
                |            |            | Order Processing, Customer Billing
--------------------------------------------------------------------------------

3. SYSTEM PREREQUISITES
- Java Runtime Environment (JRE) / JDK 17 or higher
- MySQL Server 8.0 or higher (Port 3306)
- MySQL Connector/J JDBC Driver (included in project libraries)

4. DATABASE CONFIGURATION
1. Launch MySQL Workbench.
2. Open and execute the script located at: /sql/database.sql
3. Verify that the schema 'pims_db' is created with all 5 normalized tables:
   - users
   - suppliers
   - medicines
   - sales
   - sale_items
4. If your MySQL root password is not 'password', update the DBConnection class:
   File: src/com/healthfirst/pims/util/DBConnection.java
   Modify constant: private static final String PASSWORD = "your_actual_password";

5. RUNNING THE SYSTEM
Option A: From NetBeans IDE
1. Open NetBeans IDE and select 'Open Project'.
2. Select 'HealthFirst_PIMS'.
3. Right-click the project root and select 'Clean and Build'.
4. Right-click 'src/com/healthfirst/pims/Main.java' and choose 'Run File'.

Option B: Using the Standalone Executable
1. Ensure MySQL Server is active on localhost:3306.
2. Launch the bundled executable: khweze_pims.exe

6. SUBMITTED SCREENSHOTS DIRECTORY (/screenshots)
1. 01_Login_Screen.png - Dual-role authentication screen
2. 02_Admin_Dashboard.png - Main admin console showing navigation tabs
3. 03_Manage_Suppliers.png - Supplier CRUD management grid with live data
4. 04_Cashier_POS_Cart.png - POS terminal showing active items staged in cart
5. 05_Generated_Bill.png - Rendered printable customer invoice window
6. 06_Report_Sales.png - General sales analytics report
7. 07_Report_ItemWise.png - Item-wise sales and revenue distribution report
8. 08_Report_LowStock.png - Low stock warning report with supplier contacts
9. 09_Report_Expiry.png - Medicines expiring within the 30-day window
================================================================================