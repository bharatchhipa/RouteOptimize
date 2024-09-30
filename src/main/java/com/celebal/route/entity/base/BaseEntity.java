package com.celebal.route.entity.base;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @CreationTimestamp
    @Column(nullable = false)
    protected Timestamp createdOn;

    @UpdateTimestamp
    @Column(nullable = false)
    protected Timestamp modifiedOn;

    protected Boolean isActive = Boolean.TRUE;

}