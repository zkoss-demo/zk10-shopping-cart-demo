package org.zkoss.stateless.demo.pojo;

import java.util.*;

public class Order {
    private String id;
    private List<Item> items = new LinkedList<>();

    public Order(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
