package com.example.demo.controller;

import com.example.demo.dao.*;
import com.example.demo.entity.*;
import com.example.demo.msg.InvitationMsg;
import com.example.demo.msg.ReturnMsg;
import com.example.demo.result.ResultVO;
import com.example.demo.result.ResultVOUtil;
import com.example.demo.upload.*;
import com.example.demo.util.Flag;
import com.example.demo.websocket.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/Invitation")
public class InvitationController {
    private Logger logger = LoggerFactory.getLogger(InvitationController.class);
    @Autowired
    private MessageProperties config;

    @Autowired
    private FileUpAndDownService fileUpAndDownService;

    @Autowired
    private InvitationImageDao invimageDao;

    @Autowired
    private InvitationInfoDao infoDao;

    @Autowired
    private InvitationPartticularsDao partticularsDao;

    @Autowired
    private UserinfoDao userinfoDao;

    @Autowired
    private ShanChatDao shanChatDao;

    /**
     * 用户发送帖子
     *
     * @param map token  content//文本内容  id//发帖人id  channelid//帖子所在频道  img//图片base64  time//发帖时间
     * @return
     */
    @RequestMapping(value = "/SetFileUpload", method = {RequestMethod.POST})
    public ResultVO setFileUpload(@RequestBody Map map) {
        ResultVO resultVO = new ResultVO();
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
                fwriter = new FileWriter(oldFile);
                String content = map.get("content").toString();
                fwriter.write(content);
                UserRegister register = userinfoDao.findById((Integer) map.get("id")).get();
                InvitationInfo info = new InvitationInfo();
                info.setUid(register.getId());
                info.setSendname(register.getUsername());
                info.setTime(map.get("time").toString());
                info.setChannelId((Integer) map.get("channelid"));
                if (content.length() <= 100) {
                    info.setDescription(content);
                } else {
                    info.setDescription(content.substring(0, 101));
                }
                InvitationInfo info1 = infoDao.save(info);
                InvitationParticulars particulars = new InvitationParticulars();
                particulars.setId(info1.getId());
                particulars.setParticulars(path + fileName);
                partticularsDao.save(particulars);
                String img[] = map.get("img").toString().split("    ");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < img.length; i++) {
                            setImageUpload(img[i], info1.getId());
                        }
                    }
                }).start();
                resultVO.setMsg("S");
                InvitationMsg invitationMsg = new InvitationMsg();
                invitationMsg.setInfo(info1);
                invitationMsg.setInvitationImages(invimageDao.findAllById(info1.getId()));
                resultVO.setData(invitationMsg);
                return resultVO;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fwriter.flush();
                    fwriter.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            return ResultVOUtil.error("error:no token");
        }
        return resultVO;
    }

    /**
     * 接收客户端发送过来的图片
     *
     * @param img 加密的base64
     * @param id  帖子id
     * @return
     */
    public ReturnMsg setImageUpload(String img, int id) {
        ReturnMsg result = null;
        MultipartFile file = null;
        result = new ReturnMsg();
        try {
            file = BASE64DecodedMultipartFile.base64ToMultipart(img);
            Map<String, Object> resultMap = upload(file);
            if(!resultMap.get("result").equals("F")){
                if (!IStatusMessage.SystemStatus.SUCCESS.getMessage().equals(resultMap.get("result"))) {
                    result.setCode(Flag.SEND_IMG_SUCCEED);
                }
//                result.setMsg(resultMap);
                result.setMsg("S");
                InvitationImage image = new InvitationImage();
                image.setId(id);
                image.setImagePath(resultMap.get("path").toString());
                InvitationImage image1 = invimageDao.save(image);
            }else{
                result.setCode(Flag.SEND_IMG_ERROR);
                result.setMsg("暂时只支持jpeg、png、jpg等格式");
            }
        } catch (ServiceException e) {
            e.printStackTrace();
            logger.error(">>>>>>图片上传异常，e={}", e.getMessage());
            result.setCode(Flag.SEND_IMG_ERROR);
//                result.setMsg(IStatusMessage.SystemStatus.ERROR.getMessage());
            result.setMsg("F");
        }
        return result;
    }

    /**
     * 保存用户上传图片
     *
     * @param file
     * @return
     * @throws ServiceException
     */
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
                logger.info(">>>>>>上传图片为空文件");
                returnMap.put("result", IStatusMessage.SystemStatus.ERROR.getMessage());
                returnMap.put("msg", IStatusMessage.SystemStatus.FILE_UPLOAD_NULL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(IStatusMessage.SystemStatus.ERROR.getMessage());
        }
        return returnMap;
    }

    /**
     * 获取帖子信息
     *
     * @param map token,num
     * @return
     */
    @RequestMapping(value = "/GetInvitation", method = {RequestMethod.POST})
    public ResultVO getInvitation(@RequestBody Map map) {
        ResultVO resultVO = new ResultVO();
        List<InvitationMsg> invitationMsgs = new ArrayList<>();
        if (map.get("token") != null && WebSocketServer.getWebSocketServer(map.get("token").toString()) != null) {
            List<InvitationInfo> infos = infoDao.findAll();
            Collections.reverse(infos);
            int count = (int) map.get("num");
            int num = infos.size();
            int start = 0;
            if(num>10*count){
                num = num-10*count;
                if(num > 10){
                    num = 10;
                }
                start = 10*count;
            }else{
                return ResultVOUtil.error("没有数据了");
            }
            int end = start+num;
            InvitationMsg msg = null;
            for (int i = start; i < end; i++) {
                msg = new InvitationMsg();
                msg.setInfo(infos.get(i));
                msg.setInvitationImages(invimageDao.findAllById(infos.get(i).getId()));
                invitationMsgs.add(msg);
            }
            resultVO.setMsg("S");
            resultVO.setData(invitationMsgs);
        }else{
            resultVO.setMsg("error:no token");
        }
        return resultVO;
    }

    /**
     * 获取帖子详情
     *
     * @param map token,id//帖子id
     * @return
     */
    @RequestMapping(value = "/GetInvitationParticulars")
    public ResultVO getInvitationParticulars(@RequestBody Map map) {
        ResultVO resultVO = new ResultVO();
        if (map.get("token") != null && WebSocketServer.getWebSocketServer(map.get("token").toString()) != null) {
            InvitationParticulars particulars = partticularsDao.findById((Integer) map.get("id")).get();
            resultVO.setMsg("S");
            File f = new File(particulars.getParticulars());
            String str = null;
            try {
                FileReader fre = new FileReader(f);
                BufferedReader bre = new BufferedReader(fre);
                str = "";
                while ((str += bre.readLine()) != null)    //●判断最后一行不存在，为空
                {
                }
                bre.close();
                fre.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            resultVO.setData(str);
        } else {
            resultVO.setMsg("F");
            resultVO.setData("error:no token");
        }
        return resultVO;
    }

    /**
     * 获取图片
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/GetImage", method = {RequestMethod.POST}, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getimage(@RequestBody Map map) {
        if (map.get("token") != null && WebSocketServer.getWebSocketServer(map.get("token").toString()) != null) {
            String path = map.get("img").toString();
            byte[] bytes = new byte[0];
            try {
                File file = new File(path);
                FileInputStream inputStream = new FileInputStream(file);
                bytes = new byte[inputStream.available()];
                inputStream.read(bytes, 0, inputStream.available());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bytes;
        }
        return null;
    }

    /**
     * 获取我的帖子信息
     *
     * @param map token,id,num
     * @return
     */
    @RequestMapping(value = "/GetMeInvitation", method = {RequestMethod.POST})
    public ResultVO getMeInvitation(@RequestBody Map map) {
        ResultVO resultVO = new ResultVO();
        List<InvitationMsg> invitationMsgs = new ArrayList<>();
        if (map.get("token") != null && WebSocketServer.getWebSocketServer(map.get("token").toString()) != null) {
            List<InvitationInfo> infos = infoDao.findAllById((Integer) map.get("id"));
            InvitationMsg msg = null;
            int count = (int) map.get("num");
            int num = infos.size();
            int start = 0;
            if(num>8*count){
                num = num-8*count;
                if(num > 8){
                    num = 8;
                }
                start = 8*count;
            }else{
                return ResultVOUtil.error("没有数据了");
            }
            int end = start+num;
            Collections.reverse(infos);
            for (int i = start; i < end; i++) {
                msg = new InvitationMsg();
                msg.setInfo(infos.get(i));
                msg.setInvitationImages(invimageDao.findAllById(infos.get(i).getId()));
                invitationMsgs.add(msg);
            }
            resultVO.setMsg("S");
            resultVO.setData(invitationMsgs);
        }else{
            resultVO.setMsg("error:no token");
        }
        return resultVO;
    }

    /**
     * 获取闪聊信息
     * @param map  token
     * @return
     */
    @RequestMapping(value = "/GetChat",method = {RequestMethod.POST})
    public ResultVO getChat(@RequestBody Map map){
        ResultVO resultVO = new ResultVO();
        if (map.get("token") != null && WebSocketServer.getWebSocketServer(map.get("token").toString()) != null) {
            List<ShanChat> shanChats = shanChatDao.findAll();
            resultVO.setMsg("S");
            logger.info("!!!!!!!!!!"+shanChats.size());
            resultVO.setData(shanChats);
            return resultVO;
        }else{
            return ResultVOUtil.error("error:no token");
        }
    }

}
