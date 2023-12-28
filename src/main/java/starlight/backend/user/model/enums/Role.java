package starlight.backend.user.model.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    TALENT, SPONSOR, ADMIN;

    final String roleName = "ROLE_" + name();

    @Override
    public String getAuthority() {
        return roleName;
    }
}
