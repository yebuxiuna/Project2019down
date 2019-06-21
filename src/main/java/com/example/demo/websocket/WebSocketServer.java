package com.example.demo.websocket;

import com.example.demo.msg.AddFMsg;
import com.example.demo.msg.Msg;
import com.example.demo.msg.ReturnMsg;
import com.example.demo.util.Flag;
import com.example.demo.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/websocket", encoders = {EncoderClassVo.class})
@Component
public class WebSocketServer {

    private Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    private static int onlineCount = 0;
    private static ConcurrentHashMap<String, WebSocketServer> websocketmap = new ConcurrentHashMap<>();
    private Session session;
    private int sid = 0;

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public static WebSocketServer getWebSocketServer(String id) {
        return websocketmap.get(id);
    }

    Timer timer;

    /**
     * 建立连接时
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        String token = System.currentTimeMillis() + "";
        websocketmap.put(token, this);
        addOnlineCount();
        timer = new Timer();
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
        timer.cancel();
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
        logger.info("向客户端发送消息：" + msg);
        ReturnMsg returnMsg = new ReturnMsg();
        returnMsg.setCode(Flag.CONNECT_SUCCEED);
        returnMsg.setMsg("收到，over");
        try {
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
        timer.cancel();
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
