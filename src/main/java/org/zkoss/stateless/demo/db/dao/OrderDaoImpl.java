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

import org.zkoss.stateless.demo.pojo.*;

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
	static final String USER = "admin";
	static final String URL = "jdbc:postgresql://demo_db:5432/demo_db";
	static final String PASS = "pwd";

	private Connection getConnection() {
		try {
			Class.forName(DRIVER);
			return DriverManager.getConnection(OrderDaoImpl.URL, OrderDaoImpl.USER, OrderDaoImpl.PASS);
		} catch (ClassNotFoundException e) {
			log.error("DriverClassNotFound :" , e);
			throw new RuntimeException(e);
		} catch (SQLException x) {
			log.error("connect with {}, {}, {}", OrderDaoImpl.URL, OrderDaoImpl.USER, OrderDaoImpl.PASS);
			log.error("Exception :" , x);
			throw new RuntimeException(x);
		}
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
		} catch (SQLException e) {
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
		} catch (SQLException e) {
			log.error("Select order exception :", e);
		}
		return items;
	}


	private void updateProductName(int itemId, String productName) {
		try {
			execute(itemId, productName, UPDATE_PRODUCT_NAME);
		} catch (SQLException e) {
			log.error("Update product name exception :" , e);
		}
	}

	private void execute(int itemId, String value, String statement)
			throws SQLException {
		try (Connection con = getConnection();
				PreparedStatement stat = con.prepareStatement(statement)) {
			stat.setString(1, value);
			stat.setInt(2, itemId);
			stat.executeUpdate();
		}
	}
	private void execute(int itemId, int value, String statement)
			throws SQLException {
		try (Connection con = getConnection();
				PreparedStatement stat = con.prepareStatement(statement)) {
			stat.setInt(1, value);
			stat.setInt(2, itemId);
			stat.executeUpdate();
		}
	}

	public void updateSize(int itemId, String size) {
		try {
			execute(itemId, size, UPDATE_SIZE);
		} catch (SQLException e) {
			log.error("Update size exception :", e);
		}
	}

	public void updateQuantity(int itemId, int quantity, int price) {
		try {
			execute(itemId, quantity, UPDATE_QUANTITY);
		} catch (SQLException e) {
			log.error("Update quantity exception :" , e);
		}
		updateSubTotal(itemId, quantity * price);
	}

	private void updatePrice(int itemId, int price) {
		try (Connection con = getConnection();
			PreparedStatement stat = con.prepareStatement(UPDATE_PRICE)) {
			stat.setInt(1, price);
			stat.setInt(2, itemId);
			stat.executeUpdate();
		} catch (SQLException e) {
			log.error("Update price exception :", e);
		}
	}

	private void updateSubTotal(int itemId, int subTotal) {
		try {
			execute(itemId, subTotal, UPDATE_SUB_TOTAL);
		} catch (SQLException e) {
			log.error("Update subTotal exception :" , e);
		}
	}

	private void updateStatus(int itemId, int status) {
		try {
			execute(itemId, status, UPDATE_STATUS);
		} catch (SQLException e) {
			log.error("Update status exception :", e);
		}
	}

	public void delete(int itemId) {
		updateStatus(itemId, Item.DELETE);
	}

	public void submit(String orderId) {
		try (Connection con = getConnection();
			PreparedStatement stat = con.prepareStatement(UPDATE_STATUS_TO_COMPLETE)) {
			stat.setString(1, orderId);
			stat.executeUpdate();
		} catch (SQLException e) {
			log.error("Submit exception :" , e);
		}
	}

	public void updateProduct(int itemId, String productName, Integer subTotal) {
		updateProductName(itemId, productName);
		updatePrice(itemId, getPrice(productName));
		updateSubTotal(itemId, subTotal);
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
		} catch (SQLException e) {
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
		} catch (SQLException e) {
			log.error("Count item exception :" , e);
		}
		return count;
	}

	private static int getPrice(String productName) {
		return Product.PRODUCT_TABLE.get(productName).getPrice();
	}
}
