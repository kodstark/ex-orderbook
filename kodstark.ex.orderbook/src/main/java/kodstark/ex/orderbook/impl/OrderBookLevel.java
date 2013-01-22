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
package kodstark.ex.orderbook.impl;

import kodstark.ex.orderbook.OrderDto;

/**
 * Order level for one price.
 * 
 * @author kodstark
 */
class OrderBookLevel
{
    private int quantity;
    private short ordersCount;

    public OrderBookLevel(OrderDto order)
    {
        int tmpQuantity = order.getQuantity();
        if (tmpQuantity < 0)
        {
            throw new InvalidOrderException();
        }
        quantity = tmpQuantity;
        ordersCount = (short) 1;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public short getOrdersCount()
    {
        return ordersCount;
    }

    void addOrder(OrderDto order)
    {
        int tmpQuantity = quantity + order.getQuantity();
        if (tmpQuantity <= 0)
        {
            throw new InvalidOrderException();
        }
        quantity = tmpQuantity;
        ++ordersCount;
    }

    void removeOrder(OrderDto order)
    {
        int tmpQuantity = quantity - order.getQuantity();
        if (tmpQuantity <= 0)
        {
            throw new InvalidOrderException();
        }
        quantity = tmpQuantity;
        --ordersCount;
    }

    void updateFromNewOrder(OrderDto order, OrderDto newOrder)
    {
        int tmpQuantity = quantity - order.getQuantity() + newOrder.getQuantity();
        if (tmpQuantity <= 0)
        {
            throw new InvalidOrderException();
        }
        quantity = tmpQuantity;
    }

    @Override
    public String toString()
    {
        return quantity + "/" + ordersCount;
    }
}
