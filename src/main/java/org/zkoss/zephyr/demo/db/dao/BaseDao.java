/* BaseDao.java

	Purpose:

	Description:

	History:
		Fri Mar 11 15:40:54 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.demo.db.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author katherine
 */
public class BaseDao {

	private static final String DRIVER = "org.postgresql.Driver";

	private static final String USER = "zephyr_admin";

	private static final String URL = "jdbc:postgresql://zephyr_db:5432/zephyr_db";

	private static final String PASS = "zephyr_pwd";

	private static final Logger log = LoggerFactory.getLogger(BaseDao.class);

	public Connection getConnection() {
		try {
			Class.forName(DRIVER);
			return DriverManager.getConnection(URL, USER, PASS);
		} catch (ClassNotFoundException e) {
			log.error("DriverClassNotFound :" + e);
		} catch (SQLException x) {
			log.error("Exception :" + x);
		}
		return null;
	}
}
