package org.redrock.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Component
@ServerEndpoint("/websocket/{username}")
public class WebSocket {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static int onlineNumber = 0;
    private static Map<String, WebSocket> clients = new ConcurrentHashMap<String, WebSocket>();
    private Session session;
    private String username;

    @OnOpen
    public void onOpen(@PathParam("username") String username, Session session)
    {
        onlineNumber++;
        logger.info("现在来连接的客户id："+session.getId()+"用户名："+username);
        this.username = username;
        this.session = session;
        logger.info("有新连接加入！ 当前在线人数" + onlineNumber);
        try {
            Map<String,Object> map1 = new HashMap<String,Object>();
            map1.put("messageType","上线");
            map1.put("username",username);
            sendMessageAll(JSON.toJSONString(map1),username);

            clients.put(username, this);

            Map<String,Object> map2  = new HashMap<String,Object>();
            map2.put("messageType","在线名单");

            Set<String> set = clients.keySet();
            map2.put("onlineUsers",set);
            sendMessageTo(JSON.toJSONString(map2),username);
        }
        catch (IOException e){
            logger.info(username+"上线的时候通知所有人发生了错误");
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        logger.info("服务端发生了错误"+error.getMessage());
    }

    @OnClose
    public void onClose()
    {
        onlineNumber--;
        clients.remove(username);
        try {
            Map<String,Object> map1 = new HashMap<String,Object>();
            map1.put("messageType","下线");
            map1.put("onlineUsers",clients.keySet());
            map1.put("username",username);
            sendMessageAll(JSON.toJSONString(map1),username);
        }
        catch (IOException e){
            logger.info(username+"下线的时候通知所有人发生了错误");
        }
        logger.info("有连接关闭！ 当前在线人数" + onlineNumber);
    }

    @OnMessage
    public void onMessage(String message, Session session)
    {
        try {
            logger.info("来自客户端消息：" + message+"客户端的id是："+session.getId());
            JSONObject jsonObject = JSON.parseObject(message);
            String textMessage = jsonObject.getString("message");
            String fromUsername = jsonObject.getString("username");
            String toUsername = jsonObject.getString("to");


            Map<String,Object> map1 = new HashMap<String,Object>();
            map1.put("messageType","普通消息");
            map1.put("textMessage",textMessage);
            map1.put("fromUsername",fromUsername);
            if("All".equals(toUsername)){
                map1.put("toUsername","所有人");
                sendMessageAll(JSON.toJSONString(map1),fromUsername);
            }
            else{
                map1.put("toUsername",toUsername);
                sendMessageTo(JSON.toJSONString(map1),toUsername);
            }
        }
        catch (Exception e){
            logger.info("发生了错误了");
        }
    }

    public void sendMessageTo(String message, String toUserName) throws IOException {
        for (WebSocket item : clients.values()) {
            if (item.username.equals(toUserName) ) {
                item.session.getAsyncRemote().sendText(message);
                break;
            }
        }
    }

    public void sendMessageAll(String message,String fromUserName) throws IOException {
        for (WebSocket item : clients.values()) {
            item.session.getAsyncRemote().sendText(message);
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineNumber;
    }

}
