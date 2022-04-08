/* DaoFactory.java

	Purpose:

	Description:

	History:
		Tue Mar 15 10:06:57 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.demo.db.factory;

import org.zkoss.zephyr.demo.db.service.OrderService;

/**
 * @author katherine
 */
public class DaoFactory {
	public static OrderService getInstance() {
		OrderService dao = null;
		try {
			dao = new OrderService();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dao;
	}
}