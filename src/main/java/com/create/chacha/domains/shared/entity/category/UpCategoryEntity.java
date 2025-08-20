package com.create.chacha.domains.shared.entity.category;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 상위 카테고리 엔티티
 * <p>
 * 상품의 대분류를 관리하는 엔티티입니다.
 * 하나의 상위 카테고리는 여러 하위 카테고리를 가질 수 있습니다.
 * </p>
 *
 * <ul>
 *     <li>{@link #id} - 기본 키 (AUTO_INCREMENT)</li>
 *     <li>{@link #name} - 상위 카테고리 이름</li>
 * </ul>
 */
@Entity
@Table(name = "up_category")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpCategoryEntity {

    /**
     * 기본 키 (AUTO_INCREMENT)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 상위 카테고리 이름
     */
    @Column(nullable = false, length = 100, unique = true)
    private String name;
}
