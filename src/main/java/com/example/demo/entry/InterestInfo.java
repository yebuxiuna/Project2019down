package com.example.demo.entry;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@DynamicUpdate
@Data
public class InterestInfo {
    @Id
    int id;
    String name;
    String icon_name;
    int channel_id;
}
