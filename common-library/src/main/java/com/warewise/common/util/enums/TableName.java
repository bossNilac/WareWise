package com.warewise.common.util.enums;

/**
 * Enum representing all table names in the database.
 */
public enum TableName {
    USERS("users"),
    INVENTORY("inventory"),
    ORDERS("orders"),
    ORDER_ITEMS("order_items"),
    SUPPLIERS("suppliers"),
    CATEGORIES("categories"),
    LOGS("logs"),
    STOCK_ALERTS("stock_alerts");

    private final String tableName;

    TableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Get the table name as a string.
     *
     * @return The table name.
     */
    public String getTableName() {
        return tableName;
    }

    @Override
    public String toString() {
        return tableName;
    }
}
