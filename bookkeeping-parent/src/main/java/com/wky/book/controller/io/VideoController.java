package com.wky.book.controller.io;

import cn.hutool.Hutool;
import cn.hutool.core.io.FileUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author weikaiyu
 * @date 2022/4/29 12:47
 */
@RestController
@RequestMapping("/api/book")
public class VideoController {


    @Autowired
    RestTemplate restTemplate;

    // https://cdnvideo.ccsazx.net/d9077b2c8ff7499085346a66ff4e1611/100000000117_m3u8/pieces_0.ts    周曼琳
// https://cdnvideo.ccsazx.net/4d505e3382ce4f25aaa988ca07601b5e/100000000117_m3u8/pieces_2.ts    程长进
    // TODO: 2022/4/29  完整url模板:https://cdnvideo.ccsazx.net/d9077b2c8ff7499085346a66ff4e1611/100000000117_m3u8/pieces_756.ts
    // 前缀
    private static String urlPrefix = "https://cdnvideo.ccsazx.net/d9077b2c8ff7499085346a66ff4e1611/100000000117_m3u8/pieces_";
    //  后缀
    private static String urlSuffix = ".ts";


    @GetMapping("/pull")
    @ApiOperation(value = "分页查询积分任务设置表", notes = "分页查询积分任务设置表")
    public void findPage() throws IOException {
        //        根据浏览器请求二进制视频流得知，请求url长度1开始， 周曼琳 788结束   程长进740结束
        for (int i = 0; i < 789; i++) {
            String requestUrl = urlPrefix + i + urlSuffix;
            HttpHeaders requestHeaders = new HttpHeaders();
            // 资源权限不足，需要在请求头设置Referer的值为域名https://ccsazx.net/
            requestHeaders.set("Referer", "https://ccsazx.net/");
            HttpEntity requestEntity = new HttpEntity(requestHeaders);
            HttpEntity<Resource> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.GET, requestEntity, Resource.class);
            System.err.println("文件写入第" + i + "段。" + "剩余" + (789 - i) + "段..................");
            InputStream inputStream = responseEntity.getBody().getInputStream();
            int len = -1;
            byte[] buffer = new byte[5120]; // 1KB
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            while ((len = bufferedInputStream.read(buffer)) != -1) { //当等于-1说明没有数据可以读取了
                FileUtil.writeBytes(buffer, new File("F:\\workPath\\周曼琳.ts"), 0, len, true);
            }
            bufferedInputStream.close();
        }
        System.out.println("下载完成，程序结束。");
    }


}
