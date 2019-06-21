package com.example.demo.dao;

import com.example.demo.entry.UserRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface UserinfoDao extends JpaRepository<UserRegister,Integer> {

    @Transactional
    @Modifying
    @Query(value = "update user_register set pwd = ?1 where id = ?2",nativeQuery = true)
    int updatePwd(String pwd, int id);

    @Transactional
    @Modifying
    @Query(value = "update user_register set state = ?1 ,token = ?3 where id = ?2",nativeQuery = true)
    int setState(int state,int id,String token);

    @Modifying
    @Query(value = "select * from user_register where id = ?1 and pwd = ?2",nativeQuery = true)
    UserRegister selectUser(int id,String pwd);

}
