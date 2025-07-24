package com.warewise.client.util.model;

/**
     * Model class representing a low stock notification entry.
     */
    public class LowStockNotification {
        private final String itemName;
        private final String orderDate;
        private final String userName;
        private final String categoryName;
        private final String inventoryName;
        private final String warehouseName;

        public LowStockNotification(String itemName,
                                    String orderDate,
                                    String userName,
                                    String categoryName,
                                    String inventoryName,
                                    String warehouseName) {
            this.itemName = itemName;
            this.orderDate = orderDate;
            this.userName = userName;
            this.categoryName = categoryName;
            this.inventoryName = inventoryName;
            this.warehouseName = warehouseName;
        }

        public String getItemName() {
            return itemName;
        }

        public String getOrderDate() {
            return orderDate;
        }

        public String getUserName() {
            return userName;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public String getInventoryName() {
            return inventoryName;
        }

        public String getWarehouseName() {
            return warehouseName;
        }
    }