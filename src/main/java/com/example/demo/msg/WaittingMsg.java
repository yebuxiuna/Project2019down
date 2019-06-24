package com.example.demo.msg;

import lombok.Data;

@Data
public class WaittingMsg {
    Integer uid;
    Integer fid;
    String type;
    String time;
    Object msg;
}
