package com.warewise.gui.util.model;

public class Warehouse {
    private int warehouse_id;
    private String name;
    private String address;

    public Warehouse() {
    }

    public Warehouse(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public Warehouse(int warehouse_id, String name, String address) {
        this.warehouse_id = warehouse_id;
        this.name = name;
        this.address = address;
    }

    public int getID() {
        return warehouse_id;
    }

    public void setID(int warehouse_id) {
        this.warehouse_id = warehouse_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
