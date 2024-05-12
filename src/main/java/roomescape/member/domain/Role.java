package roomescape.member.domain;

import java.util.Map;

public enum Role {

    USER("user"),
    ADMIN("admin");

    private static final Map<String, Role> CACHE = Map.of(USER.getRoleName(), USER, ADMIN.getRoleName(), ADMIN);

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public static Role of(String roleName) {
        Role role = CACHE.get(roleName);
        if (role == null) {
            throw new IllegalArgumentException("존재하지 않는 Role 입니다.");
        }

        return role;
    }

    public String getRoleName() {
        return roleName;
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }
}