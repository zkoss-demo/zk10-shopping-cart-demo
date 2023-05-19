/* DemoRichlet.java

	Purpose:

	Description:

	History:
		Wed Mar 09 15:38:04 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.demo.richlet;

import org.zkoss.stateless.action.ActionTarget;
import org.zkoss.stateless.action.data.InputData;
import org.zkoss.stateless.annotation.*;
import org.zkoss.stateless.demo.pojo.Item;
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

	@RichletMapping("")
	public List<IComponent> index() {
		return asList(
			IStyle.ofSrc(DEMO_CSS),
			IVlayout.of(
				initShoppingBag(),
				Boilerplate.ORDER_TEMPLATE
			)
		);
	}

	private IVlayout initShoppingBag() {
		final String orderId = Helper.nextUuid();
		return IVlayout.of(
			ILabel.of("Shopping Cart").withSclass("title"),
			renderOrderButtons(orderId),
			IGrid.ofId("shoppingBag").withHflex("1")
				.withEmptyMessage("please add items.")
				.withColumns(Boilerplate.SHOPPING_BAG_COLUMN_TEMPLATE)
				.withRows(intShoppingBagItems(orderId))
				)
		.withSclass("shoppingBag");
	}

	private IRows intShoppingBagItems(String orderId) {
		return IRows.ofId("shoppingBagRows").withChildren(initShoppingBagItem(orderId));
	}

	private IRow initShoppingBagItem(String orderId) {
		String uuid = orderService.insertItem(orderId);
		int initQuantity = 1;
		int initPrice = Item.DEFAULT_PRODUCT.getPrice();
		String id = combine(orderId, uuid);
		log("add item "+ id);
		return IRow.of(
			initProductList(),
			ISpinner.of(initQuantity).withInstant(true)
				.withAction(this::doQuantityChange),
			ILabel.of(String.valueOf(initPrice)),
			ILabel.of(String.valueOf(initPrice)),
			IButton.of("delete").withAction(this::doDelete)
		).withId(id);
	}

	private IDiv renderOrderButtons(String orderId) {
		return IDiv.of(
					IButton.of("add item +").withAction(this::addItem)
							.withSclass("add-items")
							.withId(combine(orderId, "add")),
					IButton.of("submit order").withAction(this::doSubmit)
							.withSclass("submit")
							.withId(combine(orderId, "submit")))
				.withId("button-area");
	}

	private ICombobox initProductList() {
		String initProductName = Item.DEFAULT_PRODUCT.getName();
		return ICombobox.of(initProductName)
			.withReadonly(true)
			.withAction(this::doItemChange)
			.withChildren(Boilerplate.PRODUCT_LIST_TEMPLATE);
	}

	private ICombobox initProductSize() {
		String initProductSize = "S";
		return ICombobox.of(initProductSize)
			.withReadonly(true)
			.withAction(this::doSizeChange)
			.withChildren(Boilerplate.PRODUCT_SIZE_TEMPLATE);
	}

	@Action(type = Events.ON_CLICK)
	public void addItem(@ActionVariable(targetId = ActionTarget.SELF, field = "id") String uuid) {
		UiAgent.getCurrent().appendChild(Locator.ofId("shoppingBagRows"),
				initShoppingBagItem(parseOrderId(uuid)));
	}

	@Action(type = Events.ON_CLICK)
	public void doDelete(Self self, @ActionVariable(targetId = ActionTarget.PARENT, field = "id") String uuid) {
		orderService.delete(parseItemId(uuid));
		UiAgent.getCurrent().remove(Locator.ofId(uuid));
		log("delete item " + uuid);
	}

	@Action(type = Events.ON_CLICK)
	public void doSubmit(@ActionVariable(targetId = ActionTarget.SELF, field = "id") String uuid) {
		final String orderId = parseOrderId(uuid);
		orderService.submit(orderId);
		UiAgent.getCurrent()
				// empty the shopping bag rows
				.replaceChildren(Locator.ofId("shoppingBagRows"))
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

	@Action(type = Events.ON_CHANGE)
	public void doItemChange(Self self, InputData data,
							 @ActionVariable(targetId = ActionTarget.PARENT, field = "id") String uuid,
							 @ActionVariable(targetId = ActionTarget.NEXT_SIBLING) Integer quantity) {
		String productName = data.getValue();
		int price = Item.PRODUCT_TABLE.get(productName).getPrice();
		orderService.updateProduct(parseItemId(uuid), productName, quantity * price);
		String subTotal = String.valueOf(quantity * price);
		UiAgent.getCurrent()
				.smartUpdate(Helper.getPriceLocator(self), new ILabel.Updater().value(String.valueOf(price)))
				.smartUpdate(Helper.getTotalLocator(self), new ILabel.Updater().value(subTotal));
		log("change item: " + productName);
	}

	@Action(type = Events.ON_CHANGE)
	public void doQuantityChange(Self self,
			InputData quantityData, @ActionVariable(targetId = ActionTarget.NEXT_SIBLING) Integer price,
			@ActionVariable(targetId = ActionTarget.PARENT, field = "id") String uuid) {
		Integer quantity = Integer.valueOf(quantityData.getValue());
		orderService.updateQuantity(parseItemId(uuid), quantity, price);
		UiAgent.getCurrent().smartUpdate(
				getTotalLocatorFromQuantity(self),
			new ILabel.Updater().value(String.valueOf((price * quantity))));
		log("change quantity " + quantity);
	}

	@Action(type = Events.ON_CHANGE)
	public void doSizeChange(InputData sizeData, @ActionVariable(targetId = ActionTarget.PARENT, field = "id") String uuid) {
		orderService.updateSize(parseItemId(uuid), sizeData.getValue());
		log("change size");
	}
}