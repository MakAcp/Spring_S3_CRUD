package com.example.S3_Bucket_demo.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    

    String fileupload(MultipartFile file);

    public byte[] downloadFile(final String keyName);

    public void deleteFile(String keyName);
}
