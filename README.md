# Как считают звёзды? Использование InterSystems Caché eXTreme в Gaia

## Инструменты звездочётов

5 лет назад, 19 декабря 2013 года был запущен спутниковый телескоп _Gaia_.
Подробно о миссии Gaia можно прочитать на
[сайте Европейского Космического Агентства](http://sci.esa.int/gaia/) и в статье
Виталия Егорова (см.
[Billion pixels for a billion stars](https://translate.google.com/translate?sl=ru&tl=en&u=https%3A%2F%2Fzelenyikot.livejournal.com%2F25742.html)).

Однако мало кто знает, какую технологию разработчики ЕКА выбрали для обработки и
хранения данных, собираемых Gaia. Двумя годами ранее, в 2011 году, в качестве
кандидатов рассматривались (см.
[Astrostatistics and Data Mining](http://books.google.com/books?id=ys-e0SotvBoC&amp;lpg=PA109&amp;ots=ut05m7Pem5&amp;dq=Astrometric%20Global%20Iterative%20Solution%20Intersystems&amp;hl=ru&amp;pg=PA112#v=onepage&amp;q=Astrometric%20Global%20Iterative%20Solution%20Intersystems&amp;f=false)
by Luis Manuel Sarro, Laurent Eyer, William O'Mullane, Joris De Ridder, pp.
111-112):

 - _IBM DB2_,
 - _PostgreSQL_,
 - _[Apache Hadoop](http://hadoop.apache.org/)_,
 - _[Apache Cassandra](http://cassandra.apache.org/)_ и
 - _InterSystems Caché_ (точнее – технология _[Caché eXTreme Event Persistence](https://docs.intersystems.com/latest/csp/docbook/DocBook.UI.Page.cls?KEY=BXJV_xep)_. 
 
Сравнение технологий на производительность дало след. результаты ([источник](images/book-fragment.png)): 

| Технология   | Время    |
|--------------|---------:|
| DB2          | 13min55s |
| PostgreSQL 8 | 14min50s |
| PostgreSQL 9 |  6min50s |
| Hadoop       |  3min37s |
| Cassandra    |  3min37s |
| Caché        |  2min25s |
     
О первых четырёх "игроках" индустрии знает, наверное, едва ли не каждый
школьник. А вот что же такое <i>Caché XEP</i>?

## Java-технологии в Caché

## Немного практики
