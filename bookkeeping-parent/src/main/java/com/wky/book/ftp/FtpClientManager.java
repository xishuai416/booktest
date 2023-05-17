package com.wky.book.ftp;


import cn.hutool.core.collection.ListUtil;
import com.wky.book.common.context.ApplicationContextUtil;
import com.wky.book.common.context.UserTokenContextHolder;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import sun.net.www.content.image.jpeg;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FtpClientManager {
    private static Logger logger = LoggerFactory.getLogger(FtpClientManager.class);

    @Autowired
    ApplicationContextUtil applicationContextUtil;

    @Value("${ftp.ip}")
    private String ip;

    @Value("${ftp.port}")
    private Integer port;

    @Value("${ftp.username}")
    private String username;

    @Value("${ftp.password}")
    private String password;


    @Value("${myservice.url}")
    private String myservice;

    private FTPClient ftpClient = null;

    /**
     * book用户的在linux的工作路径是 /home/ftp/book，
     * 调用 ftpClient.changeWorkingDirectory切换目录时，要linux的全路径/home/ftp/book/xxx（xxx是个传进来的形参）
     */
    public String prefixPath = "/home/ftp/book";

    public FtpClientManager() {
    }

    private FTPClient initClient() {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            int reply = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                logger.error("FTP连接失败");
                throw new RuntimeException("FTP连接失败！");
            }
            logger.info("success to connect ftp server");
            return ftpClient;
        } catch (IOException e) {
            logger.error("faild to connect ftp server because " + e.getMessage());
            throw new RuntimeException("FTP连接失败！");
        }
    }

    public static List<String> uploadFTP(MultipartFile img, String path, String businessId) throws IOException {
        List<String> imagesUrls = new ArrayList<>();
        FtpClientManager ftpClientManager = ApplicationContextUtil.getBean(FtpClientManager.class);
        FTPClient ftpClient = ftpClientManager.initClient();
        if (StringUtils.isNotBlank(path)) {
            ftpClient.changeWorkingDirectory(ftpClientManager.prefixPath + path);
        } else {
            ftpClient.changeWorkingDirectory(ftpClientManager.prefixPath + "default");
        }
        //storeFile 上传FTP 文件命名格式，uuid+"_"+操作人id+"_"+业务主键id+"_"+文件名称
        Long userId = UserTokenContextHolder.getUserTokenVOByToken().getUserId();
        String originalFilename = UUID.randomUUID().toString() + "_" + userId + "_" + businessId + "_" + img.getOriginalFilename();
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        if (!ftpClient.storeFile(originalFilename, img.getInputStream())) {
            throw new RuntimeException("FTP上传失败！");
        } else {
            logger.info(originalFilename + "上传成功");
        }
        if (null != path) {
            imagesUrls.add(ftpClientManager.myservice + path + File.separator + originalFilename);
        } else {
            imagesUrls.add(ftpClientManager.myservice + File.separator + originalFilename);
        }
        return imagesUrls;
    }

    public static List<String> uploadFTP(MultipartFile[] imgs, String path, String businessId) throws IOException {
        List<String> imagesUrls = new ArrayList<>();
        FtpClientManager ftpClientManager = ApplicationContextUtil.getBean(FtpClientManager.class);
        FTPClient ftpClient = ftpClientManager.initClient();
        if (StringUtils.isNotBlank(path)) {
            ftpClient.changeWorkingDirectory(ftpClientManager.prefixPath + path);
        } else {
            ftpClient.changeWorkingDirectory(ftpClientManager.prefixPath + "default");
        }
        try {
            for (MultipartFile img : imgs) {
                InputStream inputStream = img.getInputStream();
                //storeFile 上传FTP 文件命名格式，uuid+"_"+操作人id+"_"+业务主键id+"_"+文件名称
                Long userId = UserTokenContextHolder.getUserTokenVOByToken().getUserId();
                String originalFilename = UUID.randomUUID().toString() + "_" + userId + "_" + businessId + "_" + img.getOriginalFilename();
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                if (!ftpClient.storeFile(originalFilename, inputStream)) {
                    ftpClient.logout();
                    throw new RuntimeException("FTP上传失败！");
                } else {
                    logger.info(img.getOriginalFilename() + "上传成功");
                }
                if (null != path) {
                    imagesUrls.add(ftpClientManager.myservice + path + File.separator + originalFilename);
                } else {
                    imagesUrls.add(ftpClientManager.myservice + File.separator + originalFilename);
                }
            }
        } finally {
            ftpClient.logout();
        }

        return imagesUrls;

    }


    public MultipartFile fileToMultipartFile(File file, String contentType) {
        FileItem fileItem = createFileItem(file, contentType);
        MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
        return multipartFile;
    }


    private static FileItem createFileItem(File file, String contentType) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem item = factory.createItem("textField", contentType, true, file.getName());
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        try {
            FileInputStream fis = new FileInputStream(file);
            OutputStream os = item.getOutputStream();
            while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return item;
    }

    public List<String> uploadFTP(File fileImg, String param, String path, String businessId) throws IOException {
        String upperCaseType = param.toUpperCase();
        String contentType = "multipart/form-data";
        if (StringUtils.isEmpty(param)) {
            contentType = "text/plan";
        }
        if (upperCaseType.equals("JPG")) {
            contentType = "image/jpeg";
        }
        if (upperCaseType.equals("PNG")
        ) {
            contentType = "image/jpeg";
        }
        if (upperCaseType.equals("GIF")) {
            contentType = "image/jpeg";
        }
        if (upperCaseType.equals("xlsx")) {
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }
        if (upperCaseType.equals("xls")) {
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }
        return uploadFTP(fileToMultipartFile(fileImg, contentType), path, businessId);
    }
}