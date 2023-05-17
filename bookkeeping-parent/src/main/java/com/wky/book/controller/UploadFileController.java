package com.wky.book.controller;

import com.wky.book.common.response.BaseResult;
import com.wky.book.common.response.ResultUtils;
import com.wky.book.service.UploadFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
@Slf4j
public class UploadFileController {


    @Autowired
    UploadFileService uploadFileService;


    @PostMapping("/uploads")
    public BaseResult<List<String>> upload(@RequestParam("file") MultipartFile[] uploadFiles,
                                           @RequestParam(value = "uploadPath", required = false) String uploadPath,
                                           @RequestParam(value = "businessId", required = false) String businessId,
                                           HttpServletRequest request) {
        return ResultUtils.success(uploadFileService.uploads(uploadFiles, uploadPath, businessId,request));
    }

}
