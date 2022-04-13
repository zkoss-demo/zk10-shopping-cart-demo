/* DemoRichlet.java

	Purpose:

	Description:

	History:
		Wed Mar 09 15:38:04 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.demo.richlet;

import static java.util.Arrays.asList;
import static org.zkoss.zephyr.action.ActionTarget.NEXT_SIBLING;
import static org.zkoss.zephyr.action.ActionTarget.SELF;
import static org.zkoss.zephyr.demo.util.Helper.log;
import static org.zkoss.zephyr.demo.util.Helper.nextUuid;
import static org.zkoss.zephyr.demo.util.Helper.parseUuid;
import static org.zkoss.zephyr.demo.util.Helper.uuid;

import java.util.List;
import java.util.stream.Collectors;

import org.zkoss.zephyr.action.data.InputData;
import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.ActionVariable;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.demo.db.factory.DaoFactory;
import org.zkoss.zephyr.demo.db.service.OrderService;
import org.zkoss.zephyr.demo.pojo.Item;
import org.zkoss.zephyr.demo.util.Helper;
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
import org.zkoss.zk.ui.event.Events;

@RichletMapping("/shoppingCart")
public class DemoRichlet implements StatelessRichlet {

	private static final OrderService orderService = DaoFactory.INSTANCE.getService();
	private static final String DEMO_CSS = "/css/shoppingCart.css";

	@RichletMapping("")
	public List<IComponent> demo() {
		return asList(
			IStyle.ofSrc(DEMO_CSS),
			IVlayout.of(
				initShoppingBag(),
				initOrder()
			)
		);
	}

	private IVlayout initShoppingBag() {
		final String orderId = Helper.nextUuid();
		return IVlayout.of(
			ILabel.of("Shopping bag").withSclass("title"),
			IGrid.ofId("shoppingBag").withHflex("1")
				.withEmptyMessage("please add items.")
				.withColumns(initShoppingBagColumn())
				.withRows(initShoppingBagColumn(orderId)),
			IDiv.of(IButton.of("add item +").withAction(this::addItem)
				.withSclass("add-items")
				.withId(uuid(orderId, "add")),
			IButton.of("submit order").withAction(this::doSubmit)
				.withSclass("submit")
				.withId(uuid(orderId, "submit"))))
		.withSclass("shoppingBag");
	}

	private IAnyGroup initOrder() {
		return IVlayout.of(
			ILabel.of("Your Order").withSclass("title"),
			IGrid.DEFAULT.withRows(IRows.ofId("orderRows"))
				.withFoot(IFoot.ofId("summary"))
				.withHflex("1").withEmptyMessage("no order.").withSclass("order"))
		.withSclass("order-layout");
	}

	private void updateOrder(String orderId) {
		List<Item> items = orderService.selectOrder(orderId);
		List<IRow> orderRow = items.stream().map(this::initOrderRow).collect(Collectors.toList());
		IFooter orderSummary = initSummary(orderService.count(orderId), orderService.sum(orderId));
		UiAgent.getCurrent()
			.replaceChildren(Locator.ofId("orderRows"), orderRow)
			.replaceChildren(Locator.ofId("summary"), orderSummary);
	}

	private void updateProductState(Self self, String uuid, String productName, int quantity) {
		int price = Item.PRODUCT_TABLE.get(productName).getPrice();
		orderService.updateProduct(uuid, productName, quantity * price);
		String subTotal = String.valueOf(quantity * price);
		UiAgent.getCurrent()
			.smartUpdate(Helper.getPriceLocator(self), new ILabel.Updater().value(String.valueOf(price)))
			.smartUpdate(Helper.getTotalLocator(self), new ILabel.Updater().value(subTotal));
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
		int initPrice = Item.DEFAULT_PRODUCT.getPrice();
		return IRow.of(
			initProductList(uuid),
			initProductSize(uuid),
			ISpinner.ofId(uuid(uuid, "quantity"))
				.withValue(initQuantity).withInstant(true)
				.withAction(this::doQuantityChange),
			ILabel.ofId(uuid(uuid, "price")).withValue(String.valueOf(initPrice)),
			ILabel.ofId(uuid(uuid, "subTotal")).withValue(String.valueOf(initQuantity * initPrice)),
			IButton.ofId(uuid(uuid, "delete")).withLabel("delete").withAction(this::doDelete)
		);
	}

	private ICombobox initProductList(String uuid) {
		String initProductName = Item.DEFAULT_PRODUCT.getName();
		return ICombobox.ofId(uuid(uuid, "productName"))
			.withValue(initProductName)
			.withReadonly(true)
			.withAction(this::doItemChange)
			.withChildren(
				Item.PRODUCT_TABLE.values().stream()
					.map((product -> IComboitem.of(
							product.getName(), product.getIcon())))
					.collect(Collectors.toList())
			);
	}

	private ICombobox initProductSize(String uuid) {
		String initProductSize = "S";
		return ICombobox.ofId(uuid(uuid, "size")).withValue(initProductSize)
			.withReadonly(true)
			.withAction(this::doSizeChange)
			.withChildren(
				IComboitem.of("S"),
				IComboitem.of("M"),
				IComboitem.of("L")
			);
	}

	private IRow initOrderRow(Item item) {
		String productName = item.getProductName();
		String src = Item.PRODUCT_TABLE.get(productName).getIcon();
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


	@Action(type = Events.ON_CLICK)
	public void addItem(@ActionVariable(id = SELF, field = "id") String orderId) {
		UiAgent.getCurrent().appendChild(Locator.ofId("shoppingBagRows"),
				initShoppingBagItem(parseUuid(orderId)));
		log("add item");
	}

	@Action(type = Events.ON_CLICK)
	public void doDelete(Self self, @ActionVariable(id = SELF, field = "id") String uuid) {
		orderService.delete(parseUuid(uuid));
		UiAgent.getCurrent().remove(self.closest(IRow.class));
		log("delete item");
	}

	@Action(type = Events.ON_CLICK)
	public void doSubmit(@ActionVariable(id = SELF, field = "id") String orderId) {
		orderService.submit();
		UiAgent.getCurrent().replaceChildren(Locator.ofId("shoppingBag").closest(IRows.class));
		resetShoppingBag(parseUuid(orderId));
		updateOrder(parseUuid(orderId));
		log("submit order");
	}

	private void resetShoppingBag(String orderId) {
		String newOrderId = nextUuid();
		UiAgent.getCurrent()
			.replaceChildren(Locator.ofId("shoppingBagRows"))
			.smartUpdate(Locator.ofId(uuid(orderId, "add")),
					new IButton.Updater().id(uuid(newOrderId, "add")))
			.smartUpdate(Locator.ofId(uuid(orderId, "submit")),
					new IButton.Updater().id(uuid(newOrderId, "submit")));
	}

	@Action(type = Events.ON_CHANGE)
	public void doItemChange(InputData data, Self self, @ActionVariable(id = SELF, field = "id") String uuid,
			@ActionVariable(id = NEXT_SIBLING + NEXT_SIBLING) int quantity) {
		String productName = data.getValue();
		updateProductState(self, parseUuid(uuid), productName, quantity);
		log("change item");
	}

	@Action(type = Events.ON_CHANGE)
	public void doQuantityChange(InputData data, @ActionVariable(id = NEXT_SIBLING) Integer price,
			@ActionVariable(id = SELF, field = "id") String uuid) {
		Integer quantity = Integer.valueOf(data.getValue());
		String itemId = parseUuid(uuid);
		orderService.updateQuantity(itemId, quantity, price);
		if (quantity != null && price != null) {
			String subTotal = String.valueOf((price * quantity));
			UiAgent.getCurrent().smartUpdate(Locator.ofId(
				uuid(itemId, "subTotal")),
				new ILabel.Updater().value(subTotal));
			log("change quantity");
		}
	}

	@Action(type = Events.ON_CHANGE)
	public void doSizeChange(InputData data, @ActionVariable(id = SELF, field = "id") String uuid) {
		String size = data.getValue();
		orderService.updateSize(parseUuid(uuid), size);
		log("change size");
	}
}