package com.example.S3_Bucket_demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.S3_Bucket_demo.service.FileServiceImpl;

@RestController
@RequestMapping("/api/file")
public class Controller {
    
    @Autowired
	private FileServiceImpl awsS3Service;
	
	@PostMapping
	public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
		String publicURL = awsS3Service.fileupload(file);
		Map<String, String> response = new HashMap<>();
		response.put("publicURL", publicURL);
		return new ResponseEntity<Map<String, String>>(response, HttpStatus.CREATED);
	}


    @GetMapping
	public ResponseEntity<ByteArrayResource> uploadFile(@RequestParam("etag") final String etag) {
		byte[] data = awsS3Service.downloadFile(etag);

        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
            .ok()
            .contentLength(data.length)
            .header("Content-type", "application/octet-stream")
            .header("Content-disposition", "attachment; filename=\"" + etag + "\"")
            .body(resource);
	}

    @DeleteMapping
    @ResponseBody
	public ResponseEntity<String> deleteFile(@RequestParam("etag") final String etag) {
		awsS3Service.deleteFile(etag);
        return new ResponseEntity<String>(" file :" + etag + " is deleted ", HttpStatus.OK);
	}


}
