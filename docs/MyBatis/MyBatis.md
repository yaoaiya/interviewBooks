# mybatis中#{} 和 ${}的区别

#和$在预编译处理中是不一样的。#类似jdbc中的PreparedStatement，对于传入的参数，在预处理阶段会使用?代替，比如：
select * from student where id = ?;

待真正查询的时候即在数据库管理系统中（DBMS）才会代入参数。
而${}则是简单的替换，如下：
select * from student where id = 2;
