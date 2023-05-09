/* OrderDao.java

	Purpose:

	Description:

	History:
		Tue Mar 15 10:02:35 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.demo.db.dao;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.stateless.demo.pojo.Item;

/**
 * @author katherine
 */
public class OrderDaoImpl implements OrderDao{

	private static final String INSERT_ITEM = "insert into ORDER_LIST(PRODUCT_NAME,SIZE,QUANTITY,PRICE,SUB_TOTAL,STATUS,ORDER_ID)"
			+ "values('Cake','S',1,100,100,1,?)";

	private static final String SELECT_ORDER = "select * from ORDER_LIST where STATUS = " + Item.COMPLETE + " AND ORDER_ID = ?";

	private static final String UPDATE_PRODUCT_NAME = "update ORDER_LIST set PRODUCT_NAME = ? WHERE ID = ?";

	private static final String UPDATE_SIZE = "update ORDER_LIST set SIZE = ? WHERE ID = ?";

	private static final String UPDATE_QUANTITY = "update ORDER_LIST set QUANTITY = ? WHERE ID = ?";

	private static final String UPDATE_PRICE = "update ORDER_LIST set PRICE = ? WHERE ID = ?";

	private static final String UPDATE_SUB_TOTAL = "update ORDER_LIST set SUB_TOTAL = ? WHERE ID = ?";

	private static final String UPDATE_STATUS = "update ORDER_LIST set STATUS = ? WHERE ID = ?";

	private static final String UPDATE_STATUS_TO_COMPLETE = "update ORDER_LIST set STATUS = " + Item.COMPLETE +
			" WHERE ORDER_ID = ? AND STATUS = " + Item.NOT_COMPLETE;

	private static final String SUM_SUB_TOTAL = "select SUM(PRICE * QUANTITY) FROM ORDER_LIST where ORDER_ID = ? AND STATUS = " + Item.COMPLETE;

	private static final String ITEMS_COUNT = "select SUM(QUANTITY) FROM ORDER_LIST where ORDER_ID = ? AND STATUS = " + Item.COMPLETE;

	private static final Logger log = LoggerFactory.getLogger(OrderDaoImpl.class);
	static final String DRIVER = "org.postgresql.Driver";
	static final String USER = "zephyr_admin";
	static final String URL = "jdbc:postgresql://zephyr_db:5432/zephyr_db";
	static final String PASS = "zephyr_pwd";

	private Connection getConnection() {
		try {
			Class.forName(DRIVER);
			return DriverManager.getConnection(OrderDaoImpl.URL, OrderDaoImpl.USER, OrderDaoImpl.PASS);
		} catch (ClassNotFoundException e) {
			log.error("DriverClassNotFound :" , e);
		} catch (SQLException x) {
			log.error("Exception :" , x);
		}
		return null;
	}
	public String insertItem(String orderId) {
		int id = -1;
		try (Connection con = getConnection();
			PreparedStatement stat = con.prepareStatement(INSERT_ITEM,Statement.RETURN_GENERATED_KEYS)
		) {
			stat.setString(1, orderId);
			stat.executeUpdate();
			ResultSet rs = stat.getGeneratedKeys();
			if (rs.next())
				id =  rs.getInt(1);
			rs.close();
		} catch (Exception e) {
			log.error("Insert item exception :" , e);
		}
		return String.valueOf(id);
	}

	public List<Item> selectOrder(String orderId) {
		List<Item> items = new LinkedList<>();
		try (Connection con = getConnection();
			PreparedStatement stat = con.prepareStatement(SELECT_ORDER)) {
			stat.setString(1, orderId);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				items.add(new Item(rs.getString(2), rs.getString(3),
						rs.getInt(4), rs.getInt(5), rs.getInt(6)));
			}
			rs.close();
		} catch (Exception e) {
			log.error("Select order exception :", e);
		}
		return items;
	}


	private void updateProductName(int uuid, String productName) {
		try {
			execute(uuid, productName, UPDATE_PRODUCT_NAME);
		} catch (Exception e) {
			log.error("Update product name exception :" , e);
		}
	}

	private void execute(int uuid, String value, String statement)
			throws SQLException {
		try (Connection con = getConnection();
				PreparedStatement stat = con.prepareStatement(statement)) {
			stat.setString(1, value);
			stat.setInt(2, uuid);
			stat.executeUpdate();
		}
	}
	private void execute(int uuid, int value, String statement)
			throws SQLException {
		try (Connection con = getConnection();
				PreparedStatement stat = con.prepareStatement(statement)) {
			stat.setInt(1, value);
			stat.setInt(2, uuid);
			stat.executeUpdate();
		}
	}

	public void updateSize(int uuid, String size) {
		try {
			execute(uuid, size, UPDATE_SIZE);
		} catch (Exception e) {
			log.error("Update size exception :", e);
		}
	}

	public void updateQuantity(int uuid, int quantity, int price) {
		try {
			execute(uuid, quantity, UPDATE_QUANTITY);
		} catch (Exception e) {
			log.error("Update quantity exception :" , e);
		}
		updateSubTotal(uuid, quantity * price);
	}

	private void updatePrice(int uuid, int price) {
		try (Connection con = getConnection();
			PreparedStatement stat = con.prepareStatement(UPDATE_PRICE)) {
			stat.setInt(1, price);
			stat.setInt(2, uuid);
			stat.executeUpdate();
		} catch (Exception e) {
			log.error("Update price exception :", e);
		}
	}

	private void updateSubTotal(int uuid, int subTotal) {
		try {
			execute(uuid, subTotal, UPDATE_SUB_TOTAL);
		} catch (Exception e) {
			log.error("Update subTotal exception :" , e);
		}
	}

	private void updateStatus(int uuid, int status) {
		try {
			execute(uuid, status, UPDATE_STATUS);
		} catch (Exception e) {
			log.error("Update status exception :", e);
		}
	}

	public void delete(int uuid) {
		updateStatus(uuid, Item.DELETE);
	}

	public void submit(String orderId) {
		try (Connection con = getConnection();
			PreparedStatement stat = con.prepareStatement(UPDATE_STATUS_TO_COMPLETE)) {
			stat.setString(1, orderId);
			stat.executeUpdate();
		} catch (Exception e) {
			log.error("Submit exception :" , e);
		}
	}

	public void updateProduct(int uuid, String productName, Integer subTotal) {
		updateProductName(uuid, productName);
		updatePrice(uuid, getPrice(productName));
		updateSubTotal(uuid, subTotal);
	}

	public int totalPrice(String orderId) {
		int sum = 0;
		try (Connection con = getConnection();
			PreparedStatement stat = con.prepareStatement(SUM_SUB_TOTAL)
		) {
			stat.setString(1, orderId);
			stat.execute();
			ResultSet rs = stat.getResultSet();
			if (rs.next())
				sum = rs.getInt(1);
			rs.close();
		} catch (Exception e) {
			log.error("Sum sub total exception :" , e);
		}
		return sum;
	}

	public int count(String orderId) {
		int count = 0;
		try (Connection con = getConnection();
			PreparedStatement stat = con.prepareStatement(ITEMS_COUNT)
		) {
			stat.setString(1, orderId);
			stat.execute();
			ResultSet rs = stat.getResultSet();
			if (rs.next())
				count =  rs.getInt(1);
			rs.close();
		} catch (Exception e) {
			log.error("Count item exception :" , e);
		}
		return count;
	}

	private static int getPrice(String productName) {
		return Item.PRODUCT_TABLE.get(productName).getPrice();
	}
}
