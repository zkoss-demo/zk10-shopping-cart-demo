package org.zkoss.stateless.demo.db.dao;

import org.zkoss.stateless.demo.pojo.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderInMemory implements OrderDao{
    static AtomicInteger currentId = new AtomicInteger(0);
    static List<Item> itemList = new ArrayList<Item>();
    static List<Order> orderList = new ArrayList();

    @Override
    public String insertItem(String orderId) {
        Order order = findOrCreateOrder(orderId);
        Integer itemId = currentId.incrementAndGet();
        Item newItem = new Item(Item.DEFAULT_PRODUCT.getName(), "S", 1, Item.DEFAULT_PRODUCT.getPrice(), 100);
        newItem.setId(itemId);
        order.getItems().add(newItem);
        orderList.add(order);
        return itemId.toString();
    }

    @Override
    public List<Item> selectOrder(String orderId) {
        List<Item> result = new ArrayList<Item>();
        for (Order order : orderList) {
            if (order.getId().equals(orderId)){
                result = order.getItems();
            }
        }
        return result;
    }

    Order findOrCreateOrder(String orderId){
        Order result = null;
        for (Order order : orderList) {
            if (order.getId().equals(orderId)){
                result = order;
            }
        }
        if (result == null){
            result = new Order(orderId);
        }
        return result;
    }
    @Override
    public void updateQuantity(int uuid, int quantity, int price) {

    }

    @Override
    public void delete(int uuid) {

    }

    @Override
    public void submit(String orderId) {

    }

    @Override
    public void updateProduct(int uuid, String productName, Integer subTotal) {

    }

    @Override
    public void updateSize(int uuid, String size) {

    }

    @Override
    public int totalPrice(String orderId) {
        return 0;
    }

    @Override
    public int count(String orderId) {
        return 0;
    }
}
