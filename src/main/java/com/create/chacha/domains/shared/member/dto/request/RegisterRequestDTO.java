package com.create.chacha.domains.shared.member.dto.request;

import com.create.chacha.domains.shared.entity.member.MemberAddressEntity;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {
    private MemberEntity member;
    private MemberAddressEntity memberAddress;
}

