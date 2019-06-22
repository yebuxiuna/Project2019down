package com.example.demo.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@DynamicUpdate
@Data
public class UserRegister {
    @Id
    int id;
    String username;
    String pwd;
    String email;
    int state;

}
