package com.raito.zf_demo.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

/**
 * @author raito
 * @since 2024/09/05
 */
@Data
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("主键")
    private Long id;

    @CreationTimestamp
    @Comment("创建时间")
    @Column(columnDefinition = "DATETIME(6)")
    private Date createTime;

    @UpdateTimestamp
    @Comment("更新时间")
    @Column(columnDefinition = "DATETIME(6)")
    private Date updateTime;

}