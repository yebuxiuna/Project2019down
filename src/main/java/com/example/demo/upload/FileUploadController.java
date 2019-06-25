package com.example.demo.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/Upload")
public class FileUploadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private FileUpAndDownService fileUpAndDownService;

    @RequestMapping(value = "/SetFileUpload", method = RequestMethod.POST)
    public List<ResponseResult> setFileUpload(@RequestBody Map map) {
        List<ResponseResult> list = new ArrayList<>();
        String img[] = map.get("img").toString().split("    ");
        for (int i = 0; i < img.length; i++) {
            MultipartFile file = null;
            ResponseResult result = new ResponseResult();
            try {
                file = BASE64DecodedMultipartFile.base64ToMultipart(img[i]);
                LOGGER.info(file.getName());
                Map<String, Object> resultMap = upload(file);
                if (!IStatusMessage.SystemStatus.SUCCESS.getMessage().equals(resultMap.get("result"))) {
                    result.setCode(IStatusMessage.SystemStatus.ERROR.getCode());
                    result.setMessage((String) resultMap.get("msg"));
                    list.add(result);
                }
                result.setData(resultMap);
            } catch (ServiceException e) {
                e.printStackTrace();
                LOGGER.error(">>>>>>图片上传异常，e={}", e.getMessage());
                result.setCode(IStatusMessage.SystemStatus.ERROR.getCode());
                result.setMessage(IStatusMessage.SystemStatus.ERROR.getMessage());
            }
            LOGGER.info(">>>>>>>>>>>"+result.getMessage());
            list.add(result);
        }
        return list;
    }

    private Map<String, Object> upload(MultipartFile file) throws ServiceException {
        Map<String, Object> returnMap = new HashMap<>();
        try {
            if (!file.isEmpty()) {
                Map<String, Object> picMap = fileUpAndDownService.uploadPicture(file);
                if (IStatusMessage.SystemStatus.SUCCESS.getMessage().equals(picMap.get("result"))) {
                    return picMap;
                } else {
                    returnMap.put("result", IStatusMessage.SystemStatus.ERROR.getMessage());
                    returnMap.put("msg", picMap.get("result"));
                }
            } else {
                LOGGER.info(">>>>>>上传图片为空文件");
                returnMap.put("result", IStatusMessage.SystemStatus.ERROR.getMessage());
                returnMap.put("msg", IStatusMessage.SystemStatus.FILE_UPLOAD_NULL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(IStatusMessage.SystemStatus.ERROR.getMessage());
        }
        return returnMap;
    }
}
