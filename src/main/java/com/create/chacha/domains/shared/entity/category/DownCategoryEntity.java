package com.create.chacha.domains.shared.entity.category;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 하위 카테고리 엔티티
 * <p>
 * 상품의 소분류를 관리하는 엔티티입니다.
 * 각 하위 카테고리는 반드시 하나의 상위 카테고리에 속합니다.
 * </p>
 *
 * <ul>
 *     <li>{@link #id} - 기본 키 (AUTO_INCREMENT)</li>
 *     <li>{@link #name} - 하위 카테고리 이름</li>
 *     <li>{@link #upCategory} - 연결된 상위 카테고리</li>
 * </ul>
 */
@Entity
@Table(name = "down_category")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DownCategoryEntity{

    /**
     * 기본 키 (AUTO_INCREMENT)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 하위 카테고리 이름
     */
    private String name;

    /**
     * 연결된 상위 카테고리
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_category_id", nullable = false)
    private UpCategoryEntity upCategory;
}
