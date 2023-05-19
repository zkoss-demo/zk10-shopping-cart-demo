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
    @Override
    public void updateQuantity(int uuid, int quantity, int price) {
        for (Order order : orderList) {
            for (Item item : order.getItems()) {
                if (item.getId().equals(uuid)){
                    item.setQuantity(quantity);
                    item.setPrice(price);
                    break;
                }
            }
        }
    }

    @Override
    public void delete(int uuid) {
        for (Order order : orderList) {
            for (Item item : order.getItems()) {
                if (item.getId().equals(uuid)){
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
    public void updateProduct(int uuid, String productName, Integer subTotal) {
        for (Order order : orderList) {
            for (Item item : order.getItems()) {
                if (item.getId().equals(uuid)){
                    item.setProductName(productName);
                    break;
                }
            }
        }
    }

    @Override
    public void updateSize(int uuid, String size) {

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
