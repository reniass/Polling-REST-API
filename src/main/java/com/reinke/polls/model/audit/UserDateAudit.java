package com.reinke.polls.model.audit;


import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class UserDateAudit extends DateAudit{

    @CreatedBy
    private Long createdBy;

    @LastModifiedBy
    private Long updatedBy;
}
