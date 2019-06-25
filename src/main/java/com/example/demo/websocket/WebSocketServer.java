package com.example.demo.websocket;

import com.example.demo.DemoApplication;
import com.example.demo.msg.AddFMsg;
import com.example.demo.msg.Msg;
import com.example.demo.msg.ReturnMsg;
import com.example.demo.msg.WaittingMsg;
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
    private static ArrayList<WaittingMsg> waitting = new ArrayList<>();
    private Session session;
    private int sid = 0;

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public static WebSocketServer getWebSocketServer(String token) {
        //根据token获取当前机器的websocket
        DemoApplication.logger.info(token);
        return websocketmap.get(token);
    }

    public static void getMeMessage(int id){
        //todo 获取别人在你离线时发送过来的消息
        ArrayList<WaittingMsg> withme = new ArrayList<>();
        for (WaittingMsg w:waitting) {
            if(w.getFid() == id){
                withme.add(w);
            }
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
        msg.setMsg(token);
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
     * @param msg
     * @param session
     */
    @OnMessage
    public void onMessage(String msg, Session session) {
        try {
            Map<String,Object> map = JsonUtil.getMap(msg);

            switch (map.get("type").toString()){
                case "text":

                    break;
                case "img":

                    break;
            }
            ReturnMsg returnMsg = new ReturnMsg();
            sendMessage(returnMsg);
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
