package com.create.chacha.domains.shared.entity.store;

import jakarta.persistence.*;
import lombok.*;

/**
 * 스토어 아이콘 정보 엔티티
 * <p>
 * 각 스토어에서 사용할 수 있는 아이콘(이미지)의 메타데이터를 저장.
 * 아이콘 이름, 설명, 경로를 관리.
 */
@Entity
@Table(name = "store_icon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class StoreIconEntity {

    /**
     * 기본 키 (AUTO_INCREMENT)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 아이콘 이름
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 사용자용 아이콘 설명
     */
    @Column(length = 255)
    private String content;

    /**
     * 아이콘이 저장된 경로(URL)
     */
    @Column(length = 500)
    private String url;
}
