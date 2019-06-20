package com.example.demo.websocket;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class EncoderClassVo implements Encoder.Text<String> {


    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }


    @Override
    public String encode(String s) throws EncodeException {
        return null;
    }
}
