package com.wky.book.service;

import com.wky.book.common.response.BaseResult;
import com.wky.book.common.response.ResultUtils;
import com.wky.book.response.UserMoneyStatisticsRes;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * 文件上传 2022-05-03 01:01:30
 */
public interface UploadFileService {

    List<String> uploads(String base64,String uploadPath, String businessId, HttpServletRequest request);

    List<String> uploads(MultipartFile[] uploadFiles,String uploadPath, String businessId, HttpServletRequest request);

    /**
     * 将XSSF类型xlsx文件上传到ftp服务器
     *
     * @param title     标题名称
     * @param shellName shell名称
     * @param uploadPath 上传到哪个工作目录
     * @param data      数据
     * @return 可访问路径
     */
    List<String>  exportExcelXls(String title, String shellName,String uploadPath, List<?> data,Class clazz,String bookId) throws IOException;

    List<String>  exportExcelImage(String title, String shellName,String uploadPath, List<?> data,Class clazz,String bookId) throws IOException;
}
