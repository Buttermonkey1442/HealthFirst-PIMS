CREATE DATABASE IF NOT EXISTS pims_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE pims_db;

DROP TABLE IF EXISTS sale_items;
DROP TABLE IF EXISTS sales;
DROP TABLE IF EXISTS medicines;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('Admin', 'Cashier') NOT NULL,
    full_name VARCHAR(100) NOT NULL
);

CREATE TABLE suppliers (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    address TEXT NOT NULL
);

CREATE TABLE medicines (
    medicine_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    company VARCHAR(100) NOT NULL,
    medicine_type VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity_in_stock INT NOT NULL DEFAULT 0,
    reorder_level INT NOT NULL DEFAULT 10,
    expiry_date DATE NOT NULL,
    supplier_id INT NOT NULL,
    CONSTRAINT fk_medicines_supplier 
        FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
        ON UPDATE CASCADE 
        ON DELETE RESTRICT
);

CREATE TABLE sales (
    sale_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    user_id INT NOT NULL,
    CONSTRAINT fk_sales_user 
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON UPDATE CASCADE 
        ON DELETE RESTRICT
);

CREATE TABLE sale_items (
    sale_item_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    medicine_id INT NOT NULL,
    quantity_sold INT NOT NULL,
    price_at_sale DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_sale_items_parent 
        FOREIGN KEY (sale_id) REFERENCES sales(sale_id)
        ON UPDATE CASCADE 
        ON DELETE CASCADE,
    CONSTRAINT fk_sale_items_medicine 
        FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id)
        ON UPDATE CASCADE 
        ON DELETE RESTRICT
);

INSERT INTO users (username, password, role, full_name) VALUES
('admin', 'admin123', 'Admin', 'Lead Pharmacist Admin'),
('cashier', 'cash123', 'Cashier', 'Front Desk Cashier');

INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES
('Adcock Ingram Healthcare', 'Sipho Ndlovu', '+27 11 635 0000', 'orders@adcock.co.za', '1 New Rd, Midrand, Johannesburg'),
('Aspen Pharmacare', 'Candice Meyer', '+27 31 580 8600', 'supply@aspenpharma.com', 'Building 8, Healthcare Park, Durban'),
('Cipla Medpro South Africa', 'Faizel Patel', '+27 21 943 4200', 'contact@cipla.co.za', 'Rosen Heights, Bellville, Cape Town');

INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id) VALUES
('Amoxicillin 500mg', 'Adcock Ingram', 'Capsule', 94.50, 80, 20, '2027-11-15', 1),
('Panado 500mg Tablets', 'Aspen Pharmacare', 'Tablet', 38.00, 250, 40, '2028-06-30', 2),
('Benylin 4-Flu Liquid', 'Johnson & Johnson', 'Syrup', 89.90, 8, 15, '2027-04-10', 2),
('Allergex Cream 20g', 'Adcock Ingram', 'Cream', 45.00, 6, 12, DATE_ADD(CURDATE(), INTERVAL 14 DAY), 1),
('Voltaren Emulgel 50g', 'Novartis', 'Cream', 135.00, 35, 10, DATE_ADD(CURDATE(), INTERVAL 25 DAY), 3);