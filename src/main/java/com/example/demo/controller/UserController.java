package com.example.demo.controller;

import com.example.demo.dao.ApplyinfoDao;
import com.example.demo.dao.UserDao;
import com.example.demo.dao.UserinfoDao;
import com.example.demo.entity.ApplyInfo;
import com.example.demo.entity.UserPhone;
import com.example.demo.entity.UserRegister;
import com.example.demo.result.ResultVO;
import com.example.demo.result.ResultVOUtil;
import com.example.demo.websocket.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Data")
public class UserController {

    private static Map<Integer, String> users = new HashMap<>();

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserinfoDao userinfoDao;
    @Autowired
    private ApplyinfoDao applyinfoDao;

    /**
     * 用户注册
     *
     * @param map 用户输入的信息，需要有token,phone，username，pwd，age,sex
     * @return 返回一个json
     */
    @RequestMapping(value = "/Register", method = {RequestMethod.POST})
    public ResultVO register(@RequestBody Map map) {
        if (map.get("token") != null && WebSocketServer.getWebSocketServer(map.get("token").toString()) != null) {
            ResultVO resultVO = new ResultVO();
            UserPhone user = userDao.findByPhone(map.get("phone").toString());
            if (user != null) {
                return ResultVOUtil.error("该用户已存在");
            }
            UserPhone tuser = new UserPhone();
            tuser.setPhone(map.get("phone").toString());
            tuser.setToken(map.get("token").toString());
            UserPhone user2 = userDao.save(tuser);
            resultVO.setMsg("S");
            WebSocketServer wss = WebSocketServer.getWebSocketServer(map.get("token").toString());
            wss.setSid(user2.getId());
            resultVO.setData(user2);
            System.out.print(user2);
            UserRegister info = new UserRegister();
            info.setId(user2.getId());
            info.setUsername(map.get("username").toString());
            info.setPwd(map.get("pwd").toString());
            info.setAge(map.get("age").toString());
            info.setSex(map.get("sex").toString());
            info.setState(1);
            userinfoDao.save(info);
            return resultVO;
        } else {
            return ResultVOUtil.error("error：no token");
        }
    }

    /**
     * 查看有无该用户
     * @param map
     * @return
     */
    @RequestMapping(value = "/Select", method = {RequestMethod.POST})
    public ResultVO selectUser(@RequestBody Map map) {
        if (map.get("token") != null && WebSocketServer.getWebSocketServer(map.get("token").toString()) != null) {
            ResultVO resultVO = new ResultVO();
            UserPhone userPhone = userDao.findByPhone(map.get("phone").toString());
            if (userPhone == null) {
                //该号码未注册
                resultVO.setMsg("T");
            }else{
                //该号码已注册
                resultVO.setMsg("S");
            }
            return resultVO;
        } else {
           return ResultVOUtil.error("error：no token");
        }
    }

