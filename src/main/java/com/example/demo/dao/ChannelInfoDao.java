package com.example.demo.dao;

import com.example.demo.entity.ChannelInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelInfoDao extends JpaRepository<ChannelInfo,Integer> {
}
