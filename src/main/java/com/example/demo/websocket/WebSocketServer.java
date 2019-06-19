package com.example.demo.websocket;

import com.example.demo.DemoApplication;
import com.example.demo.msg.AddFMsg;
import com.example.demo.msg.Msg;
import com.example.demo.util.JsonChange;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/websocket/{sid}",encoders = {EncoderClassVo.class})
@Component
public class WebSocketServer {

    private static int onlineCount = 0;
    private static CopyOnWriteArraySet<WebSocketServer> websocketset = new CopyOnWriteArraySet<>();
    private Session session;
    private int sid = 0;

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public static WebSocketServer getWebSocketServer(String id){
        WebSocketServer webSocketServer = null;
        for (WebSocketServer wss:websocketset) {
            if(wss.session.getId().equals(id)){
                webSocketServer = wss;
            }
        }
        return webSocketServer;
    }

    /**
     * 发送好友申请
     * @param name 好友昵称
     * @param id 好友id
     */
    public void sendApply(String name,int id){
        AddFMsg addFMsg = new AddFMsg();
        addFMsg.setMsg("添加好友请求");
        addFMsg.setId(id);
        addFMsg.setName(name);
        try {
            sendMessage(addFMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        websocketset.add(this);
        addOnlineCount();
        DemoApplication.logger.info("有用户连接~~~~当前在线人数："+onlineCount);
        Msg msg = new Msg();
        msg.setMsg(session.getId());
        try {
            sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(){
      websocketset.remove(this);
      cutOnlineCount();
      DemoApplication.logger.info("有用户退出~~~~当前在线人数："+onlineCount);
    }

    /**
     * 由一个客户端传送到其他客户端
     * @param msg
     * @param session
     */
    @OnMessage
    public void onMessage(String msg,Session session){
        DemoApplication.logger.info("向客户端发送消息："+msg);
    }

    @OnError
    public void onError(Session session,Throwable error){
        DemoApplication.logger.error("出现错误");
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(Msg msg) throws IOException {
        try {
            this.session.getAsyncRemote().sendObject(JsonChange.ObjToJson(msg));
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

}
