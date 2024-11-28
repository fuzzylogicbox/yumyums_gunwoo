package com.yum.yumyums.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface S3UploaderService {
    public String upload(MultipartFile multipartFile, String dirName) throws IOException;
}
