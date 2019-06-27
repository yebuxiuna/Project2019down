package com.example.demo.msg;

import lombok.Data;

@Data
public class SendInfoMsg extends Msg {
    String msg;
    int uid;
    int fid;
    String content;
    String time;
}
