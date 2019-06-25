package com.example.demo.upload;

import com.example.demo.msg.ReturnMsg;
import com.example.demo.util.Flag;
import com.example.demo.websocket.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/Upload")
public class FileUploadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private FileUpAndDownService fileUpAndDownService;

    @RequestMapping(value = "/SetFileUpload", method = RequestMethod.POST)
    public ReturnMsg setFileUpload(@RequestBody Map map) {
        ReturnMsg result = null;
        if(map.get("token") != null || WebSocketServer.getWebSocketServer(map.get("token").toString()) != null){
            MultipartFile file = null;
            String img = map.get("img").toString();
            result = new ReturnMsg();
            try {
                file = BASE64DecodedMultipartFile.base64ToMultipart(img);
                Map<String, Object> resultMap = upload(file);
                if (!IStatusMessage.SystemStatus.SUCCESS.getMessage().equals(resultMap.get("result"))) {
                    result.setCode(Flag.SEND_IMG_SUCCEED);
                }
                result.setMsg(resultMap);
            } catch (ServiceException e) {
                e.printStackTrace();
                LOGGER.error(">>>>>>图片上传异常，e={}", e.getMessage());
                result.setCode(Flag.SEND_IMG_ERROR);
                result.setMsg(IStatusMessage.SystemStatus.ERROR.getMessage());
            }
        }
        return result;
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
