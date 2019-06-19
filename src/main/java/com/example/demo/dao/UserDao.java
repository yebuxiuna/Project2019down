package com.example.demo.dao;

import com.example.demo.entry.UserPhone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<UserPhone,Integer> {

    UserPhone findByPhone(String phone);

}
