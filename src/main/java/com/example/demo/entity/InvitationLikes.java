package com.example.demo.entity;

import lombok.Data;

import javax.persistence.Id;

@Data
public class InvitationLikes {
    @Id
    int id;
    int Likes;
}
