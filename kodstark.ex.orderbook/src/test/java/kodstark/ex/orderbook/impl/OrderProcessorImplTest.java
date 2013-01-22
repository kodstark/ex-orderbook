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

import static junit.framework.Assert.assertEquals;

import java.io.PrintWriter;
import java.io.StringWriter;

import kodstark.ex.orderbook.OrderAction;
import kodstark.ex.orderbook.OrderLogger;
import kodstark.ex.orderbook.OrderDto;
import kodstark.ex.orderbook.impl.OrderProcessorImpl;

import org.junit.Before;
import org.junit.Test;

public class OrderProcessorImplTest
{
    private OrderProcessorImpl orderConsumer;
    private StringWriter output;
    private PrintWriter writer;
    private StringBuilder expectedLines;
    private static final boolean BUY = true;
    private static final boolean SELL = false;

    @Before
    public void setUp()
    {
        output = new StringWriter();
        writer = new PrintWriter(output);
        orderConsumer = new OrderProcessorImpl();
        orderConsumer.startOrderProcessor(getLog());
        expectedLines = new StringBuilder();
    }

    @Test
    public void shouldAdd()
    {
        add(1L, "NSD.E", BUY, 5, 200);
        add(3L, "NSD.E", SELL, 5, 300);
        add(4L, "NSD.E", BUY, 7, 150);

        orderConsumer.closeOrderProcessor();

        expectedOrderBookLine("NSD.E Bids");
        expectedOrderBookLine("5 200 1");
        expectedOrderBookLine("7 150 1");
        expectedOrderBookLine("NSD.E Asks");
        expectedOrderBookLine("5 300 1");

        assertEquals(expectedLines.toString(), output.toString());
    }

    @Test
    public void shouldRemoveOne()
    {
        add(1L, "NSD.E", BUY, 5, 200);
        add(3L, "NSD.E", SELL, 5, 300);
        add(4L, "NSD.E", BUY, 7, 150);
        remove(1L);

        orderConsumer.closeOrderProcessor();

        expectedOrderBookLine("NSD.E Bids");
        expectedOrderBookLine("7 150 1");
        expectedOrderBookLine("NSD.E Asks");
        expectedOrderBookLine("5 300 1");

        assertEquals(expectedLines.toString(), output.toString());
    }

    @Test
    public void shouldEdit()
    {
        add(1L, "NSD.E", BUY, 5, 200);
        add(3L, "NSD.E", SELL, 5, 300);
        add(4L, "NSD.E", BUY, 7, 150);
        remove(1L);
        edit(3L, 7, 200);

        orderConsumer.closeOrderProcessor();

        expectedOrderBookLine("NSD.E Bids");
        expectedOrderBookLine("7 150 1");
        expectedOrderBookLine("NSD.E Asks");
        expectedOrderBookLine("7 200 1");

        assertEquals(expectedLines.toString(), output.toString());
    }

    @Test
    public void shouldAddZeroAndEdit()
    {
        add(88L, "FLB.Q", BUY, 35, 0);
        edit(88L, 100, 214);

        orderConsumer.closeOrderProcessor();

        expectedOrderBookLine("FLB.Q Bids");
        expectedOrderBookLine("100 214 1");

        assertEquals(expectedLines.toString(), output.toString());
    }
    
    @Test
    public void shouldAddAndEditZero()
    {
        add(88L, "FLB.Q", BUY, 35, 214);
        edit(88L, 100, 0);

        orderConsumer.closeOrderProcessor();

        expectedOrderBookLine("FLB.Q Bids");
        expectedOrderBookLine("100 0 1");

        assertEquals(expectedLines.toString(), output.toString());
    }    
    
    @Test
    public void shouldAddAndEditZero2()
    {
        add(88L, "FLB.Q", BUY, 35, 214);
        edit(88L, 100, 0);
        edit(88L, 100, 440);

        orderConsumer.closeOrderProcessor();

        expectedOrderBookLine("FLB.Q Bids");
        expectedOrderBookLine("100 440 1");

        assertEquals(expectedLines.toString(), output.toString());
    }     

    @Test
    public void shouldSequentialEdit()
    {
        add(1L, "NSD.E", SELL, 10, 300);
        add(2L, "NSD.E", BUY, 20, 200);
        add(3L, "NSD.E", BUY, 30, 150);
        edit(3L, 40, 201);
        edit(3L, 20, 202);

        orderConsumer.closeOrderProcessor();

        expectedOrderBookLine("NSD.E Bids");
        expectedOrderBookLine("20 402 2");
        expectedOrderBookLine("NSD.E Asks");
        expectedOrderBookLine("10 300 1");

        assertEquals(expectedLines.toString(), output.toString());
    }

