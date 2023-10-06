package org.zkoss.stateless.demo.db.dao;

import org.zkoss.stateless.demo.pojo.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderInMemory implements OrderDao{
    static AtomicInteger currentId = new AtomicInteger(0);
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
    public Item findItem(int itemId) {
        for (Order order : orderList) {
            for (Item item : order.getItems()) {
                if (item.getId().equals(itemId)) {
                    return item;
                }
            }
        }
        return null;
    }

    @Override
    public void updateQuantity(int itemId, int quantity, int price) {
        Item item = findItem(itemId);
        if (item != null) {
            item.setQuantity(quantity);
            item.setPrice(price);
        }
    }

    @Override
    public void delete(int itemId) {
        for (Order order : orderList) {
            for (Item item : order.getItems()) {
                if (item.getId().equals(itemId)){
                    order.getItems().remove(item);
                    break;
                }
            }
        }
    }

    @Override
    public void submit(String orderId) {

    }

    @Override
    public void updateProduct(int itemId, String productName, Integer subTotal) {
        Item item = findItem(itemId);
        if (item != null) {
            item.setProductName(productName);
            item.setSubTotal(subTotal);
        }
    }

    @Override
    public void updateSize(int itemId, String size) {
        Item item = findItem(itemId);
        if (item != null) {
            item.setSize(size);
        }

    }

    @Override
    public int totalPrice(String orderId) {
        int totalPrice = 0;
        for (Order order : orderList) {
            if (order.getId().equals(orderId)){
                for (Item item : order.getItems()){
                    totalPrice += item.getSubTotal();
                }
                break;
            }
        }
        return totalPrice;
    }

    @Override
    public int count(String orderId) {
        for (Order order : orderList) {
            if (order.getId().equals(orderId)){
                return order.getItems().size();
            }
        }
        return 0;
    }
}
