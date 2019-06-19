package com.example.demo.websocket;

import com.example.demo.msg.Msg;
import com.example.demo.util.JsonChange;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class EncoderClassVo implements Encoder.Text<Msg> {


    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public String encode(Msg msg) throws EncodeException {
        try {
            return JsonChange.ObjToJson(msg);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
