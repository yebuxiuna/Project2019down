package com.example.demo.msg;

import com.example.demo.entity.InvitationImage;
import com.example.demo.entity.InvitationInfo;
import lombok.Data;

import java.util.List;

@Data
public class InvitationMsg extends Msg {
    InvitationInfo info;
    List<InvitationImage> invitationImages;
}
