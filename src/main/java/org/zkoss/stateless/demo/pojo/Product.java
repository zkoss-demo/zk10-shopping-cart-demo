/* Product.java

	Purpose:

	Description:

	History:
		3:25 PM 2022/4/12, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.demo.pojo;

import java.util.*;

/**
 * @author jumperchen
 */
public class Product {
	public static final Map<String, Product> PRODUCT_TABLE = new LinkedHashMap<>(3);
	public static final Product DEFAULT_PRODUCT = new Product("Cake", 100, "/image/cake.svg");

	static {
		Product.PRODUCT_TABLE.put("Cake", Product.DEFAULT_PRODUCT);
		Product.PRODUCT_TABLE.put("Hamburger", new Product("Hamburger", 200, "/image/hamburger.svg"));
		Product.PRODUCT_TABLE.put("Pizza", new Product("Pizza", 300, "/image/pizza.svg"));
	}
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
