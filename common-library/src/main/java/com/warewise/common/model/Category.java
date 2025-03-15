package com.warewise.common.model;

import com.warewise.common.util.SequenceManager;
import com.warewise.common.util.enums.TableName;
import com.warewise.common.util.protocol.Protocol;

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
        output = output + ID + Protocol.ARG_SEPARATOR;
        output = output + name+Protocol.ARG_SEPARATOR;
        output = output + description+Protocol.ARG_SEPARATOR;
        return output;
    }

}
