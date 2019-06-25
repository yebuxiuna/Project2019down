package com.example.demo.controller;

import com.example.demo.dao.InvitationImageDao;
import com.example.demo.entity.InvitationImage;
import com.example.demo.msg.ReturnMsg;
import com.example.demo.upload.*;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/Upload")
public class FileUploadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private MessageProperties config;

    @Autowired
    private FileUpAndDownService fileUpAndDownService;

    @Autowired
    private InvitationImageDao invimageDao;

    @RequestMapping(value = "/SetImageUpload", method = RequestMethod.POST)
    public ReturnMsg setImageUpload(@RequestBody Map map) {
        ReturnMsg result = null;
        if (map.get("token") != null && WebSocketServer.getWebSocketServer(map.get("token").toString()) != null) {
            MultipartFile file = null;
            String img = map.get("img").toString();
            result = new ReturnMsg();
            try {
                file = BASE64DecodedMultipartFile.base64ToMultipart(img);
                Map<String, Object> resultMap = upload(file);
                if (!IStatusMessage.SystemStatus.SUCCESS.getMessage().equals(resultMap.get("result"))) {
                    result.setCode(Flag.SEND_IMG_SUCCEED);
                }
//                result.setMsg(resultMap);
                result.setMsg("S");
                InvitationImage image = new InvitationImage();
                image.setId((Integer) map.get("invitationid"));
                image.setImagePath(resultMap.get("path").toString());
                invimageDao.save(image);
            } catch (ServiceException e) {
                e.printStackTrace();
                LOGGER.error(">>>>>>图片上传异常，e={}", e.getMessage());
                result.setCode(Flag.SEND_IMG_ERROR);
//                result.setMsg(IStatusMessage.SystemStatus.ERROR.getMessage());
                result.setMsg("F");
            }
        }
        return result;
    }

    @RequestMapping(value = "/SetFileUpload",method = {RequestMethod.POST})
    public ReturnMsg setFileUpload(@RequestBody Map map) {
        ReturnMsg result = null;
        FileWriter fwriter = null;
        if (map.get("token") != null && WebSocketServer.getWebSocketServer(map.get("token").toString()) != null) {
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            // 获得文件后缀名称
            String imageName = "txt";
            String fileName = uuid + "." + imageName;
            // 年月日文件夹
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String basedir = sdf.format(new Date());
            String path = config.getUpPath() + "/projectText" + "/" + basedir + "/";
            // 如果目录不存在则创建目录
            try {
                File oldFile = new File(path);
                if (!oldFile.exists()) {
                    oldFile.mkdirs();
                }
                oldFile = new File(path + fileName);
                if (!oldFile.exists()) {
                    oldFile.createNewFile();
                }
                // true表示不覆盖原来的内容，而是加到文件的后面。若要覆盖原来的内容，直接省略这个参数就好
                fwriter = new FileWriter(path + fileName);
                fwriter.write(map.get("content").toString());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fwriter.flush();
                    fwriter.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
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
