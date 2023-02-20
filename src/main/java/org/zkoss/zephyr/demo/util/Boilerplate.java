/* Boilerplate.java

	Purpose:

	Description:

	History:
		12:16 PM 2022/4/13, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.demo.util;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.zkoss.zephyr.demo.pojo.Item;
import org.zkoss.zephyr.zpr.IColumn;
import org.zkoss.zephyr.zpr.IColumns;
import org.zkoss.zephyr.zpr.IComboitem;
import org.zkoss.zephyr.zpr.IFoot;
import org.zkoss.zephyr.zpr.IFooter;
import org.zkoss.zephyr.zpr.IGrid;
import org.zkoss.zephyr.zpr.IImage;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zephyr.zpr.IRow;
import org.zkoss.zephyr.zpr.IRows;
import org.zkoss.zephyr.zpr.IVlayout;

/**
 * Boilerplate for shopping cart.
 * @author jumperchen
 */
public class Boilerplate {
	public static final IVlayout ORDER_TEMPLATE = IVlayout.of(
					ILabel.of("Your Order").withSclass("title"),
					IGrid.DEFAULT.withRows(IRows.ofId("orderRows"))
							.withFoot(IFoot.ofId("summary")).withHflex("1")
							.withEmptyMessage("no order.").withSclass("order"))
			.withSclass("order-layout");

	public static final IColumns SHOPPING_BAG_COLUMN_TEMPLATE = IColumns.of(
			IColumn.of("ITEMS"), IColumn.of("SIZE"), IColumn.of("QUANTITY"),
			IColumn.of("PRICE"), IColumn.of("TOTAL"), IColumn.DEFAULT);

	public static final Iterable<IComboitem> PRODUCT_LIST_TEMPLATE = Item.PRODUCT_TABLE.values()
			.stream().map((product -> IComboitem.of(product.getName(),
					product.getIcon()))).collect(Collectors.toList());

	public static final Iterable<IComboitem> PRODUCT_SIZE_TEMPLATE = Arrays.asList(
			IComboitem.of("S"), IComboitem.of("M"), IComboitem.of("L"));

	public static IFooter summaryTemplate(int count, int sum) {
		return IFooter.of(
				ILabel.of("My bag: " + count + " items, "),
				ILabel.of(" Total price: $" + sum)
		);
	}

	public static IRow orderItemTemplate(Item item) {
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
}
