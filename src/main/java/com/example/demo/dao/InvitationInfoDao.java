package com.example.demo.dao;

import com.example.demo.entity.InvitationInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationInfoDao extends JpaRepository<InvitationInfo,Integer> {
}
