参考：https://blog.csdn.net/Aplumage/article/details/120266836

1、ftp启动

service vsftpd start


2、ftp重启

service vsftpd restart


3、ftp 用户名密码：ftpuser/Wky1314*


4、修改了 vsftpd.conf配置文件，将存储路径指定为 /home/ftpuser



创建一个用户
1、新增用户并指定工作路径 useradd book /home/ftp/book
 
2、修改密码 passwd newPd



查看命令：vim /etc/passwd 
发现 book用户已经指定了/home/ftp/book为工作目录 book:x:1005:1005::/home/ftp/book:/bin/bash
以后使用该用户文件就会上传到工作目录



nginx博客教程：https://www.cnblogs.com/cainiaoyige1/p/15785911.html
安装nginx，映射目录为/home/ftp/book

1）、ps：安装nginx所需的环境 :yum install gcc-c++ 报错问题
解决：
执行如下命令即可：
wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
sed -i  's/$releasever/7/g' /etc/yum.repos.d/CentOS-Base.repo
yum repolist 



2）、
启动nginx：/usr/local/nginx/sbin/nginx
重启nginx： nginx -s reload



api服务启动命令：
nohup java -jar bookkeeping-parent-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod >/null 2>&1 &