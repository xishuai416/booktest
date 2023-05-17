package com.wky.book;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

//@SpringBootTest
class BookkeepingParentApplicationTests {
//
//    @Test
//    void contextLoads() {
//    }
//
//    public static void main(String[] args) {
//
//        FastAutoGenerator.create("jdbc:mysql://127.0.0.1:3306/book-platform?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&allowMultiQueries=true",
//                        "root",
//                        "admin")
//                .globalConfig(builder -> {
//                    builder.author("wky") // 设置作者
//                            .enableSwagger() // 开启 swagger 模式
//                            .fileOverride() // 覆盖已生成文件
//                            .outputDir("E:\\workpath\\bookkeeping\\bookkeeping-parent\\src\\main\\java"); // 指定输出目录
//                })
//                .packageConfig(builder -> {
//                    builder.parent("com.wky") // 设置父包名
//                            .moduleName("book") // 设置父包模块名
//                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "E:\\workpath\\bookkeeping\\bookkeeping-parent\\src\\main\\resources\\mapper")); // 设置mapperXml生成路径
//                })
//                .strategyConfig(builder -> {
//                    builder.addInclude("tb_book","tb_book_category","tb_book_money","tb_book_user","tb_category","tb_user") // 设置需要生成的表名
//                            .addTablePrefix("tb_", "c_"); // 设置过滤表前缀
//                })
//                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
//                .execute();
//    }
}
