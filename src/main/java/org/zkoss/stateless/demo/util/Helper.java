/* Helper.java

	Purpose:

	Description:

	History:
		3:38 PM 2022/4/12, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.demo.util;

import java.util.UUID;

import org.zkoss.stateless.ui.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;

/**
 * Helper class for this demo.
 * @author jumperchen
 */
public class Helper {
	public static Locator getPriceLocator(Self self) {
		return self.nextSibling().nextSibling().nextSibling();
	}

	public static Locator getTotalLocator(Self self) {
		return getPriceLocator(self).nextSibling();
	}

	public static Locator getTotalLocatorFromQuantity(Self self) {
		return self.nextSibling().nextSibling();
	}

	public static String nextUuid() {
		return UUID.randomUUID().toString().substring(0, 6);
	}

	public static String combine(String id, String name) {
		return id + "-" + name;
	}

	public static String parseOrderId(String id) {
		return id.split("-")[0];
	}
	public static String parseItemId(String id) {
		return id.split("-")[1];
	}

	public static void log(String act) {
		Clients.evalJavaScript(String.format("console.log('%s')", String.join(": ", String.valueOf(Executions.getCurrent().getLocalAddr()), act)));
	}
}
