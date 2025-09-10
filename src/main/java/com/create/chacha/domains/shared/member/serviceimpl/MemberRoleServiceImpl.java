package com.create.chacha.domains.shared.member.serviceimpl;

import com.create.chacha.domains.shared.constants.MemberRoleEnum;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import com.create.chacha.domains.shared.member.dto.response.TokenResponseDTO;
import com.create.chacha.domains.shared.member.exception.InvalidMemberRoleException;
import com.create.chacha.domains.shared.member.exception.InvalidRequestException;
import com.create.chacha.domains.shared.member.exception.MemberNotFoundException;
import com.create.chacha.domains.shared.member.exception.MemberRoleUpdateException;
import com.create.chacha.domains.shared.member.service.MemberLoginService;
import com.create.chacha.domains.shared.member.service.MemberRoleService;
import com.create.chacha.domains.shared.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberRoleServiceImpl implements MemberRoleService {

    private final MemberRepository memberRepository;
    private final MemberLoginService memberLoginService;

    @Override
    @Transactional
    public MemberEntity updateMemberRole(Long memberId, MemberRoleEnum memberRole) {
        log.info("회원 권한 업데이트 요청 - 회원ID: {}, 권한: {}", memberId, memberRole);

        try {
            // 입력값 검증
            if (memberId == null) {
                throw new InvalidRequestException("회원 ID가 없습니다.");
            }

            if (memberRole == null) {
                throw new InvalidMemberRoleException("회원 권한이 없습니다.");
            }

            // 회원 존재 여부 확인
            MemberEntity member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다. ID: " + memberId));

            // 이전 권한 저장 (로깅용)
            MemberRoleEnum previousRole = member.getMemberRole();

            // 권한 업데이트
            member.setMemberRole(memberRole);
            MemberEntity updatedMember = memberRepository.save(member);

            log.info("회원 권한 업데이트 완료 - 회원ID: {}, 이전 권한: {}, 새 권한: {}",
                    memberId, previousRole, memberRole);

            return updatedMember;

        } catch (DataAccessException e) {
            log.error("회원 권한 업데이트 중 데이터베이스 오류 발생: {}", e.getMessage());
            throw new MemberRoleUpdateException("회원 권한 업데이트 중 데이터베이스 오류가 발생했습니다.");
        } catch (MemberNotFoundException | InvalidRequestException | InvalidMemberRoleException e) {
            // 이미 정의된 커스텀 예외들은 그대로 다시 던짐
            throw e;
        } catch (Exception e) {
            log.error("회원 권한 업데이트 중 예상치 못한 오류: {}", e.getMessage(), e);
            throw new MemberRoleUpdateException("회원 권한 업데이트 중 오류가 발생했습니다.");
        }
    }

    @Override
    public MemberEntity getMemberRole(Long memberId) {
        log.info("회원 권한 조회 요청 - 회원ID: {}", memberId);

        try {
            // 입력값 검증
            if (memberId == null) {
                throw new InvalidRequestException("회원 ID가 없습니다.");
            }

            MemberEntity member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다. ID: " + memberId));

            log.info("회원 권한 조회 완료 - 회원ID: {}, 권한: {}", memberId, member.getMemberRole());
            return member;

        } catch (DataAccessException e) {
            log.error("회원 권한 조회 중 데이터베이스 오류 발생: {}", e.getMessage());
            throw new MemberRoleUpdateException("회원 권한 조회 중 데이터베이스 오류가 발생했습니다.");
        } catch (MemberNotFoundException | InvalidRequestException e) {
            // 이미 정의된 커스텀 예외들은 그대로 다시 던짐
            throw e;
        } catch (Exception e) {
            log.error("회원 권한 조회 중 예상치 못한 오류: {}", e.getMessage(), e);
            throw new MemberRoleUpdateException("회원 권한 조회 중 오류가 발생했습니다.");
        }
    }

    @Override
    @Transactional
    public TokenResponseDTO updateMemberRoleWithToken(Long memberId, MemberRoleEnum memberRole) {
        // 1. 권한 업데이트
        MemberEntity updatedMember = updateMemberRole(memberId, memberRole);
        String email = updatedMember.getEmail();

        return memberLoginService.regenerateTokensForUser(email);
    }
}
