/* DemoRichletTest.java

	Purpose:
		
	Description:
		
	History:
		2:35 PM 2022/8/25, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.demo.richlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.palantir.docker.compose.DockerComposeExtension;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.WebDriver;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A simple test case for ZK 10 API
 * @author jumperchen
 */
public class ShoppingCartRichletTest extends WebDriverTestCase {
	@RegisterExtension
	public final static DockerComposeExtension docker = DockerComposeExtension.builder()
			.file("docker/docker-compose.yml")
			.waitingForService("zephyr_nginx", HealthChecks.toHaveAllPortsOpen())
			.waitingForService("zephyr_tomcat_1", HealthChecks.toHaveAllPortsOpen())
			.waitingForService("zephyr_tomcat_2", HealthChecks.toHaveAllPortsOpen())
			.waitingForService("zephyr_tomcat_3", HealthChecks.toHaveAllPortsOpen())
			.waitingForService("zephyr_tomcat_4", HealthChecks.toHaveAllPortsOpen())
			.waitingForService("zephyr_tomcat_5", HealthChecks.toHaveAllPortsOpen())
			.waitingForService("zephyr_db", HealthChecks.toHaveAllPortsOpen())
			.build();

	@Test
	public void testDemoRichlet() {
		WebDriver webDriver = initWebDriver();
		String url = "http://" + getHost() + "/shoppingCart";
		int statusCode = getStatusCode(url);
		assertEquals(200, statusCode);
		webDriver.get(url);

		// wait for page ready.
		waitResponse();

		assertEquals("Shopping bag", jq(".shoppingBag .title").text());

		click(jq(".add-items"));
		waitResponse();

		assertEquals(2, jq(".z-row").length());

		assertTrue(getZKLog().contains("add item"));

		click(jq(".z-combobox-icon:eq(2)").toWidget().$n("btn"));
		waitResponse(true);

		click(jq(".z-combobox-popup.z-combobox-open .z-comboitem-text:contains(Hamburger)"));
		waitResponse();
		assertTrue(getZKLog().contains("change item"));

		assertEquals("200", jq("@row:eq(1) @label:eq(1)").text());


		click(jq(".add-items"));
		waitResponse();
		click(jq(".add-items"));
		waitResponse();
		click(jq(".add-items"));
		waitResponse();
		click(jq(".add-items"));
		waitResponse();

		click(jq(".z-combobox-icon:eq(10)"));
		waitResponse(true);

		click(jq(".z-combobox-popup.z-combobox-open .z-comboitem-text:contains(Hamburger)"));
		waitResponse();
		assertEquals(getZKLog().split("change").length, 3);

		click(jq(".submit"));
		waitResponse();

		assertEquals(0, jq(".z-grid:eq(0) .z-row").length());
		assertEquals(6, jq(".z-grid:eq(1) .z-row").length());

		assertNoAnyError();
	}

	@Test
	public void testNoDuplicatedUuid() {
		WebDriver webDriver = initWebDriver();
		String url = "http://" + getHost() + "/shoppingCart";
		webDriver.get(url);

		// wait for page ready.
		waitResponse();

		assertEquals("Shopping bag", jq(".shoppingBag .title").text());

		for (int i = 2; i < 10; i++) {
			click(jq(".add-items"));
			waitResponse();

			assertEquals(i, jq(".z-row").length());

			assertTrue(getZKLog().contains("add item"));

			click(jq(".z-combobox-icon:eq(" + (2 * (i - 2 + 1)) + ")").toWidget().$n("btn"));
			waitResponse(true);

			// to hide the popup
			click(jq(".shoppingBag .title"));
			waitResponse();
		}
		assertNoAnyError();
	}
}
