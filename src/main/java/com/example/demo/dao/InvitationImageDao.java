package com.example.demo.dao;

import com.example.demo.entity.InvitationImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InvitationImageDao extends JpaRepository<InvitationImage,Integer> {

    @Modifying
    @Query(value = "select * from invitation_image where id = ?1",nativeQuery = true)
    List<InvitationImage> findAllById(int id);

}
