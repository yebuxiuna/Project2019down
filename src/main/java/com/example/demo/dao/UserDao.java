package com.example.demo.dao;

import com.example.demo.entity.UserPhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface UserDao extends JpaRepository<UserPhone,Integer> {

    UserPhone findByPhone(String phone);

    @Transactional
    @Modifying
    @Query(value = "update user_phone set token = ?1 where id = ?2",nativeQuery = true)
    int updateToken(String token,String id);
}
