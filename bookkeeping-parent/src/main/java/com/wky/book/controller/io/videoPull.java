package com.wky.book.controller.io;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author weikaiyu
 * @date 2022/4/29 11:27
 */
@Component
public class videoPull {



    // TODO: 2022/4/29  完整url模板:https://cdnvideo.ccsazx.net/d9077b2c8ff7499085346a66ff4e1611/100000000117_m3u8/pieces_756.ts

    // 前缀
    private static String urlPrefix = "https://cdnvideo.ccsazx.net/d9077b2c8ff7499085346a66ff4e1611/100000000117_m3u8/pieces_";
    //  后缀
    private static String urlSuffix = ".ts";

    public static void main(String[] args) {
//        根据浏览器请求二进制视频流得知，请求url长度1开始， 788结束
        for (int i = 1; i < 2; i++) {
            String requestUrl = urlPrefix + i + urlSuffix;
//            RestTemplate restTemplate = ApplicationContextUtil.getBean(RestTemplate.class);
//            Object forObject = restTemplate.getForObject(requestUrl, Object.class);
//            System.out.println(requestUrl);
        }

    }



    /**
     * 追加文件：使用FileOutputStream，在构造FileOutputStream时，把第二个参数设为true
     *
     * @param file   文件路径
     * @param conent 要写入的内容
     */
    public static void method1(String file, String conent) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            out.write(conent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
