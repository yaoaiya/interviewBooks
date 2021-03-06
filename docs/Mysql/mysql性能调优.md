[TOC]
# 什么是MySQL?

 MySQL 是⼀种关系型数据库，在Java企业级开发中⾮常常⽤，因为 MySQL 是开源免费的，并 且⽅便扩展。阿⾥巴巴数据库系统也⼤量⽤到了 MySQL，因此它的稳定性是有保障的。MySQL 是开放源代码的，因此任何⼈都可以在 GPL(General Public License) 的许可下下载并根据个性 化的需要对其进⾏修改。MySQL的默认端⼝号是3306。



# MyISAM和InnoDB区别 ？

MyISAM是MySQL的默认数据库引擎（5.5版之前）。虽然性能极佳，⽽且提供了⼤量的特性， 包括全⽂索引、压缩、空间函数等，但MyISAM不⽀持事务和⾏级锁，⽽且最⼤的缺陷就是崩溃 后⽆法安全恢复。不过，5.5版本之后，MySQL引⼊了InnoDB（事务性数据库引擎），MySQL 5.5版本后默认的存储引擎为InnoDB。 

⼤多数时候我们使⽤的都是 InnoDB 存储引擎，但是在某些情况下使⽤ MyISAM 也是合适的⽐如 读密集的情况下。（如果你不介意 MyISAM 崩溃恢复问题的话）。 

**两者的对比：** 

1. 是否⽀持⾏级锁 : MyISAM 只有表级锁(table-level locking)，⽽InnoDB ⽀持⾏级锁(rowlevel locking)和表级锁,默认为⾏级锁。 

2.  是否⽀持事务和崩溃后的安全恢复： MyISAM 强调的是性能，每次查询具有原⼦性,其执⾏ 速度⽐InnoDB类型更快，但是不提供事务⽀持。但是InnoDB 提供事务⽀持事务，外部键等 ⾼级数据库功能。 具有事务(commit)、回滚(rollback)和崩溃修复能⼒(crash recovery capabilities)的事务安全(transaction-safe (ACID compliant))型表。 

3.  是否⽀持外键： MyISAM不⽀持，⽽InnoDB⽀持。

4.  是否⽀持MVCC ：仅 InnoDB ⽀持。应对⾼并发事务, MVCC⽐单纯的加锁更⾼效;MVCC只 在 READ COMMITTED 和 REPEATABLE READ 两个隔离级别下⼯作;MVCC可以使⽤ 乐 观(optimistic)锁 和 悲观(pessimistic)锁来实现;各数据库中MVCC实现并不统⼀。推荐阅读：

   [MySQL-InnoDB-MVCC多版本并发控制]: https://segmentfault.com/a/1190000012650596

《MySQL⾼性能》上⾯有⼀句话这样写到: 

> 不要轻易相信“MyISAM⽐InnoDB快”之类的经验之谈，这个结论往往不是绝对的。在很多我 们已知场景中，InnoDB的速度都可以让MyISAM望尘莫及，尤其是⽤到了聚簇索引，或者需 要访问的数据都可以放⼊内存的应⽤。 

⼀般情况下我们选择 InnoDB 都是没有问题的，但是某些情况下你并不在乎可扩展能⼒和并发能力，也不需要事务⽀持，也不在乎崩溃后的安全恢复问题的话，选择MyISAM也是⼀个不错的选 择。但是⼀般情况下，我们都是需要考虑到这些问题的



# mysql的MVCC多版本并发控制？

**从书中可以了解到:**

