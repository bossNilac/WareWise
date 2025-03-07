package com.warewise.server.server.util;

import com.warewise.common.model.*;
import com.warewise.server.server.Server;

import java.util.Objects;

public class ServerUtil {

    private Server server;

    public ServerUtil(Server server){
        this.server = server;
    }

    public User userExists(String username) {
        for (User user : server.getUsers()){
            if (user.getUsername().equals(username)){
                return user;
            }
        }
        return null;
    }

    public Category categoryExists(int id) {
        for (Category category : server.getCategories()){
            if (category.getID()==id){
                return category;
            }
        }
        return null;
    }

    public Item itemExists(int id) {
        for (Item item : server.getItems()){
            if (item.getID()==id){
                return item;
            }
        }
        return null;
    }

    public Category categoryExists(String name) {
        for (Category category : server.getCategories()){
            if (category.getName().equals( name)){
                return category;
            }
        }
        return null;
    }

    public Inventory inventoryExists(int id) {
        for (Inventory inventory : server.getInventories()){
            if (inventory.getID()==id){
                return inventory;
            }
        }
        return null;
    }

    public Order orderExists(int id) {
        for (Order order : server.getOrders()){
            if (order.getID()==id){
                return order;
            }
        }
        return null;
    }



}
