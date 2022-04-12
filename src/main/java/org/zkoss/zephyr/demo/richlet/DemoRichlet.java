/* DemoRichlet.java

	Purpose:

	Description:

	History:
		Wed Mar 09 15:38:04 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.demo.richlet;

import static org.zkoss.zephyr.action.ActionTarget.NEXT_SIBLING;
import static org.zkoss.zephyr.action.ActionTarget.SELF;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.zephyr.action.data.RequestData;
import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.ActionVariable;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.demo.db.factory.DaoFactory;
import org.zkoss.zephyr.demo.db.service.OrderService;
import org.zkoss.zephyr.demo.pojo.Item;
import org.zkoss.zephyr.demo.util.CartLocator;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.Self;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IAnyGroup;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IColumn;
import org.zkoss.zephyr.zpr.IColumns;
import org.zkoss.zephyr.zpr.ICombobox;
import org.zkoss.zephyr.zpr.IComboitem;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IDiv;
import org.zkoss.zephyr.zpr.IFoot;
import org.zkoss.zephyr.zpr.IFooter;
import org.zkoss.zephyr.zpr.IGrid;
import org.zkoss.zephyr.zpr.IImage;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zephyr.zpr.IRow;
import org.zkoss.zephyr.zpr.IRows;
import org.zkoss.zephyr.zpr.ISpinner;
import org.zkoss.zephyr.zpr.IStyle;
import org.zkoss.zephyr.zpr.IVlayout;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;

@RichletMapping("/shoppingCart")
public class DemoRichlet implements StatelessRichlet {

	private static final OrderService orderService = DaoFactory.getInstance();

	@RichletMapping("")
	public List<IComponent> demo() {
		return Arrays.asList(
				IStyle.ofSrc("/css/shoppingCart.css"),
				IVlayout.of(
						initShoppingBag(),
						initOrder()
				)
		);
	}

	private IAnyGroup initShoppingBag() {
		String orderId = UUID.randomUUID().toString().substring(0, 6);
		return IVlayout.of(
				ILabel.of("Shopping bag").withSclass("title"),
				IGrid.ofId("shoppingBag").withHflex("1").withEmptyMessage("please add items.")
						.withColumns(initShoppingBagColumn())
						.withRows(initShoppingBagColumn(orderId)),
				IDiv.of(
					IButton.of("add item +").withAction(this::addItem)
							.withSclass("add-items").withId(generateUuid(orderId, "add")),
					IButton.of("submit order").withAction(this::doSubmit)
							.withSclass("submit").withId(generateUuid(orderId, "submit"))
				)
		).withSclass("shoppingBag");
	}

	private IAnyGroup initOrder() {
		return IVlayout.of(
				ILabel.of("Your Order").withSclass("title"),
				IGrid.DEFAULT.withRows(IRows.ofId("orderRows"))
						.withFoot(IFoot.ofId("summary"))
						.withHflex("1").withEmptyMessage("no order.").withSclass("order"))
				.withSclass("order-layout");
	}

	@Action(type = Events.ON_CLICK)
	public void addItem(@ActionVariable(id = SELF, field = "id") String orderId) {
		UiAgent.getCurrent().appendChild(Locator.ofId("shoppingBagRows"), initShoppingBagItem(parseUuid(orderId)));
		serverLog("add item");
	}

	@Action(type = Events.ON_CLICK)
	public void doDelete(Self self, @ActionVariable(id = SELF, field = "id") String uuid) {
		orderService.delete(parseUuid(uuid));
		UiAgent.getCurrent().remove(self.closest(IRow.class));
		serverLog("delete item");
	}

	@Action(type = Events.ON_CLICK)
	public void doSubmit(@ActionVariable(id = SELF, field = "id") String orderId) {
		orderService.submit();
		UiAgent.getCurrent().replaceChildren(Locator.ofId("shoppingBag").closest(IRows.class));
		resetShoppingBag(parseUuid(orderId));
		updateOrder(parseUuid(orderId));
		serverLog("submit order");
	}

	private void resetShoppingBag(String orderId) {
		String newOrderId = UUID.randomUUID().toString().substring(0, 6);
		UiAgent.getCurrent().replaceChildren(Locator.ofId("shoppingBagRows"))
				.smartUpdate(Locator.ofId(generateUuid(orderId, "add")),
						new IButton.Updater().id(generateUuid(newOrderId, "add")))
				.smartUpdate(Locator.ofId(generateUuid(orderId, "submit")),
						new IButton.Updater().id(generateUuid(newOrderId, "submit")));
	}

	private void updateOrder(String orderId) {
		List<Item> items = orderService.selectOrder(orderId);
		List<IRow> orderRow = new LinkedList<>();
		if (!items.isEmpty()) {
			for (Item o : items)
				orderRow.add(initOrderRow(o));
		} else {
			orderRow = Collections.emptyList();
		}
		IFooter orderSummary = initSummary(orderService.count(orderId), orderService.sum(orderId));
		UiAgent.getCurrent().replaceChildren(Locator.ofId("orderRows"), orderRow);
		UiAgent.getCurrent().replaceChildren(Locator.ofId("summary"), orderSummary);
	}

	@Action(type = Events.ON_CHANGE)
	public void doItemChange(RequestData requestData, Self self, @ActionVariable(id = SELF, field = "id") String uuid,
								@ActionVariable(id = NEXT_SIBLING + NEXT_SIBLING) int quantity) {
		String productName = (String) requestData.getData().get("value");
		updateProductState(self, parseUuid(uuid), productName, quantity);
		serverLog("change item");
	}

	@Action(type = Events.ON_CHANGE)
	public void doQuantityChange(RequestData requestData, @ActionVariable(id = NEXT_SIBLING) Integer price,
								@ActionVariable(id = SELF, field = "id") String uuid) {
		Integer quantity = (Integer) requestData.getData().get("value");
		String itemId = parseUuid(uuid);
		orderService.updateQuantity(itemId, quantity, price);
		if (quantity != null && price != null) {
			String subTotal = String.valueOf((price * quantity));
			UiAgent.getCurrent().smartUpdate(Locator.ofId(generateUuid(itemId, "subTotal")),
					new ILabel.Updater().value(subTotal));
		}
		serverLog("change quantity");
	}

	@Action(type = Events.ON_CHANGE)
	public void doSizeChange(RequestData requestData, @ActionVariable(id = SELF, field = "id") String uuid) {
		String size = (String) requestData.getData().get("value");
		orderService.updateSize(parseUuid(uuid), size);
		serverLog("change size");
	}

	private void updateProductState(Self self, String uuid, String productName, int quantity) {
		int price = Item.priceTable.get(productName);
		orderService.updateProduct(uuid, productName, quantity * price);
		String subTotal = String.valueOf(quantity * price);
		UiAgent.getCurrent().smartUpdate(CartLocator.getPriceLocator(self), new ILabel.Updater().value(String.valueOf(price)))
				.smartUpdate(CartLocator.getTotalLocator(self), new ILabel.Updater().value(subTotal));
	}

	private IColumns initShoppingBagColumn() {
		return IColumns.of(
				IColumn.of("ITEMS"),
				IColumn.of("SIZE"),
				IColumn.of("QUANTITY"),
				IColumn.of("PRICE"),
				IColumn.of("TOTAL"),
				IColumn.DEFAULT);
	}

	private IRows initShoppingBagColumn(String orderId) {
		return IRows.ofId("shoppingBagRows").withChildren(initShoppingBagItem(orderId));
	}

	private IRow initShoppingBagItem(String orderId) {
		String uuid = orderService.insertItem(orderId);
		int initQuantity = 1;
		int initPrice = Item.priceTable.get("Cake");
		return IRow.of(
				initProductList(uuid),
				initProductSize(uuid),
				ISpinner.ofId(generateUuid(uuid, "quantity")).withValue(initQuantity).withInstant(true)
						.withAction(this::doQuantityChange),
				ILabel.ofId(generateUuid(uuid, "price")).withValue(String.valueOf(initPrice)),
				ILabel.ofId(generateUuid(uuid, "subTotal")).withValue(String.valueOf(initQuantity * initPrice)),
				IButton.ofId(generateUuid(uuid, "delete")).withLabel("delete").withAction(this::doDelete)
		);
	}

	private ICombobox initProductList(String uuid) {
		String initProductName = "Cake";
		return ICombobox.ofId(generateUuid(uuid, "productName")).withChildren(
					IComboitem.of("Cake").withImage("/image/cake.svg"),
					IComboitem.of("Hamburger").withImage("/image/hamburger.svg"),
					IComboitem.of("Pizza").withImage("/image/pizza.svg")
				).withValue(initProductName)
				.withReadonly(true)
				.withAction(this::doItemChange);
	}

	private ICombobox initProductSize(String uuid) {
		String initProductSize = "S";
		return ICombobox.ofId(generateUuid(uuid, "size")).withChildren(
					IComboitem.of("S"),
					IComboitem.of("M"),
					IComboitem.of("L")
				).withValue(initProductSize)
				.withReadonly(true)
				.withAction(this::doSizeChange);
	}

	private IRow initOrderRow(Item item) {
		String productName = item.getProductName();
		String src = "/image/" + productName.toLowerCase() + ".svg";
		return IRow.of(
				IVlayout.of(
					IImage.ofSize("70px", "70px").withSrc(src),
					ILabel.of(productName)
				).withSclass("item-image"),
				IVlayout.of(
					ILabel.of("Size: " + item.getSize()),
					ILabel.of("Quantity: " + item.getQuantity()),
					ILabel.of("$ " + item.getPrice())
				).withSclass("item-detail"),
				ILabel.of("Sub Total: $ " + item.getSubTotal()).withSclass("subTotal")
		);
	}

	private IFooter initSummary(int count, int sum) {
		return IFooter.of(
				ILabel.of("My bag: " + count + " items, "),
				ILabel.of(" Total price: $" + sum)
		);
	}

	private String generateUuid(Object uuid, String name) {
		return String.join("_", String.valueOf(uuid), name);
	}

	private String parseUuid(String uuid) {
		return uuid.split("_")[0];
	}

	private void serverLog(String act) {
		Clients.log(String.join(": ", String.valueOf(Executions.getCurrent().getLocalAddr()), act));
	}
}