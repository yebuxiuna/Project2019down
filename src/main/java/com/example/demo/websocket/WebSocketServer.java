package com.example.demo.websocket;

import com.example.demo.msg.*;
import com.example.demo.util.Flag;
import com.example.demo.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/websocket", encoders = {EncoderClassVo.class})
@Component
public class WebSocketServer {

    private Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    private static int onlineCount = 0;
    private static ConcurrentHashMap<String, WebSocketServer> websocketmap = new ConcurrentHashMap<>();
    private static ArrayList<SendInfoMsg> waitting = new ArrayList<>();
    private Session session;
    private int sid = 0;

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public static WebSocketServer getWebSocketServer(String token) {
        return websocketmap.get(token);
    }

    public static void getMeMessage(WebSocketServer webSocketServer,int id){
        try {
            for (SendInfoMsg s:waitting) {
                if(s.getUid() == id){
                    webSocketServer.sendMessage(s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 建立连接时
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        String token = UUID.randomUUID().toString();
        websocketmap.put(token, this);
        addOnlineCount();
        logger.info("有用户连接~~~~当前在线人数：" + onlineCount);
        ReturnMsg msg = new ReturnMsg();
        msg.setCode(1);
        msg.setMsg("token");
        msg.setAction(token);
        try {
            sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接
     */
    @OnClose
    public void onClose() {
        for (Map.Entry<String, WebSocketServer> wss : websocketmap.entrySet()) {
            if (wss.getValue() == this) {
                websocketmap.remove(wss.getKey());
                break;
            }
        }
        cutOnlineCount();
        logger.info("有用户退出~~~~当前在线人数：" + onlineCount);
    }

    /**
     * 客户端发送消息时
     *
     * @param json
     * @param session
     */
    @OnMessage
    public void onMessage(String json, Session session) {
        try {
            SendInfoMsg msg = (SendInfoMsg) JsonUtil.getObject(json, SendInfoMsg.class);
            if(msg.getMsg().equals("send")){
                WebSocketServer webSocketServer = websocketmap.get(msg.getFid());
                SendInfoMsg msg2 = new SendInfoMsg();
                msg2.setContent(msg.getContent());
                //发送出去是 UID变为FID  FID变为UID
                msg2.setUid(msg.getFid());
                msg2.setFid(msg.getUid());
                msg2.setTime(msg.getTime());
                msg2.setMsg("to");
                if(webSocketServer != null){
                    webSocketServer.sendMessage(msg2);
                }else{
                    //接收方不在线则放入等待消息中
                    waitting.add(msg2);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发生错误时
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("出现错误");
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    //todo 将msg类转换为由GSON转换为json格式的字符串
    public void sendMessage(Msg msg) throws IOException {
        try {
            session.getAsyncRemote().sendText(JsonUtil.getJson(msg));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getOnlineCount() {
        return onlineCount;
    }

    private void addOnlineCount() {
        onlineCount++;
    }

    private void cutOnlineCount() {
        onlineCount--;
    }

    /**
     * 向我发送的好友申请
     *
     * @param name 好友昵称
     * @param id   好友id
     */
    public void receiveApply(WebSocketServer wss, String name, int id) {
        AddFMsg addFMsg = new AddFMsg();
        addFMsg.setCode(Flag.Add_Friend);
        addFMsg.setId(id);
        addFMsg.setName(name);
        try {
            wss.sendMessage(addFMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 由我发送出去的好友申请
     *
     * @param wss  当前在线的被申请人
     * @param name 我的昵称
     * @param id   我的id
     */
    public void sendApply(WebSocketServer wss, String name, int id) {
        AddFMsg addFMsg = new AddFMsg();
        addFMsg.setCode(Flag.Add_Friend);
        addFMsg.setId(id);
        addFMsg.setName(name);
        try {
            wss.sendMessage(addFMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
