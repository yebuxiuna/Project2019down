package com.example.demo.dao;

import com.example.demo.entry.ApplyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface ApplyinfoDao extends JpaRepository<ApplyInfo,Integer> {

    @Transactional
    @Modifying
    @Query(value = "select * from user_register where uid = ?1 and fid = ?2",nativeQuery = true)
    ApplyInfo selectApply(int uid, int fid);

    List<ApplyInfo> findAllByFid(int fid);

}
