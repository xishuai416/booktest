https://blog.csdn.net/weixin_39412946/article/details/111573290


防火墙端口开放

firewall-cmd --zone=public --add-port=6379/tcp --permanent
#命令含义
--zone #作用域 
--add-port=3306/tcp #添加端口，格式为：端口/通讯协议 
--permanent #永久生效

重启防火墙
systemctl restart firewalld.service