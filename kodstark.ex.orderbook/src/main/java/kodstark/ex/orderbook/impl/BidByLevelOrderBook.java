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

import java.util.NavigableMap;


public class BidByLevelOrderBook extends ByLevelOrderBook
{

    public BidByLevelOrderBook(String shortName)
    {
        super(shortName);
    }

    @Override
    protected String getOrderBookName()
    {
        return shortName + " Bids";
    }

    @Override
    protected NavigableMap<Integer, OrderBookLevel> getOrderBookLevelsPrintView()
    {
        return orderBookLevels;
    }

}
