package com.example.demo.msg;

import lombok.Data;

/**
 * 返回客户端的消息
 */
@Data
public class ReturnMsg extends Msg {
    String msg;
    String action;
}
