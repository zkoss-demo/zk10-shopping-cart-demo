package org.zkoss.stateless.demo.db.dao;

import org.zkoss.stateless.demo.pojo.Item;

import java.util.List;

public interface OrderDao {

    /**
     *
     * @param orderId
     * @return unique item id
     */
    String insertItem(String orderId);
    List<Item> selectOrder(String orderId);

    void updateQuantity(int itemId, int quantity, int price);

    void delete(int itemId);

    void submit(String orderId);

    void updateProduct(int itemId, String productName, Integer subTotal);

    void updateSize(int itemId, String size);
    int totalPrice(String orderId);

    int count(String orderId) ;
}
