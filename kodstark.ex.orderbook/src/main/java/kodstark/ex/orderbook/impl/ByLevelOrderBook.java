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
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

import kodstark.ex.orderbook.OrderLogger;
import kodstark.ex.orderbook.OrderDto;

abstract class ByLevelOrderBook
{
    protected NavigableMap<Integer, OrderBookLevel> orderBookLevels = new TreeMap<Integer, OrderBookLevel>();
    protected Map<Long, OrderWithLevel> orderBookLevelsByOrderId = new HashMap<Long, OrderWithLevel>();
    protected final String shortName;

    ByLevelOrderBook(String shortName)
    {
        this.shortName = shortName;
    }

    public static ByLevelOrderBook create(OrderDto order)
    {
        if (order.isBuy())
        {
            return new BidByLevelOrderBook(order.getShortName());
        }
        else
        {
            return new AskByLevelOrderBook(order.getShortName());
        }
    }

    void addOrder(OrderDto order)
    {
        ensureOrderUniqueness(order);
        OrderBookLevel orderBookLevel = orderBookLevels.get(order.getPrice());
        if (orderBookLevel == null)
        {
            orderBookLevel = createOrderBookLevel(order);
            orderBookLevels.put(order.getPrice(), orderBookLevel);
        }
        else
        {
            orderBookLevel.addOrder(order);
        }
        orderBookLevelsByOrderId.put(order.getOrderId(), new OrderWithLevel(order, orderBookLevel));
    }

    void removeOrder(OrderDto order)
    {
        OrderWithLevel orderWithLevel = orderBookLevelsByOrderId.get(order.getOrderId());
        if (orderWithLevel == null)
        {
            throw new InvalidOrderException();
        }
        removeOrderWithLevel(orderWithLevel);
        orderBookLevelsByOrderId.remove(order.getOrderId());
    }

    void editOrder(OrderDto order)
    {
        OrderWithLevel orderWithLevel = orderBookLevelsByOrderId.get(order.getOrderId());
        if (orderWithLevel == null)
        {
            throw new InvalidOrderException();
        }
        editOrderWithLevel(orderWithLevel, order);
    }

    protected OrderBookLevel createOrderBookLevel(OrderDto order)
    {
        return new OrderBookLevel(order);
    }

    protected abstract String getOrderBookName();

    protected abstract NavigableMap<Integer, OrderBookLevel> getOrderBookLevelsPrintView();

    private void ensureOrderUniqueness(OrderDto order)
    {
        if (orderBookLevelsByOrderId.containsKey(order.getOrderId()))
        {
            throw new InvalidOrderException();
        }
    }

    private void removeOrderWithLevel(OrderWithLevel orderWithLevel)
    {
        OrderDto order = orderWithLevel.order;
        OrderBookLevel orderBookLevel = orderWithLevel.level;
        if (orderBookLevel.getOrdersCount() > 1)
        {
            orderBookLevel.removeOrder(order);
        }
        else
        {
            orderBookLevels.remove(order.getPrice());
        }
    }

    private void editOrderWithLevel(OrderWithLevel orderWithLevel, OrderDto newOrder)
    {
        OrderDto order = orderWithLevel.order;
        OrderBookLevel orderBookLevel = orderWithLevel.level;
        if (order.getPrice() != newOrder.getPrice())
        {
            removeOrder(order);
            addOrder(newOrder);
        }
        else
        {
            orderBookLevel.updateFromNewOrder(order, newOrder);
        }
    }

    static class OrderWithLevel
    {
        private final OrderDto order;
        private final OrderBookLevel level;

        OrderWithLevel(OrderDto order, OrderBookLevel level)
        {
            this.order = order;
            this.level = level;
        }

        @Override
        public String toString()
        {
            return order + ":" + level;
        }
    }

    void logOrderBook(OrderLogger log)
    {
        ByLevelOrderBookLogger bookLogger = new ByLevelOrderBookLogger(log);
        bookLogger.log();
    }

    class ByLevelOrderBookLogger
    {
        OrderLogger log;

        public ByLevelOrderBookLogger(OrderLogger log)
        {
            this.log = log;
        }

        void log()
        {
            log.log(getOrderBookName());
            logOrderBookLevels(getOrderBookLevelsPrintView());
        }

        private void logOrderBookLevels(SortedMap<Integer, OrderBookLevel> orderBookLevels)
        {
            Iterator<Entry<Integer, OrderBookLevel>> levelsEntriesIt = orderBookLevels.entrySet().iterator();
            while (levelsEntriesIt.hasNext())
            {
                Map.Entry<Integer, OrderBookLevel> levelsEntry = levelsEntriesIt.next();
                logOrderBookLevel(levelsEntry);
            }
        }

        private void logOrderBookLevel(Map.Entry<Integer, OrderBookLevel> levelsEntry)
        {
            Integer price = levelsEntry.getKey();
            OrderBookLevel orderBookLevel = levelsEntry.getValue();
            String mess = String.format("%s %s %s", price, orderBookLevel.getQuantity(), orderBookLevel.getOrdersCount());
            log.log(mess);
        }
    }

    @Override
    public String toString()
    {
        return String.valueOf(orderBookLevels);
    }
}
