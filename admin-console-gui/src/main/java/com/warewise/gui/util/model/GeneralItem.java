package com.warewise.gui.util.model;

public class GeneralItem {
    private int id;
    private String name;
    private int setQuantity;
    private String barcode;
    private int categoryId;
    private int price;
    private int supplierId;
    private boolean expires;

    public GeneralItem() {
    }

    public GeneralItem(int id, String name, int setQuantity, String barcode, int categoryId, int supplierId,int price, boolean expires) {
        this.id = id;
        this.name = name;
        this.setQuantity = setQuantity;
        this.barcode = barcode;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.price = price;
        this.expires = expires;
    }

    public GeneralItem(String name, int setQuantity, String barcode, int categoryId, int supplierId,int price,boolean expires) {
        this.price = price;
        this.name = name;
        this.setQuantity = setQuantity;
        this.barcode = barcode;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.expires = expires;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSetQuantity() {
        return setQuantity;
    }

    public void setSetQuantity(int setQuantity) {
        this.setQuantity = setQuantity;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean getExpires() {
        return expires;
    }

    public void setExpires(boolean expires) {
        this.expires = expires;
    }
}
