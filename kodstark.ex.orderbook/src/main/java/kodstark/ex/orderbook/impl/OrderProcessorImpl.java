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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import kodstark.ex.orderbook.OrderAction;
import kodstark.ex.orderbook.OrderLogger;
import kodstark.ex.orderbook.OrderDto;
import kodstark.ex.orderbook.OrderProcessor;

/**
 * Process order into order book by price level.
 * 
 * @author kodstark
 */
public class OrderProcessorImpl implements OrderProcessor
{
    private OrderLogger log;
    private SortedMap<String, ByLevelOrderBook> orderBooks;
    private Map<Long, ByLevelOrderBook> orderBooksByOrderId;

    @Override
    public void startOrderProcessor(OrderLogger log)
    {
        this.log = log;
        orderBooks = new TreeMap<String, ByLevelOrderBook>();
        orderBooksByOrderId = new HashMap<Long, ByLevelOrderBook>();
    }

    @Override
    public void handleOrder(OrderAction action, OrderDto order)
    {
        if (action == OrderAction.ADD)
        {
            ByLevelOrderBook orderBook = getOrCreateOrderBook(order);
            orderBook.addOrder(order);
        }
        else if (action == OrderAction.EDIT)
        {
            ByLevelOrderBook orderBook = orderBooksByOrderId.get(order.getOrderId());
            orderBook.editOrder(order);
        }
        else if (action == OrderAction.REMOVE)
        {
            ByLevelOrderBook orderBook = orderBooksByOrderId.get(order.getOrderId());
            orderBook.removeOrder(order);
        }
        else
        {
            throw new InvalidOrderException();
        }
    }

    @Override
    public void closeOrderProcessor()
    {
        Iterator<Entry<String, ByLevelOrderBook>> orderBooksEntriesIt = orderBooks.entrySet().iterator();
        while (orderBooksEntriesIt.hasNext())
        {
            Map.Entry<String, ByLevelOrderBook> orderBooksEntry = orderBooksEntriesIt.next();
            orderBooksEntry.getValue().logOrderBook(log);
        }
    }

    private ByLevelOrderBook getOrCreateOrderBook(OrderDto order)
    {
        ByLevelOrderBook result = orderBooks.get(orderBookKey(order));
        if (result == null)
        {
            result = ByLevelOrderBook.create(order);
            orderBooks.put(orderBookKey(order), result);
        }
        orderBooksByOrderId.put(order.getOrderId(), result);
        return result;
    }

    protected String orderBookKey(OrderDto order)
    {
        return order.getShortName() + (order.isBuy() ? "B" : "S");
    }
}
