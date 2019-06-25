package com.example.demo.entity;

import lombok.Data;

import javax.persistence.Id;

@Data
public class InvitationInfo {
    @Id
    int id;
    String description;
    int uid;
    int channelId;
}
