package com.example.demo.entry;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@DynamicUpdate
@Data
public class UserPhone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String phone;
    String token;
}
