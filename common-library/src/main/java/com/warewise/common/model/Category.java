package com.warewise.common.model;

import com.warewise.server.database.handler.SequenceManager;
import com.warewise.common.util.enums.TableName;

public class Category {
    private int ID;
    private String name;
    private String description;

    public Category(int ID, String name, String description) {
        this.ID = ID;
        this.name = name;
        this.description = description;
    }

    public Category(String name, String description) {
        this.setID(SequenceManager.getInstance().getNextId(TableName.CATEGORIES.getTableName()));
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString(){
        String output = "";
        output = output + ID +",";
        output = output + name+",";
        output = output + description+",";
        return output;
    }

}
