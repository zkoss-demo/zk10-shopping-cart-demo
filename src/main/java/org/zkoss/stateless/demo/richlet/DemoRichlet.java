/* DemoRichlet.java

	Purpose:

	Description:

	History:
		Wed Mar 09 15:38:04 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.demo.richlet;

import org.zkoss.stateless.action.*;
import org.zkoss.stateless.action.data.InputData;
import org.zkoss.stateless.annotation.*;
import org.zkoss.stateless.demo.pojo.*;
import org.zkoss.stateless.demo.util.*;
import org.zkoss.stateless.sul.*;
import org.zkoss.stateless.ui.*;
import org.zkoss.stateless.demo.db.factory.ServiceFactory;
import org.zkoss.stateless.demo.db.service.OrderService;
import org.zkoss.zk.ui.event.Events;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.zkoss.stateless.demo.util.Helper.*;

@RichletMapping("/shoppingCart")
public class DemoRichlet implements StatelessRichlet {

	private static final OrderService orderService = ServiceFactory.INSTANCE.getService();
	private static final String DEMO_CSS = "/css/shoppingCart.css";
	public static final String SHOPPING_CART = "shoppingCart";
	public static final String SHOPPING_CART_ROWS = "shoppingCartRows";

	/**
	 * render the main page
	 */
	@RichletMapping("") // append the path on this method after the path specified on the class
	public List<IComponent> index() {
		return asList(
			IStyle.ofSrc(DEMO_CSS),
			IStyle.ofSrc("https://fonts.googleapis.com/css?family=Roboto:300,400,500,600,700"),
			IScript.ofSrc("/js/load-favicon.js"),
			IVlayout.of(
				renderShoppingCart(),
				Boilerplate.ORDER_TEMPLATE
			)
		);
	}

	private IVlayout renderShoppingCart() {
		final String orderId = Helper.nextUuid();
		return IVlayout.of(
			ILabel.of("ZK Stateless Components Demo - Shopping Cart").withSclass("title"),
			renderDescription(),
			IGrid.ofId(SHOPPING_CART).withHflex("1")
				.withEmptyMessage("please add items.")
				.withColumns(Boilerplate.SHOPPING_CART_COLUMN_TEMPLATE)
				.withRows(renderShoppingCartItems(orderId)),
			renderOrderButtons(orderId))
		.withSclass(SHOPPING_CART);
	}

	private IDiv renderDescription() {
		return IDiv.of(IHtml.of("Stateless Components is a new feature introduced in ZK 10 enabling developers to build cloud-native apps. For more information, please read <a href=\"https://www.zkoss.org/wiki/ZK%20Developer's%20Reference/Stateless%20Components\" target=\"_blank\">ZK Developer's Reference > Stateless Components</a> for details"));
	}

	private IRows renderShoppingCartItems(String orderId) {
		return IRows.ofId(SHOPPING_CART_ROWS).withChildren(renderShoppingCartOneItem(orderId));
	}

	private IRow renderShoppingCartOneItem(String orderId) {
		String itemId = orderService.insertItem(orderId);
		int initQuantity = 1;
		int initPrice = Product.DEFAULT_PRODUCT.getPrice();
		String rowId = combine(orderId, itemId);
		log("add item "+ rowId);
		return IRow.of(
			renderProductDropdownList(),
			renderProductSize(),
			ISpinner.of(initQuantity).withInstant(true)
				.withAction(ActionType.onChange(this::doQuantityChange)),
			ILabel.of(String.valueOf(initPrice)),
			ILabel.of(String.valueOf(initPrice)),
			IButton.of("delete").withAction(ActionType.onClick(this::doDelete))
		).withId(rowId);
	}

	private IDiv renderOrderButtons(String orderId) {
		return IDiv.of(
					IButton.of("add item +")
							.withSclass("add-items")
							.withAction(ActionType.onClick(this::addItem)) // register an action handler
							.withId(combine(orderId, "add")),
					IButton.of("submit order")
							.withAction(ActionType.onClick(this::doSubmit))
							.withSclass("submit")
							.withId(combine(orderId, "submit")))
				.withId("button-area");
	}

	private ICombobox renderProductDropdownList() {
		String initProductName = Product.DEFAULT_PRODUCT.getName();
		return ICombobox.of(initProductName)
			.withReadonly(true)
			.withAction(ActionType.onChange(this::doItemChange))
			.withChildren(Boilerplate.PRODUCT_LIST_TEMPLATE);
	}

	private ICombobox renderProductSize() {
		String initProductSize = "S";
		return ICombobox.of(initProductSize)
			.withReadonly(true)
			.withAction(ActionType.onChange(this::doSizeChange))
			.withChildren(Boilerplate.PRODUCT_SIZE_TEMPLATE);
	}

	public void addItem(@ActionVariable(targetId = ActionTarget.SELF, field = "id") String id) {
		UiAgent.getCurrent().appendChild(Locator.ofId(SHOPPING_CART_ROWS),
				renderShoppingCartOneItem(parseOrderId(id)));
	}

	public void doDelete(@ActionVariable(targetId = ActionTarget.PARENT, field = "id") String id) {
		orderService.delete(parseItemId(id));
		UiAgent.getCurrent().remove(Locator.ofId(id));
		log("delete item " + id);
	}

	@Action(type = Events.ON_CLICK)
	public void doSubmit(@ActionVariable(targetId = ActionTarget.SELF, field = "id") String id) {
		final String orderId = parseOrderId(id);
		orderService.submit(orderId);
		UiAgent.getCurrent()
				// empty the shopping cart rows
				.replaceChildren(Locator.ofId(SHOPPING_CART_ROWS))
				// render the order table content
				.replaceChildren(Locator.ofId("orderRows"),
					orderService.selectOrder(orderId).stream()
							.map(Boilerplate::orderItemTemplate)
							.collect(Collectors.toList()))
				// update the summary content
				.replaceChildren(Locator.ofId("summary"),
						Boilerplate.summaryTemplate(orderService.count(orderId), orderService.sum(orderId)))
				// reset the order buttons with a new orderId
				.replaceWith(Locator.ofId("button-area"),
						renderOrderButtons(nextUuid()));
		log("submit order " + orderId);
	}

	public void doItemChange(InputData data,
							 Self self,
							 @ActionVariable(targetId = ActionTarget.PARENT, field = "id") String id,
							 @ActionVariable(targetId = ActionTarget.NEXT_SIBLING + ActionTarget.NEXT_SIBLING) Integer quantity) {
		String productName = data.getValue();
		int price = Product.PRODUCT_TABLE.get(productName).getPrice();
		orderService.updateProduct(parseItemId(id), productName, quantity * price);
		String subTotal = String.valueOf(quantity * price);
		UiAgent.getCurrent()
				.smartUpdate(Helper.getPriceLocator(self), new ILabel.Updater().value(String.valueOf(price)))
				.smartUpdate(Helper.getTotalLocator(self), new ILabel.Updater().value(subTotal));
		log("change item");
	}

	public void doQuantityChange(Self self,
								 InputData data,
								 @ActionVariable(targetId = ActionTarget.NEXT_SIBLING) Integer price,
								 @ActionVariable(targetId = ActionTarget.PARENT, field = "id") String id) {
		Integer quantity = Integer.valueOf(data.getValue());
		orderService.updateQuantity(parseItemId(id), quantity, price);
		UiAgent.getCurrent().smartUpdate(
				getTotalLocatorFromQuantity(self),
			new ILabel.Updater().value(String.valueOf((price * quantity))));
		log("change quantity");
	}

	public void doSizeChange(InputData data,
							 @ActionVariable(targetId = ActionTarget.PARENT, field = "id") String id) {
		orderService.updateSize(parseItemId(id), data.getValue());
		log("change size");
	}
}