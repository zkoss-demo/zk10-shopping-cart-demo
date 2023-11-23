/* Item.java

	Purpose:

	Description:

	History:
		Fri Mar 11 15:32:16 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.demo.pojo;

/**
 * @author katherine
 */
public class Item {
	private Integer id;

	private String productName;

	private String size;

	private Integer quantity;

	private Integer price;

	private Integer subTotal;

	private Integer status;

	public static final int NOT_COMPLETE = 1;

	public static final int COMPLETE = 2;

	public static final int DELETE = 3;

	public Item() {
	}

	public Item(String productName, String size, Integer quantity, Integer price, Integer subTotal) {
		this.productName = productName;
		this.size = size;
		this.quantity = quantity;
		this.price = price;
		this.subTotal = subTotal;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getSubTotal() {
		return price * quantity;
	}

	public void setSubTotal(Integer subTotal) {
		this.subTotal = subTotal;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer submit) {
		this.status = status;
	}
}
