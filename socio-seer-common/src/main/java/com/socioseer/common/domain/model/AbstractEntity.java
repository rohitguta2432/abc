package com.socioseer.common.domain.model;


import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AbstractEntity implements Serializable {
    @Indexed
    @CreatedDate
    protected Long createdDate;
    @Indexed
    @CreatedBy
    protected String createdBy;
    @Indexed
    @LastModifiedDate
    protected Long updatedDate;
    @Indexed
    @LastModifiedBy
    protected String updatedBy;
}
