package com.example.demo.entity;

import lombok.Data;

import javax.persistence.Id;

@Data
public class InvitationParticulars {
    @Id
    int id;
    String particulars;
}
