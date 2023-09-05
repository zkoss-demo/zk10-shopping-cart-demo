/* OrderService.java

	Purpose:

	Description:

	History:
		Tue Mar 15 10:08:35 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.demo.db.service;

import java.io.*;
import java.util.*;

import org.zkoss.stateless.demo.db.dao.*;
import org.zkoss.stateless.demo.pojo.Item;

/**
 * An order service provides applications to communicate with the database.
 * @author katherine
 */
public class OrderService {

	private OrderDao dao;


	public OrderService() {
		Properties prop = loadProperties("config.properties");
		if (prop != null) {
			String debugProperty = prop.getProperty("debug");
			if ("true".equalsIgnoreCase(debugProperty)) {
				this.dao = new OrderInMemory();
			} else {
				this.dao = new OrderDaoImpl();
			}
		}
	}

	private Properties loadProperties(String fileName) {
		Properties prop = new Properties();
		try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
			if (input == null) {
				System.out.println("Sorry, unable to find " + fileName);
				return null;
			}

			// Load the properties
			prop.load(input);
			return prop;

		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
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
