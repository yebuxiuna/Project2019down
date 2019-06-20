package com.example.demo.msg;

import lombok.Data;

/**
 * 发送好友申请
 */
@Data
public class AddFMsg extends Msg {
    //好友昵称
    String name;
    //好友用户id
    int id;

}
