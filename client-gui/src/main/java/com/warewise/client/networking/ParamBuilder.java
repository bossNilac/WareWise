package com.warewise.client.networking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.warewise.client.util.model.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class ParamBuilder {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String buildParamsUser(boolean isAdd, User user) {
        Map<String, Object> m = new LinkedHashMap<>();
        if (!isAdd) {
            m.put("userId", user.getID());
        }
        m.put("username",    user.getUsername());
        m.put("password",    user.getPasswordHash());
        m.put("email",       user.getEmail());
        m.put("role",        user.getRole().name());
        m.put("warehouseId", user.getWarehouseId());
        return gson.toJson(m);
    }

    public static String buildParamsCategory(boolean isAdd, Category cat) {
        Map<String, Object> m = new LinkedHashMap<>();
        if (!isAdd) {
            m.put("categoryId", cat.getID());
        }
        m.put("name",        cat.getName());
        m.put("description", cat.getDescription());
        return gson.toJson(m);
    }

    public static String buildParamsInventory(boolean isAdd, Inventory inv) {
        Map<String, Object> m = new LinkedHashMap<>();
        if (!isAdd) {
            m.put("inventoryId", inv.getID());
        }
        m.put("name",          inv.getName());
        m.put("stockQuantity", inv.getQuantity());
        m.put("description",   inv.getDescription());
        m.put("lastUpdated",   inv.getLastUpdated());
        m.put("warehouseId",   inv.getWarehouseId());
        return gson.toJson(m);
    }

    public static String buildParamsItem(boolean isAdd, GeneralItem it) {
        Map<String, Object> m = new LinkedHashMap<>();
        if (!isAdd) {
            m.put("itemId",      it.getId());
        }
        m.put("barcode",     it.getBarcode());
        m.put("setQuantity",    it.getSetQuantity());
        m.put("price",       it.getPrice());
        m.put("name",       it.getName());
        m.put("categoryId",  it.getCategoryId());
        m.put("supplierId",  it.getSupplierId());
        m.put("expires",  it.getExpires());
        return gson.toJson(m);
    }

    public static String buildParamsWarehouseItem(boolean isAdd, WarehouseItem it) {
        Map<String, Object> m = new LinkedHashMap<>();
        if (!isAdd) {
            m.put("itemId",      it.getID());
        }
        m.put("orderId",     it.getOrderID());
        m.put("inventoryId",    it.getInventoryID());
        m.put("quantity",       it.getQuantity());
        m.put("total",       it.getTotal());
        m.put("general_item_id",  it.getGeneralItemId());
        m.put("expireDate",  it.getExpireDate());
        m.put("sold",  it.getSold());
        return gson.toJson(m);
    }

    public static String buildParamsOrder(boolean isAdd, Order ord) {
        Map<String, Object> m = new LinkedHashMap<>();
        if (!isAdd) {
            m.put("orderId", ord.getID());
        }
        m.put("customerName",  ord.getCustomerName());
        m.put("customerEmail", ord.getCustomerEmail());
        m.put("status",        ord.getStatus().name());
        m.put("createdAt",     ord.getCreatedAt());
        m.put("updatedAt",     ord.getUpdatedAt());
        m.put("userId",        ord.getUserId());
        return gson.toJson(m);
    }

    public static String buildParamsSupplier(boolean isAdd, Supplier sup) {
        Map<String, Object> m = new LinkedHashMap<>();
        if (!isAdd) {
            m.put("supplierId", sup.getID());
        }
        m.put("supplierName",  sup.getName());
        m.put("contactEmail",  sup.getContactEmail());
        m.put("contactPhone",  sup.getContactPhoneNo());
        m.put("address",       sup.getAddress());
        m.put("createdAt",     sup.getCreatedAt());
        return gson.toJson(m);
    }

    public static String buildParamsStockAlert(boolean isAdd, StockAlert a) {
        Map<String, Object> m = new LinkedHashMap<>();
        if (!isAdd) {
            m.put("alertId", a.getID());
        }
        m.put("productId", a.getProductID());
        m.put("createdAt", a.getCreatedAt());
        m.put("resolved",  a.getResolved());
        return gson.toJson(m);
    }

    public static String buildParamsWarehouse(boolean isAdd, Warehouse w) {
        Map<String, Object> m = new LinkedHashMap<>();
        if (!isAdd) {
            m.put("warehouseId", w.getID());
        }
        m.put("name",    w.getName());
        m.put("address", w.getAddress());
        return gson.toJson(m);
    }
}