- MVCC是被Mysql中 `事务型存储引擎InnoDB` 所支持的;
- **应对高并发事务, MVCC比`单纯的加锁`更高效**;
- MVCC只在 `READ COMMITTED` 和 `REPEATABLE READ` 两个隔离级别下工作;
- MVCC可以使用 `乐观(optimistic)锁` 和 `悲观(pessimistic)锁`来实现;
- 各数据库中MVCC实现并不统一
- 但是书中提到 "InnoDB的MVCC是通过在每行记录后面保存**两个隐藏的列**来实现的"(网上也有很多此类观点), 但其实并不准确, 可以参考[MySQL官方文档](https://link.segmentfault.com/?enc=sW97c8CR5LOpTphX25G6zA%3D%3D.92l3H3d7pUui2nz2l4o0xGN%2FXG78kooOV7spssMbHM2UZsIkzx3LEWB920gNaiAYXLEaGXCCt3SO%2FSey8vwuBv5DW%2FQm14mzPtZG3v5OK%2FE%3D), 可以看到, InnoDB存储引擎在数据库每行数据的后面添加了**三个字段**, 不是两个!!

InnoDB存储引擎在数据库每行数据的后面添加了三个字段

- 6字节的`事务ID`(`DB_TRX_ID`)字段: 用来标识最近一次对本行记录做修改(insert|update)的事务的标识符, 即最后一次修改(insert|update)本行记录的事务id。
  至于delete操作，在innodb看来也不过是一次update操作，更新行中的一个特殊位将行表示为deleted, **并非真正删除**。
- 7字节的`回滚指针`(`DB_ROLL_PTR`)字段: 指写入回滚段(rollback segment)的 `undo log` record (撤销日志记录记录)。
  如果一行记录被更新, 则 `undo log` record 包含 '重建该行记录被更新之前内容' 所必须的信息。
- 6字节的`DB_ROW_ID`字段: 包含一个随着新行插入而单调递增的行ID, 当由innodb自动产生聚集索引时，聚集索引会包括这个行ID的值，否则这个行ID不会出现在任何索引中。
  结合聚簇索引的相关知识点, 我的理解是, 如果我们的表中没有主键或合适的唯一索引, 也就是无法生成聚簇索引的时候, InnoDB会帮我们自动生成聚集索引, 但聚簇索引会使用DB_ROW_ID的值来作为主键; 如果我们有自己的主键或者合适的唯一索引, 那么聚簇索引中也就不会包含 DB_ROW_ID 了 。

**总结**

InnoDB实现MVCC的方式是:

- 事务以排他锁的形式修改原始数据
- 把修改前的数据存放于undo log，通过回滚指针与主数据关联
- 修改成功（commit）啥都不做，失败则恢复undo log中的数据（rollback）



#  字符集及校对规则 ？

字符集指的是⼀种从⼆进制编码到某类字符符号的映射。校对规则则是指某种字符集下的排序规则。MySQL中每⼀种字符集都会对应⼀系列的校对规则。 

MySQL采⽤的是类似继承的⽅式指定字符集的默认值，每个数据库以及每张数据表都有⾃⼰的默 认值，他们逐层继承。⽐如：某个库中所有表的默认字符集将是该数据库所指定的字符集（这些 表在没有指定字符集的情况下，才会采⽤默认字符集） 

# 索引

 MySQL索引使⽤的数据结构主要有**BTree索引** 和 **哈希索引** 。

对于哈希索引来说，底层的数据结 构就是哈希表，因此在绝⼤多数需求为单条记录查询的时候，可以选择哈希索引，查询性能最 快；其余大部分场景，建议选择BTree索引。 

MySQL的BTree索引使⽤的是B树中的B+Tree，但对于主要的两种存储引擎的实现⽅式是不同 的。 

- **MyISAM:** B+Tree叶节点的data域存放的是数据记录的地址。在索引检索的时候，⾸先按照 B+Tree搜索算法搜索索引，如果指定的Key存在，则取出其 data 域的值，然后以 data 域的 值为地址读取相应的数据记录。这被称为“⾮聚簇索引”。 

- **InnoDB:** 其数据⽂件本身就是索引⽂件。相比MyISAM的索引⽂件和数据⽂件是分离的，其 表数据⽂件本身就是按B+Tree组织的⼀个索引结构，树的叶节点data域保存了完整的数据记 录。这个索引的key是数据表的主键，因此InnoDB表数据⽂件本身就是主索引。这被称为“聚 簇索引（或聚集索引）”。而其余的索引都作为辅助索引，辅助索引的data域存储相应记录主 键的值⽽不是地址，这也是和MyISAM不同的地⽅。**在根据主索引搜索时，直接找到key所 在的节点即可取出数据；在根据辅助索引查找时，则需要先取出主键的值，再走⼀遍主索 引。 因此，在设计表的时候，不建议使用过长的字段作为主键，也不建议使⽤⾮单调的字段 作为主键，这样会造成主索引频繁分裂。** PS：整理⾃《Java⼯程师修炼之道》



# 查询缓存的使用

执行查询的时候会先查询缓存，不过mysql8.0以后移除，因为这个功能不太实用。

**开启查询缓存后在同样的查询条件以及数据情况下，会直接在缓存中返回结果。**这⾥的查 询条件包括查询本身、当前要查询的数据库、客户端协议版本号等⼀些可能影响结果的信息。因 此任何两个查询在任何字符上的不同都会导致缓存不命中。此外，如果查询中包含任何⽤户⾃定 义函数、存储函数、⽤户变量、临时表、MySQL库中的系统表，其查询结果也不会被缓存。

缓存建⽴之后，MySQL的查询缓存系统会跟踪查询中涉及的每张表，如果这些表（数据或结构） 发⽣变化，那么和这张表相关的所有缓存数据都将失效。

**缓存虽然能够提升数据库的查询性能，但是缓存同时也带来了额外的开销，每次查询后都要做⼀ 次缓存操作，失效后还要销毁。** 因此，开启缓存查询要谨慎，尤其对于写密集的应⽤来说更是如 此。如果开启，要注意合理控制缓存空间大小，⼀般来说其大小设置为几十MB比较合适。

# 什么是事务？

事务是逻辑上的一组操作，要么都执行，要么都不执行。

## 事务的四大特性

**原子性：**事务是最小的执行单位，不允许分隔，事务的原子性保证一个动作要么全部执行，要么完全不起作用。

**一致性：**事务执行前后，数据保持一致，多个事务对同一个数据的访问结果是相同的。

**隔离性：**并发访问数据库时，一个用户的事务不被其他用户干扰，各并发事务之间数据库是独立的。

**持久性：**一个事务提交后，对数据库的改变是持久的，即使数据库发生改变也不应该对其有任何影响。



# 并发事务带来哪些问题？

- **脏读：**一个事务读取到了另一个事务未提交的修改数据，那么就出现了**脏读**问题。（<u>*读未提交可导致*</u>）

- **不可重复读：**指在一个事务内多次读取同一个数据，在这个事务还没有结束时，另一个事务对这个数据进行修改。那么在第一个数据访问之间，由于第二个事务对数据的修改，造成了第一个事务多次读取该数据的结果可能不一致。这样就造成了**在同一个事务内多次读取同一个数据的结果不一样的情况**，所以叫做不可重复读。（*<u>读已提交可导致</u>*）

- **幻读：**幻读与不可重复读类似。它发⽣在⼀个事务（T1）读取了⼏⾏数 据，接着另⼀个并发事务（T2）插⼊了⼀些数据时。在随后的查询中，第⼀个事务（T1） 就会发现多了⼀些原本不存在的记录，就好像发⽣了幻觉⼀样，所以称为幻读

  <u>不可重复读和幻读区别</u>： 不可重复读的重点是修改⽐如多次读取⼀条记录发现其中某些列的值被修改，幻读的重点在于新 增或者删除⽐如多次读取⼀条记录发现记录增多或减少了。



# 事务的隔离级别有哪些，mysql的默认隔离级别是什么？

因此如果我们可以容忍一些严重程度较轻的问题，我们就能获取一些性能上的提升。于是便有了事务的四种隔离级别：

- 读未提交（`Read Uncommitted`）：允许读取未提交的记录，会发生脏读、不可重复读、幻读；
- 读已提交（`Read Committed`）：只允许读物已提交的记录，不会发生脏读，但会出现重复读、幻读；
- 可重复读（`Repeatable Read`）：不会发生脏读和不可重复读的问题，但会发生幻读问题；但`MySQL`在此隔离级别下利用**MVCC**或者**间隙锁**可以禁止幻读问题的发生；
- 可串行化（`Serializable`）：即事务串行执行，以上各种问题自然也就都不会发生。

默认的事务处理级别就是【REPEATABLE-READ】，也就是可重复读。



# 锁机制与InnoDB算法

**MyISAM和InnoDB存储引擎使用的锁：** 

- MyISAM采⽤表级锁(table-level locking)。 
- InnoDB⽀持⾏级锁(row-level locking)和表级锁,（<u>*默认为行级锁。*</u>）

表级锁和行级锁的对比：

- **表级锁：** MySQL中锁定 粒度最大的⼀种锁，对当前操作的整张表加锁，实现简单，资源消耗也比较少，加锁快，不会出现死锁。其锁定粒度最⼤，触发锁冲突的概率最高，并发度最 低，MyISAM和 InnoDB引擎都支持表级锁。
- **行级锁：**  MySQL中锁定 粒度最小的⼀种锁，只针对当前操作的行进行加锁。 行级锁能大大减少数据库操作的冲突。其加锁粒度最小，并发度高，但加锁的开销也最大，加锁慢，会 出现死锁。



# mysql如何优化

mysql 数据库优化从两方面入手。
 **1.通过优化配置参数**
 如合适的 innodb 池大小，取消反向解析，合理的连接数，合理的超时时长，合理的相关 cache 等
 **2.通过操作的优化**
 如，合理的表结构，合理的索引，合理的查询语录(可通过分析慢查询日志找出可优化的，再通过 explain 去测试语句，找出可优化的点进行优化)。
 如果都有优化了还有瓶颈、最后就是分表、分库、扩硬件、主从读写分离。



[MySQL优化/面试，看这一篇就够了 - 掘金 (juejin.cn)](https://juejin.cn/post/6844903750839058446)

**如何优化：**

- 设计数据库时：数据库表、字段的设计，存储引擎
- 利用好MySQL自身提供的功能，如索引等
- 横向扩展：MySQL集群、负载均衡、读写分离
- SQL语句的优化（收效甚微）

**原则：尽可能选择小的数据类型和指定短的长度**

**原则：尽可能使用 not null。**

非`null`字段的处理要比`null`字段的处理高效些！且不需要判断是否为`null`。

`null`在MySQL中，不好处理，存储需要额外空间，运算也需要特殊的运算符。如`select null = null`和`select null <> null`（`<>`为不等号）有着同样的结果，只能通过`is null`和`is not null`来判断字段是否为`null`。

**原则：单表字段不宜过多**   二三十个就是极限了

**表设计三范式**

- 字段的原子性：字段不可再分割。
- 消除对主键的部分依赖：即在表中加上一个与业务逻辑无关的字段作为主键
- 消除对主键的传递依赖：将有依赖性字段的表拆分成两张或多张表

**存储引擎选择**

> 早期问题：如何选择MyISAM和Innodb？
>
> 现在不存在这个问题了，Innodb不断完善，从各个方面赶超MyISAM，也是MySQL默认使用的。

存储引擎Storage engine：MySQL中的数据、索引以及其他对象是如何存储的，是一套文件系统的实现。

**功能差异**

```
show engines
```

| Engine | Support | Comment                                                      |
| ------ | ------- | ------------------------------------------------------------ |
| InnoDB | DEFAULT | **Supports transactions, row-level locking, and foreign keys** |
| MyISAM | YES     | **MyISAM storage engine**                                    |

**存储差异**

|                                                              | MyISAM                                            | Innodb                                   |
| ------------------------------------------------------------ | ------------------------------------------------- | ---------------------------------------- |
| 文件格式                                                     | 数据和索引是分别存储的，数据`.MYD`，索引`.MYI`    | 数据和索引是集中存储的，`.ibd`           |
| 文件能否移动                                                 | 能，一张表就对应`.frm`、`MYD`、`MYI`3个文件       | 否，因为关联的还有`data`下的其它文件     |
| 记录存储顺序                                                 | 按记录插入顺序保存                                | 按主键大小有序插入                       |
| 空间碎片（删除记录并`flush table 表名`之后，表文件大小不变） | 产生。定时整理：使用命令`optimize table 表名`实现 | 不产生                                   |
| 事务                                                         | 不支持                                            | 支持                                     |
| 外键                                                         | 不支持                                            | 支持                                     |
| 锁支持（锁是避免资源争用的一个机制，MySQL锁对用户几乎是透明的） | 表级锁定                                          | 行级锁定、表级锁定，锁定力度小并发能力高 |



**选择依据**

如果没有特别的需求，使用默认的`Innodb`即可。

MyISAM：以读写插入为主的应用程序，比如博客系统、新闻门户网站。

Innodb：更新（删除）操作频率也高，或者要保证数据的完整性；并发量高，支持事务和外键保证数据完整性。比如OA自动化办公系统。



# 索引

> 关键字与数据的映射关系称为索引（==包含关键字和对应的记录在磁盘中的地址==）。关键字是从数据当中提取的用于标识、检索数据的特定内容。

## MySQL中索引类型

> **普通索引**（`key`），**唯一索引**（`unique key`），**主键索引**（`primary key`），**全文索引**（`fulltext key`）

三种索引的索引方式是一样的，只不过对索引的关键字有不同的限制：

- 普通索引：对关键字没有限制
- 唯一索引：要求记录提供的关键字不能重复
- 主键索引：要求关键字唯一且不为null

# 压测工具mysqlslap

安装MySQL时附带了一个压力测试工具`mysqlslap`（位于`bin`目录下）

## 自动生成sql测试

```
C:\Users\zaw>mysqlslap --auto-generate-sql -uroot -proot
mysqlslap: [Warning] Using a password on the command line interface can be insecure.
Benchmark
        Average number of seconds to run all queries: 1.219 seconds
        Minimum number of seconds to run all queries: 1.219 seconds
        Maximum number of seconds to run all queries: 1.219 seconds
        Number of clients running queries: 1
        Average number of queries per client: 0
```





# 大表优化

当MySQL单表记录数过⼤时，数据库的CRUD性能会明显下降，⼀些常⻅的优化措施如下：



## 限定数据范围



## 读写分离



## 垂直分表

也就是“大表拆小表”，基于列字段进行的。一般是表中的字段较多，将不常用的， 数据较大，长度较长（比如text类型字段）的拆分到“扩展表“。 一般是针对那种几百列的大表，也避免查询时，数据量太大造成的“跨页”问题。

## 水平分表

水平分表

针对数据量巨大的单张表（比如订单表），按照某种规则（RANGE,HASH取模等），切分到多张表里面去。 但是这些表还是在同一个库中，所以库级别的数据库操作还是有IO瓶颈。不建议采用。

水平分库分表

将单张表的数据切分到多个服务器上去，每个服务器具有相应的库与表，只是表中数据集合不同。 水平分库分表能够有效的缓解单机和单库的性能瓶颈和压力，突破IO、连接数、硬件资源等的瓶颈。

水平分库分表切分规则

1. RANGE

   从0到10000一个表，10001到20000一个表；

2. HASH取模

   一个商场系统，一般都是将用户，订单作为主表，然后将和它们相关的作为附表，这样不会造成跨库事务之类的问题。 取用户id，然后hash取模，分配到不同的数据库上。

3. 地理区域

   比如按照华东，华南，华北这样来区分业务，七牛云应该就是如此。

4. 时间

   按照时间切分，就是将6个月前，甚至一年前的数据切出去放到另外的一张表，因为随着时间流逝，这些表的数据 被查询的概率变小，所以没必要和“热数据”放在一起，这个也是“冷热数据分离”。

   

- ### **分库分表总结**

  - 分库分表，首先得知道瓶颈在哪里，然后才能合理地拆分(分库还是分表?水平还是垂直?分几个?)。且不可为了分库分表而拆分。
  - 选key很重要，既要考虑到拆分均匀，也要考虑到非partition key的查询。
  - 只要能满足需求，拆分规则越简单越好。

  
  