    /**
     * 用户登录
     *
     * @param map 需要参数:token,phone,pwd
     * @return
     */
    @RequestMapping(value = "/Login", method = {RequestMethod.POST})
    public ResultVO login(@RequestBody Map map) {
        try {
            if (map.get("token") != null && WebSocketServer.getWebSocketServer(map.get("token").toString()) != null) {
                ResultVO resultVO = new ResultVO();
                UserPhone userPhone = userDao.findByPhone(map.get("phone").toString());
                if (userPhone == null) {
                    throw new Exception("该手机号未注册");
                }
                UserRegister userRegister = userinfoDao.selectUser
                        (userPhone.getId(), map.get("pwd").toString());
                if (userRegister == null) {
                    throw new Exception("手机号或密码错误");
                }
                WebSocketServer wss = WebSocketServer.getWebSocketServer(map.get("token").toString());
                wss.setSid(userRegister.getId());
                //向我发送好友申请的用户id
                ArrayList<Integer> fids = new ArrayList<>();
                List<ApplyInfo> applyInfos = applyinfoDao.findAllByFid(userRegister.getId());
                for (int i = 0; i < applyInfos.size(); i++) {
                    fids.add(applyInfos.get(i).getUid());
                }
                if (fids.size() != 0) {
                    receiveFriend(map, fids);
                }
                resultVO.setMsg("S");
                ArrayList al = new ArrayList();
                al.add(userPhone);
                al.add(userRegister);
                resultVO.setData(al);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UpdateSate(1, userPhone.getId());
                    }
                }).start();
//                map.put(userPhone.getId(), map.get("token"));
                return resultVO;
            } else {
                throw new Exception("error：no token");
            }
        } catch (Exception e) {
            return ResultVOUtil.error(e.getMessage());
        }
    }

    /**
     * 登录时将未在线时收到的申请发送到客户端
     * 通过WebSocket让客户端接收好友申请
     *
     * @param map 装载着用户数据的集合
     * @param ids 用户id
     */
    public void receiveFriend(Map map, ArrayList<Integer> ids) {
        WebSocketServer wss = WebSocketServer.getWebSocketServer(map.get("token").toString());
        for (int i = 0; i < ids.size(); i++) {
            UserRegister userRegister = userinfoDao.findById(ids.get(i)).get();
            wss.receiveApply(wss, userRegister.getUsername(), ids.get(i));
        }
    }

    /**
     * 是否同意添加好友
     * 用户登录的session token
     * 用户是否同意 result
     * 用户自己的id uid
     * 申请人的id fid
     *
     * @param map
     */
    @RequestMapping(value = "/AgreementAdd", method = {RequestMethod.POST})
    public void Succeedfriend(@RequestBody Map map) {
        if (map.get("token") != null) {
            if (map.get("result").equals("yes")) {
                WebSocketServer wss = WebSocketServer.getWebSocketServer(map.get("token").toString());

            }
            ApplyInfo applyInfo = new ApplyInfo();
            applyInfo.setUid((Integer) map.get("fid"));
            applyInfo.setFid((Integer) map.get("uid"));
            applyinfoDao.delete(applyInfo);
        }
    }

    /**
     * 添加好友
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/AddFriend", method = {RequestMethod.POST})
    public ResultVO addFriend(@RequestBody Map map) {
        if (map.get("token") != null) {
            ResultVO resultVO = new ResultVO();
            ApplyInfo applyInfo = applyinfoDao.selectApply((Integer) map.get("uid"), (Integer) map.get("fid"));
            if (applyInfo != null) {
                return ResultVOUtil.error("已发送申请");
            }
            ApplyInfo applyInfo2 = new ApplyInfo();
            applyInfo2.setUid((Integer) map.get("uid"));
            applyInfo2.setFid((Integer) map.get("fid"));
            ApplyInfo applyInfo3 = applyinfoDao.save(applyInfo2);
            UserPhone userPhone = userDao.findById((Integer) map.get("fid")).get();
            UserRegister userRegister = userinfoDao.findById((Integer) map.get("uid")).get();
            if (applyInfo3 != null) {
                if (userPhone.getToken() != null) {
                    WebSocketServer wss = WebSocketServer.getWebSocketServer(userPhone.getToken());
                    wss.sendApply(wss, userRegister.getUsername(), (Integer) map.get("uid"));
                }
                resultVO.setMsg("S");
                resultVO.setData(applyInfo3);
                return resultVO;
            } else {
                return ResultVOUtil.error("请求发送失败");
            }
        }
        return ResultVOUtil.error("error:no token");
    }

    /**
     * 修改登录状态
     */
    public void UpdateSate(int state, int id) {
        userinfoDao.setState(state, id);
    }

    /**
     * 用户退出
     *
     * @param map
     */
    @RequestMapping(value = "/Exit", method = {RequestMethod.POST})
    public void exit(@RequestBody Map map) {
        UpdateSate(0, (Integer) map.get("id"));
    }

    /**
     * 修改密码 or 忘记密码
     *
     * @param map  需要参数： token  pwd  phone
     * @return
     */
    @RequestMapping(value = "/UpdatePwd", method = {RequestMethod.POST})
    public ResultVO updatePwd(@RequestBody Map map) {
        if (map.get("token") != null) {
            UserPhone userPhone = userDao.findByPhone(map.get("phone").toString());
            if(userPhone == null){
                return ResultVOUtil.error("手机号未注册");
            }
            UserRegister userRegister = userinfoDao.findById(userPhone.getId()).get();
            if(map.get("pwd").toString().equals(userRegister.getPwd())){
                return ResultVOUtil.error("请不要使用上一个密码");
            }
            int num = userinfoDao.updatePwd(map.get("pwd").toString(), userPhone.getId());
            ResultVO resultVO = new ResultVO();
            if (num > 0) {
                resultVO.setMsg("S");
                resultVO.setData("修改成功！");
            } else {
                resultVO.setMsg("T");
                resultVO.setData("网络错误，请稍后重试！");
            }
            return resultVO;
        }
        return ResultVOUtil.error("error:no token");
    }

}