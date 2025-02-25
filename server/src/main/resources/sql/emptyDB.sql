-- Disable foreign key constraints temporarily
PRAGMA foreign_keys = OFF;

-- Delete data from all tables.
DELETE FROM order_items;
DELETE FROM orders;
DELETE FROM logs;
DELETE FROM stock_alerts;
DELETE FROM inventory;
DELETE FROM suppliers;
DELETE FROM categories;
DELETE FROM users;

-- Reset auto-increment counters
DELETE FROM sqlite_sequence;

-- Re-enable foreign key constraints
PRAGMA foreign_keys = ON;
