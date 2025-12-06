package com.whu.pet.vo;

import lombok.Data;

/**
 * 登录响应VO
 */
@Data
public class LoginVO {

    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String role;
    private String token;
}
