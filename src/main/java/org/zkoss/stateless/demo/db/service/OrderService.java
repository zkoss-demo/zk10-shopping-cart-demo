/* OrderService.java

	Purpose:

	Description:

	History:
		Tue Mar 15 10:08:35 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.demo.db.service;

import java.util.List;

import org.zkoss.stateless.demo.db.dao.*;
import org.zkoss.stateless.demo.pojo.Item;

/**
 * An order service provides applications to communicate with the database.
 * @author katherine
 */
public class OrderService {

	private OrderDao dao;

	public OrderService() {
		dao = new OrderDaoImpl();
//		dao = new OrderInMemory();
	}

	public String insertItem(String orderId) {
		return dao.insertItem(orderId);
	}

	public void updateProduct(String itemId, String productName, Integer subTotal) {
		dao.updateProduct(Integer.parseInt(itemId), productName, subTotal);
	}

	public void updateSize(String itemId, String size) {
		dao.updateSize(Integer.parseInt(itemId), size);
	}

	public void updateQuantity(String itemId, int quantity, Integer price) {
		dao.updateQuantity(Integer.parseInt(itemId), quantity, price);
	}

	public List<Item> selectOrder(String orderId) {
		return dao.selectOrder(orderId);
	}

	public void submit(String orderId) {
		dao.submit(orderId);
	}

	public void delete(String itemId) {
		dao.delete(Integer.parseInt(itemId));
	}

	public int sum(String orderId) { return dao.totalPrice(orderId); }

	public int count(String orderId) { return dao.count(orderId); }

}
