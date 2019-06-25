package com.example.demo.dao;

import com.example.demo.entity.InvitationLikes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationLikesDao extends JpaRepository<InvitationLikes,Integer> {
}