    @Test
    public void shouldSequentialEdit2()
    {
        add(1L, "NSD.E", SELL, 10, 300);
        add(2L, "NSD.E", BUY, 20, 200);
        add(3L, "NSD.E", BUY, 30, 150);
        edit(3L, 40, 201);
        edit(3L, 20, 202);
        edit(3L, 18, 203);

        orderConsumer.closeOrderProcessor();

        expectedOrderBookLine("NSD.E Bids");
        expectedOrderBookLine("18 203 1");
        expectedOrderBookLine("20 200 1");
        expectedOrderBookLine("NSD.E Asks");
        expectedOrderBookLine("10 300 1");

        assertEquals(expectedLines.toString(), output.toString());
    }

    @Test
    public void shouldSequentialEdit3()
    {
        add(1L, "NSD.E", SELL, 10, 300);
        add(2L, "NSD.E", BUY, 20, 200);
        add(3L, "NSD.E", BUY, 30, 150);
        edit(3L, 40, 201);
        edit(3L, 20, 202);
        edit(3L, 20, 206);

        orderConsumer.closeOrderProcessor();

        expectedOrderBookLine("NSD.E Bids");
        expectedOrderBookLine("20 406 2");
        expectedOrderBookLine("NSD.E Asks");
        expectedOrderBookLine("10 300 1");

        assertEquals(expectedLines.toString(), output.toString());
    }

    @Test
    public void shouldMoreLines()
    {
        add(2L, "ZTA.B", BUY, 15, 100);
        add(5L, "ZTA.B", SELL, 17, 300);
        add(6L, "ZTA.B", BUY, 12, 150);
        add(7L, "ZTA.B", SELL, 16, 100);
        add(8L, "ZTA.B", SELL, 19, 100);
        add(9L, "ZTA.B", SELL, 21, 112);
        remove(5L);

        orderConsumer.closeOrderProcessor();

        expectedOrderBookLine("ZTA.B Bids");
        expectedOrderBookLine("12 150 1");
        expectedOrderBookLine("15 100 1");
        expectedOrderBookLine("ZTA.B Asks");
        expectedOrderBookLine("21 112 1");
        expectedOrderBookLine("19 100 1");
        expectedOrderBookLine("16 100 1");

        assertEquals(expectedLines.toString(), output.toString());
    }

    @Test
    public void shouldHaveTwoOrderBooks()
    {
        add(1L, "NSD.E", BUY, 5, 200);
        add(2L, "ZTA.B", BUY, 15, 100);
        add(3L, "NSD.E", SELL, 5, 300);
        add(4L, "NSD.E", BUY, 7, 150);
        remove(1L);
        add(5L, "ZTA.B", SELL, 17, 300);
        add(6L, "ZTA.B", BUY, 12, 150);
        edit(3L, 7, 200);
        add(7L, "ZTA.B", SELL, 16, 100);
        add(8L, "ZTA.B", SELL, 19, 100);
        add(9L, "ZTA.B", SELL, 21, 112);
        remove(5L);

        orderConsumer.closeOrderProcessor();

        expectedOrderBookLine("NSD.E Bids");
        expectedOrderBookLine("7 150 1");
        expectedOrderBookLine("NSD.E Asks");
        expectedOrderBookLine("7 200 1");
        expectedOrderBookLine("ZTA.B Bids");
        expectedOrderBookLine("12 150 1");
        expectedOrderBookLine("15 100 1");
        expectedOrderBookLine("ZTA.B Asks");
        expectedOrderBookLine("21 112 1");
        expectedOrderBookLine("19 100 1");
        expectedOrderBookLine("16 100 1");

        assertEquals(expectedLines.toString(), output.toString());
    }

    private void expectedOrderBookLine(String line)
    {
        expectedLines.append(line);
        expectedLines.append('\n');
    }

    public void add(long orderId, String shortName, boolean isBuy, int price, int quantity)
    {
        orderConsumer.handleOrder(OrderAction.ADD, new OrderDto(orderId, shortName, isBuy, price, quantity));
    }

    public void edit(long orderId, int price, int quantity)
    {
        orderConsumer.handleOrder(OrderAction.EDIT, new OrderDto(orderId, null, true, price, quantity));
    }

    public void remove(long orderId)
    {
        orderConsumer.handleOrder(OrderAction.REMOVE, new OrderDto(orderId, null, true, -1, -1));
    }

    private OrderLogger getLog()
    {
        return new OrderLogger()
        {
            @Override
            public void log(String msg)
            {
                writer.println(msg);
            }
        };
    }
}
