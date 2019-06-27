package com.example.demo.dao;

import com.example.demo.entity.InvitationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InvitationInfoDao extends JpaRepository<InvitationInfo,Integer> {

    @Modifying
    @Query(value = "select * from invitation_info where uid= ?1",nativeQuery = true)
    List<InvitationInfo> findAllById(int id);

}
