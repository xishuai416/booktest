package com.wky.book.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.export.styler.ExcelExportStylerBorderImpl;
import cn.afterturn.easypoi.excel.export.styler.ExcelExportStylerColorImpl;
import com.spire.xls.Worksheet;
import com.wky.book.common.exception.BasicInfoException;
import com.wky.book.common.exception.BasicInfoStatusEnum;
import com.wky.book.common.response.BaseResult;
import com.wky.book.common.response.ResultUtils;
import com.wky.book.ftp.FtpClientManager;
import com.wky.book.response.UserMoneyStatisticsRes;
import com.wky.book.service.UploadFileService;
import com.wky.book.util.Base64ToMultipartFile;
import com.wky.book.util.MoneyConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class UploadFileServiceImpl implements UploadFileService {

    @Autowired
    FtpClientManager ftpClientManager;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");

    @Override
    public List<String> uploads(String base64, String path, String businessId, HttpServletRequest request) {

        if (base64.length() > 0) {
            try {
                final String[] base64Array = base64.split(",");
                // base64转为流
                String dataUir = base64Array[0];
                String data = base64Array[1];
                MultipartFile multipartFile = new Base64ToMultipartFile(data, dataUir);
                return ftpClientManager.uploadFTP(multipartFile, path, businessId);
            } catch (IOException e) {
                log.error("文件上传失败：{}", e.getMessage());
                throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                        "文件上传失败");
            }
        } else if (base64.length() == 0) {
            log.error("请选择文件");
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "请选择文件");
        }
        throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                "上传失败");
    }

    @Override
    public List<String> uploads(MultipartFile[] uploadFiles, String path, String businessId, HttpServletRequest request) {
        if (uploadFiles.length > 0) {
            try {
                return ftpClientManager.uploadFTP(uploadFiles, path, businessId);
            } catch (IOException e) {
                log.error("文件上传失败：{}", e.getMessage());
                throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                        "文件上传失败");
            }
        } else if (uploadFiles.length == 0) {
            log.error("请选择文件");
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "请选择文件");
        }
        throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                "上传失败");
    }

    public String tempBuild(String title, String shellName, List<?> data, Class clazz) {
        String realPath = System.getProperty("user.dir");
        File realPathUrl = new File(realPath);
        if (!realPathUrl.exists()) {
            realPathUrl.mkdirs();
        }
        ExportParams exportParams = new ExportParams(title, shellName);
        exportParams.setType(ExcelType.XSSF);
        exportParams.setStyle(ExcelExportStylerColorImpl.class);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams,
                clazz, data);
        FileOutputStream fos = null;
        // 临时文件
        String excelUrl = realPath + File.separator + System.currentTimeMillis() + ".xlsx";

        try {
            fos = new FileOutputStream(excelUrl);
            workbook.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return excelUrl;
    }

    @Override
    public List<String> exportExcelImage(String title, String shellName, String path, List<?> data, Class clazz, String businessId) throws IOException {
        String realPath = System.getProperty("user.dir");
        String excelUrl = null;
        File fileImg = null;
        List<String> list = null;
        try {
            com.spire.xls.Workbook wb = new com.spire.xls.Workbook();

            excelUrl = tempBuild(title, shellName, data, clazz);
//        //文件所在位置
            wb.loadFromFile(excelUrl);
//        // 第0个shell-
            Worksheet sheet = wb.getWorksheets().get(0);
            BufferedImage bufferedImage = null;
            Locale newLocale = Locale.ROOT;
            Locale.setDefault(newLocale);
            try {
                //保存到图片
                bufferedImage = sheet.toImage(1, 1, sheet.getLastRow(), sheet.getLastColumn());
            } catch (Exception e) {
                e.printStackTrace();
                log.error("excel转图片错误：{}", e.getMessage());
                throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(), "名字不允许特殊符号，导出失败！");
            }
            try {
                fileImg = new File(realPath + File.separator, "temp.png");
                ImageIO.write(bufferedImage, "PNG", fileImg);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("bufferedImage写入File失败：{}", e.getMessage());
                throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(), "下载失败，请尝试");
            }
            list = ftpClientManager.uploadFTP(fileImg, "png", path, businessId);
        } finally {
            // 删除临时文件
            if (null != excelUrl)
                new File(excelUrl).delete();
            if (null != fileImg)
                fileImg.delete();
        }
        return list;
    }


    @Override
    public List<String> exportExcelXls(String title, String shellName, String path, List<?> data, Class clazz, String businessId) throws IOException {
        String excelUrl = null;
        List<String> list;
        try {
            excelUrl = tempBuild(title, shellName, data, clazz);
            list = ftpClientManager.uploadFTP(new File(excelUrl), "png", path, businessId);
        } finally {
            // 删除临时文件
            if (null != excelUrl)
                new File(excelUrl).delete();
        }
        return list;
    }


}
