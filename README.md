# How are stars counted? How InterSystems Caché eXTreme is used in Gaia

## Astronomers' tools

5 years ago, on December 19, 2013, the ESA launched an orbital telescope called
Gaia. Learn more about the Gaia mission on the official
[website of the European Space Agency](http://sci.esa.int/gaia/) or in the
article by Vitaly Egorov
([Billion pixels for a billion stars](https://translate.google.com/translate?sl=ru&tl=en&u=https%3A%2F%2Fzelenyikot.livejournal.com%2F25742.html)).

However, few people know what technology the agency chose for storing and
processing the data collected by Gaia. Two years before the launch, in 2011, the
developers were considering a number of candidates (see
"[Astrostatistics and Data Mining](https://books.google.com/books?id=ys-e0SotvBoC&amp;lpg=PA109&amp;ots=ut05m7Pem5&amp;dq=Astrometric%20Global%20Iterative%20Solution%20Intersystems&amp;hl=ru&amp;pg=PA112#v=onepage&amp;q=Astrometric%20Global%20Iterative%20Solution%20Intersystems&amp;f=false)"
by Luis Manuel Sarro, Laurent Eyer, William O'Mullane, Joris De Ridder, pp.
111-112):

 - _IBM DB2_,
 - _PostgreSQL_,
 - _[Apache Hadoop](https://hadoop.apache.org/)_,
 - _[Apache Cassandra](https://cassandra.apache.org/)_ and
 - _InterSystems Caché_ (to be more precise, the _[Caché eXTreme Event Persistence](https://docs.intersystems.com/latest/csp/docbook/DocBook.UI.Page.cls?KEY=BXJV_xep)_
 technology). 
 
Comparing the technologies side-by-side produced the following results ([source](images/book-fragment.png)): 

| Technology   | Time     |
|:-------------|---------:|
| DB2          | 13min55s |
| PostgreSQL 8 | 14min50s |
| PostgreSQL 9 |  6min50s |
| Hadoop       |  3min37s |
| Cassandra    |  3min37s |
| Caché        |  2min25s |
     
The first four will probably sound familiar even to schoolchildren. But what is
_Caché XEP_?

## Java technologies in Caché

If you look at the Java API stack provided by _InterSystems_, you will see the
following:

 - The
 _[Caché Object Binding](https://docs.intersystems.com/latest/csp/docbook/DocBook.UI.Page.cls?KEY=BLJV)_
 technology that transparently projects data in Java. In _Caché_ terms, the
 generated Java proxy classes are called exactly like that - projections. This
 approach is the simplest, since it saves the "natural" relations between
 classes in the object model, but doesn't guarantee great performance: a lot of
 service metadata describing the object model is transferred "over the wires".
 
 - JDBC and various add-ons (_Hibernate_, _JPA_). I guess I won't tell you
 anything new here apart from the fact that Caché supports two types of
 transaction isolation: 
 [`READ_UNCOMMITTED`](https://docs.oracle.com/javase/8/docs/api/java/sql/Connection.html#TRANSACTION_READ_UNCOMMITTED)
 and [`READ_COMMITTED`](https://docs.oracle.com/javase/8/docs/api/java/sql/Connection.html#TRANSACTION_READ_COMMITTED)
 – and works in the `READ_UNCOMMITTED` mode by default.

 - The _Caché eXTreme_ family (also available in
 _[.NET](https://docs.intersystems.com/latest/csp/docbook/DocBook.UI.Page.cls?KEY=BXNT)_
 and _[Node.js](https://docs.intersystems.com/latest/csp/docbook/DocBook.UI.Page.cls?KEY=BXJS)_
 editions). This approach is characterized by the direct access to the low-level
 data representation (so-called "globals" – quanta of data in the _Caché_
 world) ensuring high performance. The
 _[Caché XEP](https://docs.intersystems.com/latest/csp/docbook/DocBook.UI.Page.cls?KEY=BXJV_xep)_
 library simultaneously provides object and quasi-relational access to data.

   - Object – the API client no longer needs to care about object-relational
   representation: following the Java object model (even in cases of complex
   multi-layer inheritance),
   [the system automatically creates](https://docs.intersystems.com/latest/csp/docbook/DocBook.UI.Page.cls?KEY=BXJV_xep_import)
   an object model on the _Caché_ class level (or a DB schema if we want to use
   the terms of the relational representation).

   - Quasi-relational – in the sense that you can
   [run SQL queries](https://docs.intersystems.com/latest/csp/docbook/DocBook.UI.Page.cls?KEY=BXJV_xep_queries)
   against multiple "events" stored in a database (to be exact, requests using 
   the SQL subset) directly from the context of an _eXTreme_-connection.
   [Indices](https://docs.intersystems.com/latest/csp/docbook/DocBook.UI.Page.cls?KEY=BXJV_xep_events_indexing)
   and transactions are fully supported as well. Of course, all loaded data
   become immediately accessible via JDBC and a relational representation
   (supporting all the powerful features of ANSI SQL and SQL extensions specific
   to the _Caché_ dialect), but the access speed will be completely different.

Summing up, here's what we have:

 - "schema" import (_Caché_ classes are created automatically), including
 - import of the Java class hierarchy;
 - instant relational access to data – you can work with _Caché_ classes the way
 you work with tables;
 - support of indices and transactions via _Caché eXTreme_;
 - support of simple SQL queries via _Caché eXTreme_;
 - support of arbitrary SQL queries via the underlying JDBC over TCP connection (_Caché_ uses the standard [Type 4](https://docs.oracle.com/cd/E19509-01/820-5069/ggzbd/index.html) (Direct-to-Database Pure Java) driver).

This approach offers some advantages in comparison with comparable relational
(higher access speed) and various NoSQL solutions (instant access to data in the
relational style).

The "nuance" of configuring _Caché eXTreme_ prior to connecting is the environment set-up:

 - the `GLOBALS_HOME` variable has to point to the _Caché_ installation folder
 and
 - `LD_LIBRARY_PATH` (`DYLD_LIBRARY_PATH` for _Mac OS X_ or `PATH` for
 _Windows_) has to contain `${GLOBALS_HOME}/bin`.

Additionally, you may need to increase the stack and heap size
of the _JVM_ (`-Xss2m -Xmx768m`).

## Some practice

The authors were interested in how _Caché eXTreme_ would behave while writing an
uninterrupted stream of data in comparison with other data processing
technologies. We used historical stock price data in the CSV format from the
website of the
"[Finam](https://www.finam.ru/profile/mirovye-indeksy/nasdaq/export/)"
holding. Sample data file:

```
<TICKER>,<PER>,<DATE>,<TIME>,<LAST>,<VOL>
NASDAQ100,0,20130802,09:31:07,3 125.300000000,0
NASDAQ100,0,20130802,09:32:08,3 122.860000000,806 906
NASDAQ100,0,20130802,09:33:09,3 123.920000000,637 360
NASDAQ100,0,20130802,09:34:10,3 124.090000000,421 928
NASDAQ100,0,20130802,09:35:11,3 125.180000000,681 585
```

The code of the _Caché_ class modeling the above structure might look like this:

```
Class com.intersystems.persistence.objbinding.Event Extends %Persistent [ ClassType = persistent, DdlAllowed, Final, SqlTableName = Event ]
{
Property Ticker As %String(MAXLEN = 32);

Property Per As %Integer(MAXVAL = 2147483647, MINVAL = -2147483648);

Property TimeStamp As %TimeStamp;

Property Last As %Double;

Property Vol As %Integer(MAXVAL = 9223372036854775807, MINVAL = -9223372036854775810);
}
```

We also wrote some basic and naive test code. This "naive" approach can be
justified by the fact that we are not really measuring the speed of the code
generated by _JIT_, but the speed at which the code that is completely unrelated
to _JVM_ (with the exception of _Apache Derby_) can write to the disk. Here's
how the test program window looks like:

![Caché eXTreme Persistence Benchmark](images/persistence-benchmark.png)

Our contenders:

 - _Apache Derby_ 10.14.2.0
 - _Oracle_ 10.2.0.3.0
 - _InterSystems Caché_ 2018.1 (JDBC)
 - _InterSystems Caché_ 2018.1 (eXTreme)

Note that since tests are somewhat approximate, we saw no practical purpose in
providing exact numbers: the margin of error is fairly high, while the goal of
the article is to demonstrate the general tendency. For the same reasons, we are
not specifying the exact version of JDK and the settings of the garbage
collector: the server-side JVM 8u191 with `-Xmx2048m -Xss128m` reached a very
similar level of performance on _Linux_ and _Mac OS X_. One million events
were saved in each test; several warm-up runs (up to 10) were performed before
each test of a particular database. As for _Caché_ settings, the routine cache
was increased to 256 MB and the 8kb database cache was expanded to 1024 MB.

Our testing yielded the following results (the write speed values are expressed
in events per second (eps)):

| Technology | Time, s (less is better) | Write speed, eps (more is better) |
|:-----------|-----------------:|--:|
| Apache Derby | 140&plusmn;30 | 7100&plusmn;1300 |
| Oracle | 780&plusmn;50 | 1290&plusmn;80 |
| Caché JDBC | 61&plusmn;8 | 17000&plusmn;2000 |
| Caché eXTreme | 6.7&plusmn;0.8 | 152000&plusmn;17000 |
| Caché eXTreme, transaction journaling disabled | 6.3&plusmn;0.6 | 162000&plusmn;14000 |

 1. _Derby_ offers speeds varying from 6200 to 8000
 eps.
 
 1. _Oracle_ turned out to be as fast as 1290 eps.

 1. _Caché_ in the JDBC mode gives you a higher speed (from 15000 to 18000 eps),
 but there is a trade-off: the default transaction isolation level, as mentioned
 above, is `READ_UNCOMMITTED`.

 1. The next option, _Caché eXTreme_, gives us 127000 to 167000 eps.
 
 1. Finally, we took some risk and
 [disabled the transaction log](https://docs.intersystems.com/latest/csp/docbook/DocBook.UI.Page.cls?KEY=GCDI_journal_util_NOJRN)
 (for a given client process), and managed to achieve the write speed of 172000
 eps on a test system.

Those who are interested in more accurate numbers can view the
[source code](https://github.com/unix-junkie/persistence-benchmark). You will
need the following to build and run:
 
 - _JDK_ 1.8+,
 - _Git_,
 - _Maven_
 - _Oracle_ JDBC driver (available from [Oracle Maven Repository](https://blogs.oracle.com/dev2dev/get-oracle-jdbc-drivers-and-ucp-from-oracle-maven-repository-without-ides)) 
 - _[Maven Install Plugin](https://maven.apache.org/plugins/maven-install-plugin/)_
 for [for creating local](https://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html)
 _Caché JDBC_ and _Caché eXTreme_ artifacts:
    ```
    $ mvn install:install-file -Dfile=cache-db-2.0.0.jar
    $ mvn install:install-file -Dfile=cache-extreme-2.0.0.jar
    $ mvn install:install-file -Dfile=cache-gateway-2.0.0.jar
    $ mvn install:install-file -Dfile=cache-jdbc-2.0.0.jar
    ```
   and, finally,
 - _Caché_ 2018.1+.
