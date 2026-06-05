package com.warewise.client.util.model;

/**
     * Model class representing a low stock notification entry.
     */
    public class LowStockNotification {
        private final int inventoryId;
        private final String itemName;
        private final String orderDate;
        private final String userName;
        private final String categoryName;
        private final String inventoryName;
        private final String warehouseName;

        public LowStockNotification(int inventoryId,
                                    String itemName,
                                    String orderDate,
                                    String userName,
                                    String categoryName,
                                    String inventoryName,
                                    String warehouseName) {
            this.inventoryId = inventoryId;
            this.itemName = itemName;
            this.orderDate = orderDate;
            this.userName = userName;
            this.categoryName = categoryName;
            this.inventoryName = inventoryName;
            this.warehouseName = warehouseName;
        }

        public int getInventoryId() {
            return inventoryId;
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
