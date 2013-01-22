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
 * Process orders. 
 * 
 * @author kodstark
 */
public interface OrderProcessor
{
    public abstract void startOrderProcessor(OrderLogger log);

    public abstract void handleOrder(OrderAction action, OrderDto order);

    public abstract void closeOrderProcessor();
}