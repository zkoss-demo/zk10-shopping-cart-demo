package org.zkoss.stateless.demo.db.dao;

import org.zkoss.stateless.demo.pojo.Item;

import java.sql.*;
import java.util.List;

public interface OrderDao {

    /**
     *
     * @param orderId
     * @return unique item id
     */
    String insertItem(String orderId);
    List<Item> selectOrder(String orderId);

    void updateQuantity(int uuid, int quantity, int price);

    void delete(int uuid);

    void submit(String orderId);

    void updateProduct(int uuid, String productName, Integer subTotal);

    void updateSize(int uuid, String size);
    int sum(String orderId);

    int count(String orderId) ;
}
