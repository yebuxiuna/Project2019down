package com.example.demo.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@DynamicUpdate
@Data
public class InvitationInfo {
    @Id
    int id;
    String description;
    int uid;
    int channelId;
}
