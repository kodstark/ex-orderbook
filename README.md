## Order Book

Example of BDD unit tests for order book problem.

Order book keeps buy and sell orders in required arrangement. By level order book is organized by price 
and for each price level it counts how many orders and quantities exists.

BDD tests focus on behaviour and used words. Because of that changing implementation without breaking API
should have minimal impact on test.

Example of tests:

```
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
```

#### Build

Launch build

```mvn clean install```

Generate coverage report

```mvn clean install -Pcoverage```

#### License

Copyright (C) 2007 Kamil Demecki <kodstark@gmail.com>

Licensed under the terms of any of the following licenses at your choice:

- GNU Lesser General Public License Version 2.1 or later (the "LGPL")
  http://www.gnu.org/licenses/lgpl.html

- Mozilla Public License Version 1.1 or later (the "MPL")
  http://www.mozilla.org/MPL/MPL-1.1.html
