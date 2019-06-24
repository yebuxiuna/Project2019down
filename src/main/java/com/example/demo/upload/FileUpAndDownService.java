package com.example.demo.upload;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileUpAndDownService {
    Map<String, Object> uploadPicture(MultipartFile file) throws ServiceException;
}
