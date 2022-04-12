/* Helper.java

	Purpose:

	Description:

	History:
		3:38 PM 2022/4/12, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.demo.util;

import java.util.UUID;

import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.Self;
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

	public static String nextUuid() {
		return UUID.randomUUID().toString().substring(0, 6);
	}

	public static String uuid(String uuid, String name) {
		return uuid + "_" + name;
	}

	public static String parseUuid(String uuid) {
		return uuid.split("_")[0];
	}

	public static void log(String act) {
		Clients.log(String.join(": ", String.valueOf(Executions.getCurrent().getLocalAddr()), act));
	}
}
