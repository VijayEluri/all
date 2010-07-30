源文件在src/java下
dist/FileServer.war是编译后的war包

在使用前，需要配置以下2个配置文件

WEB-INF/classes/saperion.properties
WEB-INF/web.xml

在saperion.properties中，需要修改loadbalancing.servers属性，指向saperion服务器的IP地址

在web.xml中，修改servlet的初始化参数：

user: 用于访问saperion的用户名
password: 用于访问saperion的密码

将FileServer.war部署到/FileServer后，访问方式如下：

http://host:port/FileServer/Downloader?dcc=<dcc name>&field=<field name>&value=<value>

<dcc name>为待查询的Definition名称
<field name>为待查询的字段名称
<value>为查询的值

比如

http://host:port/FileServer/Downloader?dcc=Lesson1&field=CUSTOMERNO&value=11

会形成这样的查询语句：

SELECT ... FROM Lesson1 WHERE CUSTOMERNO='11'

返回的结果是文档的数据流，文件名为file.bin