/* Product.java

	Purpose:

	Description:

	History:
		3:25 PM 2022/4/12, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.demo.pojo;

/**
 * @author jumperchen
 */
public class Product {
	final private String name;
	final private int price;
	final private String icon;

	public Product(String name, int price, String icon) {
		this.name = name;
		this.price = price;
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public int getPrice() {
		return price;
	}

	public String getIcon() {
		return icon;
	}
}
