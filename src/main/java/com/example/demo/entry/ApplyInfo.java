package com.example.demo.entry;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;

@Entity
@DynamicUpdate
@Data
public class ApplyInfo {
    int uid;
    int fid;
}
