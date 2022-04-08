/* OrderService.java

	Purpose:

	Description:

	History:
		Tue Mar 15 10:08:35 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.demo.db.service;

import java.util.List;

import org.zkoss.zephyr.demo.db.dao.OrderDao;
import org.zkoss.zephyr.demo.pojo.Item;

/**
 * An order service provides applications to communicate with the database.
 * @author katherine
 */
public class OrderService {

	private OrderDao dao;

	public OrderService() {
		dao = new OrderDao();
	}

	public String insertItem(String orderId) {
		return dao.insertItem(orderId);
	}

	public void updateProduct(String orderId, String productName, Integer subTotal) {
		dao.updateProduct(Integer.parseInt(orderId), productName, subTotal);
	}

	public void updateSize(String orderId, String size) {
		dao.updateSize(Integer.parseInt(orderId), size);

	}

	public void updateQuantity(String orderId, int quantity, Integer price) {
		dao.updateQuantity(Integer.parseInt(orderId), quantity, price);
	}

	public List<Item> selectOrder(String orderId) {
		return dao.selectOrder(orderId);
	}

	public void submit() {
		dao.submit();
	}

	public void delete(String id) {
		dao.delete(Integer.parseInt(id));
	}

	public int sum(String orderId) { return dao.sum(orderId); }

	public int count(String orderId) { return dao.count(orderId); }

}
