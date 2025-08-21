package com.create.chacha.domains.shared.entity;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass // table은 생성되지 않음, 다른 entity의 부모
@EntityListeners(value = AuditingEntityListener.class) // 변경이 일어나면 자동으로 넣어줌
@Getter
@Setter
@ToString
public class BaseEntity {
    /*
    * 생성 시간
    */
    @CreatedDate
    @Column(updatable = false)
    LocalDateTime createdAt;
    /*
     * 수정 시간
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;
    /*
     * 삭제 시간
     */
    private LocalDateTime deletedAt;

    /*
     * 삭제 여부
     */
    @Column(nullable = false, name = "is_deleted", columnDefinition = "TINYINT")
    private Boolean isDeleted;
}
