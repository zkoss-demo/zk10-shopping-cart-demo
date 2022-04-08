/* CartLocator.java

	Purpose:

	Description:

	History:
		Mon Mar 14 14:36:50 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.demo.util;

import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.Self;

/**
 * @author katherine
 */
public class CartLocator {

	public static Locator getPriceLocator(Self self) {
		return self.nextSibling().nextSibling().nextSibling();
	}

	public static Locator getTotalLocator(Self self) {
		return self.nextSibling().nextSibling().nextSibling().nextSibling();
	}
}
