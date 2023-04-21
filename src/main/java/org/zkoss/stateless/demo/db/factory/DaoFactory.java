/* DaoFactory.java

	Purpose:

	Description:

	History:
		Tue Mar 15 10:06:57 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.demo.db.factory;

import org.zkoss.stateless.demo.db.service.OrderService;

/**
 * @author katherine
 */
public enum DaoFactory {
	INSTANCE(new OrderService());
	final OrderService service;
	DaoFactory(OrderService dao) {
		this.service = dao;
	}

	public OrderService getService() {
		return service;
	}
}