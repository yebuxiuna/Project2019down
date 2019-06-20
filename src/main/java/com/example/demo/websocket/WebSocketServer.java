package com.example.demo.websocket;

import com.example.demo.DemoApplication;
import com.example.demo.msg.AddFMsg;
import com.example.demo.msg.Msg;
import com.example.demo.msg.ReturnMsg;
import com.example.demo.util.Flag;
import com.example.demo.util.JsonUtil;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/",encoders = {EncoderClassVo.class})
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
     * 向我发送的好友申请
     * @param name 好友昵称
     * @param id 好友id
     */
    public void receiveApply(String name,int id){
        AddFMsg addFMsg = new AddFMsg();
        addFMsg.setMsg(Flag.Add_Friend);
        addFMsg.setId(id);
        addFMsg.setName(name);
        try {
            sendMessage(addFMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 由我发送出去的好友申请
     * @param wss 当前在线的被申请人
     * @param name 我的昵称
     * @param id 我的id
     */
    public void sendApply(WebSocketServer wss,String name,int id){
        AddFMsg addFMsg = new AddFMsg();
        addFMsg.setMsg(Flag.Add_Friend);
        addFMsg.setId(id);
        addFMsg.setName(name);
        try {
            wss.sendMessage(addFMsg);
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
        System.out.println("有用户连接~~~~当前在线人数："+onlineCount);
        ReturnMsg msg = new ReturnMsg();
        msg.setMsg(1);
        msg.setCode(session.getId());
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
        System.out.println("有用户退出~~~~当前在线人数："+onlineCount);
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

}
