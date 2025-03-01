package com.warewise.common.model;

import com.warewise.server.database.handler.SequenceManager;
import com.warewise.common.util.enums.TableName;

public class Supplier {
    private int ID;
    private String name;
    private String contactEmail;
    private String contactPhoneNo;
    private String address;
    private String createdAt;

    public Supplier(int ID, String name, String contactEmail, String contactPhoneNo, String address) {
        this.ID = ID;
        this.name = name;
        this.contactEmail = contactEmail;
        this.contactPhoneNo = contactPhoneNo;
        this.address = address;
    }

    public Supplier( String name, String contactEmail, String contactPhoneNo, String address) {
        this.name = name;
        this.setID(SequenceManager.getInstance().getNextId(TableName.SUPPLIERS.getTableName()));
        this.contactEmail = contactEmail;
        this.contactPhoneNo = contactPhoneNo;
        this.address = address;
    }

    public Supplier(int ID, String supplierName, String contactEmail, String contactPhoneNo, String address, String createdAt) {
        this.ID = ID;
        this.name = supplierName;
        this.contactEmail = contactEmail;
        this.contactPhoneNo = contactPhoneNo;
        this.address = address;
        this.createdAt = createdAt;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhoneNo() {
        return contactPhoneNo;
    }

    public void setContactPhoneNo(String contactPhoneNo) {
        this.contactPhoneNo = contactPhoneNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String toString(){
        String output = "";
        output = output + ID +",";
        output = output + name+",";
        output = output + contactEmail+",";
        output = output + contactPhoneNo+",";
        output = output + address+",";
        output = output + createdAt;
        return output;
    }

}

