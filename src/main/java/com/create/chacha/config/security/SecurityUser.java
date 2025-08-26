package com.create.chacha.config.security;

import com.create.chacha.domains.shared.constants.MemberRoleEnum;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Setter
public class SecurityUser extends User {
    private static final long serialVersionUID = 1L;
    private static final String ROLE_PREFIX="ROLE_";

    // MemberEntity 정보를 저장할 필드 추가
    private final MemberEntity memberEntity;

    //기본제공 생성자
    public SecurityUser(String name, String password, Collection<? extends GrantedAuthority> authorities) {
        super(name, password, authorities);
        this.memberEntity = null; // 기본 생성자에서는 null
        log.info("*****생성자에서 출력 member:" + name);
    }
    //개발자가 추가한 코드
    public SecurityUser(MemberEntity member) {
        super(member.getEmail(), member.getPassword(), makeRole(member) );
        this.memberEntity = member; // MemberEntity 저장
        log.info("*****MemberEntity member:" + member);
    }
    //Role을 여러개 가질수 있도록 되어있음
    private static List<GrantedAuthority> makeRole(MemberEntity member) {
        List<GrantedAuthority> roleList = new ArrayList<>();
        roleList.add(new SimpleGrantedAuthority(ROLE_PREFIX + member.getMemberRole()));
        return roleList;
    }
    //User class에서 username필드가 있지만 google 인증시 사용되는 필드는 name 이를 맞추기위해 함수 추가함
    public String getName() {
        return super.getUsername();
    }
    /**
     * 회원 ID 반환
     * @return Integer 회원 ID
     */
    public Long getMemberId() {
        if (memberEntity == null) {
            log.warn("MemberEntity가 null입니다. 기본 생성자로 생성된 SecurityUser일 수 있습니다.");
            return null;
        }
        return memberEntity.getId(); // MemberEntity의 ID 필드명에 맞게 수정 필요
    }

    /**
     * 회원 권한 반환
     * @return MemberRoleEnum 회원 권한
     */
    public MemberRoleEnum getMemberRole() {
        if (memberEntity == null) {
            log.warn("MemberEntity가 null입니다. 기본 생성자로 생성된 SecurityUser일 수 있습니다.");
            return null;
        }
        return memberEntity.getMemberRole();
    }

    /**
     * MemberEntity 전체 반환 (필요시 사용)
     * @return MemberEntity 회원 엔티티
     */
    public MemberEntity getMemberEntity() {
        return memberEntity;
    }

    /**
     * 이메일 반환 (편의 메서드)
     * @return String 이메일
     */
    public String getEmail() {
        return super.getUsername(); // Spring Security에서 username이 email로 사용됨
    }
    /**
     * 특정 권한 보유 여부 확인
     * @param role 확인할 권한 (ROLE_ 접두사 제외)
     * @return boolean 권한 보유 시 true
     */
    public boolean hasRole(String role) {
        return getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(ROLE_PREFIX + role));
    }
}
