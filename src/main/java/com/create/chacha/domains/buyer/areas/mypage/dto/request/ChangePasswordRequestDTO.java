package com.create.chacha.domains.buyer.areas.mypage.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ChangePasswordRequestDTO {
    private String currentPassword;
    private String newPassword;
    private String newPasswordConfirm;
}
