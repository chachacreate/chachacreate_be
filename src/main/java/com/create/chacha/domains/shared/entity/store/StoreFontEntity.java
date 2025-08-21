package com.create.chacha.domains.shared.entity.store;

import jakarta.persistence.*;
import lombok.*;

/**
 * 스토어 글꼴 정보 엔티티
 * <p>
 * 각 스토어에서 사용할 수 있는 글꼴의 메타데이터를 저장.
 * 글꼴명, 스타일, 경로를 관리.
 */
@Entity
@Table(name = "store_font")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class StoreFontEntity {

    /**
     * 기본 키 (AUTO_INCREMENT)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 글꼴명
     */
    private String name;

    /**
     * 글꼴 스타일 (예: Regular, Bold, Italic 등)
     */
    private String style = "Regular";

    /**
     * 글꼴이 저장된 경로(URL)
     */
    private String url;
}
