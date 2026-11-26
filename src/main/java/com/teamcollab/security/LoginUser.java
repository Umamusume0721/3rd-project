package com.teamcollab.security;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class LoginUser {

    private final Long userId;
    private final String account;
    private final String nickname;
    private final List<String> roleCodes;
    private final Set<String> permCodes;
    private final boolean superAdmin;

    public LoginUser(Long userId, String account, String nickname,
                     List<String> roleCodes, Set<String> permCodes, boolean superAdmin) {
        this.userId = userId;
        this.account = account;
        this.nickname = nickname;
        this.roleCodes = roleCodes == null ? Collections.emptyList() : roleCodes;
        this.permCodes = permCodes == null ? Collections.emptySet() : permCodes;
        this.superAdmin = superAdmin;
    }

    public boolean hasPermission(String code) {
        return superAdmin || permCodes.contains(code);
    }

    public boolean hasRole(String roleCode) {
        return roleCodes.contains(roleCode);
    }

    public Long getUserId() {
        return userId;
    }

    public String getAccount() {
        return account;
    }

    public String getNickname() {
        return nickname;
    }

    public List<String> getRoleCodes() {
        return roleCodes;
    }

    public Set<String> getPermCodes() {
        return permCodes;
    }

    public boolean isSuperAdmin() {
        return superAdmin;
    }
}
