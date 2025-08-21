package com.create.chacha.domains.shared.entity.member;

import com.create.chacha.domains.shared.constants.ReportStatusEnum;
import com.create.chacha.domains.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 신고 정보 엔티티
 * <p>
 * BaseEntity를 상속받아 생성/수정/삭제 시간을 자동 관리
 */
@Entity
@Table(name = "report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class ReportEntity extends BaseEntity {

    /**
     * 기본 키 (AUTO_INCREMENT)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 신고 당한 회원 (피신고자)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_id", nullable = false)
    private MemberEntity reportedMember;

    /**
     * 신고한 회원 (신고자)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private MemberEntity reportMember;

    /**
     * 신고 내용
     */
    private String content;

    /**
     * 신고 처리 상태
     * <p>0: 미처리, 1: 처리 완료</p>
     */
    @Column(columnDefinition = "TINYINT")
    private ReportStatusEnum status = ReportStatusEnum.UNPROCESSED;
}
