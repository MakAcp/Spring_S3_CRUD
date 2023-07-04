package com.example.S3_Bucket_demo.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.iterable.S3Objects;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;

@Service
public class FileServiceImpl implements FileService{

    @Autowired
    private AmazonS3Client awsS3Client;

    

    @Override
    public String fileupload(MultipartFile file) {
        
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String key = UUID.randomUUID().toString() + extension;
        
        ObjectMetadata metaData = new ObjectMetadata();
        metaData.setContentLength(file.getSize());
        metaData.setContentType(file.getContentType());
        System.out.println(key);
        try{
            awsS3Client.putObject("my-test-s3-bucket-093", key, file.getInputStream(), metaData);
        }catch(IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Error");
        }

        awsS3Client.setObjectAcl("my-test-s3-bucket-093", key, CannedAccessControlList.PublicRead);
    
        return awsS3Client.getResourceUrl("my-test-s3-bucket-093", key);
    }

    @Override
    public byte[] downloadFile(String etaq) {
        byte[] content = null;
        //S3Object s3Object = awsS3Client.getObject("my-test-s3-bucket-093",keyName);
       // S3Object s3Object = awsS3Client.getObject("my-test-s3-bucket-093", keyName);
        String[] key = new String[1];
        AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();
        S3Objects.inBucket(s3, "my-test-s3-bucket-093").forEach((S3ObjectSummary objectsummary)->{
            String Etag = objectsummary.getETag();
            if(etaq.equals(Etag)){
            key[0]= objectsummary.getKey();
        }
        });
        S3Object s3Object = awsS3Client.getObject("my-test-s3-bucket-093", key[0]);                  
        S3ObjectInputStream stream = s3Object.getObjectContent();
        try {
            content = IOUtils.toByteArray(stream);
            s3Object.close();
        }catch(IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Error");
        }
        return content;
    }  

    
    public void deleteFile(String etaq){
        String[] key = new String[1];
        AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();
        S3Objects.inBucket(s3, "my-test-s3-bucket-093").forEach((S3ObjectSummary objectsummary)->{
            String Etag = objectsummary.getETag();
            if(etaq.equals(Etag)){
            key[0]= objectsummary.getKey();
        }
        });
        try{
        awsS3Client.deleteObject("my-test-s3-bucket-093",key[0]) ;
        }catch(IllegalArgumentException exception){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Error");
        }
    }
    
    
}
