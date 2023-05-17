package com.wky.book;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@MapperScan("com.wky.book.mapper")
@SpringBootApplication
@EnableAspectJAutoProxy
public class BookkeepingParentApplication {
    private static final Logger logger = LoggerFactory.getLogger(BookkeepingParentApplication.class);

    public static void main(String[] args) {

        SpringApplication.run(BookkeepingParentApplication.class, args);
        logger.info("服务启动成功");
    }

}
