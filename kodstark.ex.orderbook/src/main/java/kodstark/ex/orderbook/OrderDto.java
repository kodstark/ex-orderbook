/**
 * Copyright (C) 2013 Kamil Demecki <kodstark@gmail.com>
 *
 * Licensed under the terms of any of the following licenses at your
 * choice:
 *
 *  - GNU Lesser General Public License Version 2.1 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl.html
 *
 *  - Mozilla Public License Version 1.1 or later (the "MPL")
 *    http://www.mozilla.org/MPL/MPL-1.1.html
 */
package kodstark.ex.orderbook;

/**
 * Order data.
 */
public class OrderDto
{
	private final long orderId;
	private final String shortName;
	private final boolean isBuy;
	private final int quantity;
	private final int price;

	public OrderDto(long orderId, String shortName, boolean isBuy, int price,
	        int quantity)
	{
		this.orderId = orderId;
		this.shortName = shortName;
		this.isBuy = isBuy;
		this.price = price;
		this.quantity = quantity;
	}

	public long getOrderId()
	{
		return orderId;
	}

	public String getShortName()
	{
		return shortName;
	}

	public boolean isBuy()
	{
		return isBuy;
	}

	public int getPrice()
	{
		return price;
	}

	public int getQuantity()
	{
		return quantity;
	}
	
	@Override
	public String toString()
	{
	    StringBuilder result = new StringBuilder();
	    result.append(orderId);
	    result.append("=");
	    result.append(price);
	    result.append("/");
	    result.append(quantity);
	    result.append(" [");
	    result.append(shortName);
	    result.append(",");
	    result.append(isBuy ? "buy" : "sell");
	    result.append("]");
	    return result.toString();
	}
}
