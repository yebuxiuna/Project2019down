package com.example.demo.entity;

import lombok.Data;

import javax.persistence.Id;

@Data
public class InvitationImage {
    @Id
    int id;
    String imagePath;
}
